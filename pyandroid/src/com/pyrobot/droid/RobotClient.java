package com.pyrobot.droid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class RobotClient extends Activity {
	Socket server;
	InputStream in;
	OutputStream out;

	private static final String TAG = "RobotClient";
	private VideoDecodeThread vdt = null;
	private AudioDecodeThread adt = null;
	private AudioSendThread audioSend = null;
	private Object lock = new Object();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.client);
		init();
	}

	public void onDestroy() {
		if (adt != null)
			adt.shutdown();
		if (audioSend != null)
			audioSend.shutdown();
		if ( vdt != null)
			vdt.shutdown();
		super.onDestroy();
	}

	public void init() {
		// initializeButtons();
		vdt = new VideoDecodeThread(handler);
		//adt = new AudioDecodeThread();
		//audioSend = new AudioSendThread();
		connectToServer();
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			byte[] packet = b.getByteArray("packet");
			ImageView v = (ImageView) findViewById(R.id.cameraView);
			Bitmap bm = BitmapFactory.decodeByteArray(packet, 12,
					packet.length - 12);
			synchronized (lock) {
				v.setImageBitmap(bm);
				v.invalidate();
			}
		}
	};

	public void connectToServer() {
		try {
			server = new Socket(ModeSelect.hostname, ModeSelect.port);
			out = server.getOutputStream();
			in = server.getInputStream();
			Toast.makeText(getApplicationContext(), ModeSelect.hostname,
					Toast.LENGTH_SHORT).show();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Button listeners below

	private void initializeButtons() {
		ImageView preview = (ImageView) findViewById(R.id.cameraView);
		preview.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.i(TAG, Integer.toString(event.getAction()));
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					moveForward();
					// Toast.makeText(getApplicationContext(), "DOWN",
					// Toast.LENGTH_SHORT).show();
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					stop();
					// Toast.makeText(getApplicationContext(), "UP",
					// Toast.LENGTH_SHORT).show();
					return true;
				}
				return false;
			}
		});

		Button moveLeftButton = (Button) findViewById(R.id.moveLeft);
		moveLeftButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					moveLeft();
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					stop();
				}
				return false;
			}
		});

		Button moveRightButton = (Button) findViewById(R.id.moveRight);
		moveRightButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					moveRight();
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					stop();
				}
				return false;
			}
		});
	}

	private void sendCommand(String command) {
		Instructions instructions = new Instructions();
		instructions.setCommand('"' + command + '"');
		sendMessage(instructions.toString());
	}

	private void sendMessage(String message) {
		try {
			out.write(message.getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void moveForward() {
		Log.i(TAG, "Move Forward");
		sendCommand("forward");
	}

	private void moveLeft() {
		sendCommand("left");
	}

	private void moveRight() {
		sendCommand("right");
	}

	private void stop() {
		sendCommand("stop");
	}

	private void kill() {
		sendMessage("shutdown");
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			audioSend.sendAudio();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			audioSend.stopSendingAudio();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
}