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

	ServerSocket socket;
	public static ArrayList<Socket> clients;
	ArrayList<InputStream> ins;
	ArrayList<OutputStream> outs;

	Socket robot;
	InputStream robotIn;
	OutputStream robotOut;
	
	private AudioSendThread audioSend = null;
	private AudioDecodeThread audioDecode = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server);
		init();
	}
	
	public void onDestroy() {
		if( audioSend != null ) audioSend.shutdown();
		if( audioDecode != null ) audioDecode.shutdown();
		super.onDestroy();
	}

	public void init() {
		initHolder();
		//connectToRobot();
		clientAcceptThread.start();
		/* HACK HACK HACK */
		//boolean isServer = true;
		//audioSend = new AudioSendThread(isServer);
		//audioDecode = new AudioDecodeThread();
		//relayThread.start();
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
		public void run() {
			byte[] buffer = new byte[1024];
			int readBytes = 0;
			try {
				while (true) {
					for (InputStream in : ins) {
						// if (in.available() > 0) {
		
						readBytes = in.read(buffer);
						Log.i(TAG, "Server relayed "+ readBytes);
						robotOut.write(buffer, 0, readBytes);
						// }
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	public Thread clientAcceptThread = new Thread() {
		public void run() {
			try {
				socket = new ServerSocket(ModeSelect.port);
				clients = new ArrayList<Socket>();
				outs = new ArrayList<OutputStream>();
				ins = new ArrayList<InputStream>();
				while (true) {
					Socket client = socket.accept();
					clients.add(client);
					outs.add(client.getOutputStream());
					ins.add(client.getInputStream());
				}
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

}
