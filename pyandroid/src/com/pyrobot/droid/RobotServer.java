package com.pyrobot.droid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class RobotServer extends Activity {

	protected static final String TAG = "RobotServer";
	private SurfaceView mSurfaceView;
	private SurfaceHolder holder;
	private Camera mCamera;
	private VideoDecodeThread vdt;

	public static ArrayList<Socket> clients;

	Socket robot = null;
	InputStream robotIn;
	OutputStream robotOut;		
	ServerSocket socket;


	private AudioSendThread audioSend = null;
	private AudioDecodeThread audioDecode = null;
	private Object audioLock = new Object();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server);
		init();
	}

	public void onDestroy() {
		if (audioSend != null)
			audioSend.shutdown();
		if (audioDecode != null)
			audioDecode.shutdown();
		relayThread.destroy();
		clientAcceptThread.destroy();
		super.onDestroy();
	}

	public void init() {
		initHolder();
		connectToRobot();
		clientAcceptThread.start();
		/* HACK HACK HACK */
		boolean isServer = true;
		audioSend = new AudioSendThread(isServer);
		// audioDecode = new AudioDecodeThread();
		// audioDecode.setSendThread(audioSend);
		relayThread.start();
	}

	public void connectToRobot() {
		try {
			robot = new Socket(ModeSelect.hostname, ModeSelect.port);
			robotOut = robot.getOutputStream();
			robotIn = robot.getInputStream();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initHolder() {
		mSurfaceView = (SurfaceView) findViewById(R.id.video_surface_view);
		SurfaceHolder holder = mSurfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		SurfaceHolderCallback callback = new SurfaceHolderCallback(holder);
		holder.addCallback(callback);
	}

	public Thread relayThread = new Thread() {
		private boolean alive;

		@Override 
		public void destroy() {
			alive = false;
		}
		
		public void run() {
			alive = true;
			byte[] buffer = new byte[1024];
			int readBytes = 0;
			while (alive) {
				for (Socket client : clients) {
					try {
						if (client.isConnected()) {
							InputStream in = client.getInputStream();
							readBytes = in.read(buffer);
							if (readBytes < 0) {
								Log.i(TAG, "Client removed");
								client.close();
								clients.remove(client);
								robotOut.write("{'command':'stop'}".getBytes());
								continue;
							}
							Log.i(TAG, "Server relayed " + readBytes);
							robotOut.write(buffer, 0, readBytes);
							robotOut.flush();
						} else {
							Log.i(TAG, "Client removed");
							client.close();
							clients.remove(client);
							robotOut.write("{'command':'stop'}".getBytes());
						}
					} catch (IOException e) {
						e.printStackTrace();
						Log.i(TAG, "Exception: Client removed");
						Log.e(TAG, e.toString());
						clients.remove(client);
						try {
							client.close();
							robotOut.write("{'command':'stop'}".getBytes());
						} catch (Exception e2) {
						}
						
						e.printStackTrace();
					}
				}
			}

			if (robot != null) {
				try {
					robotOut.flush();
					robotOut.close();
					robot.close();
					for (Socket client : clients) {
						client.close();
					}
				} catch (Exception gofuckyourself) {
				}
			}
		}
	};

	public Thread clientAcceptThread = new Thread() {
		private boolean alive; 
		
		@Override
		public void destroy() {
			alive = false;
		}
		
		public void run() {
			alive = true;
			try {
				socket = new ServerSocket(ModeSelect.port);
				clients = new ArrayList<Socket>();
				while (alive) {
					Socket client = socket.accept();
					clients.add(client);
				}
				socket.close();
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

}
