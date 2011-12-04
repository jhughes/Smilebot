package com.pyrobot.droid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class RobotClient extends Activity {
	Socket server;
	InputStream in;
	OutputStream out;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.client);
		init();
	}

	public void init() {
		initializeButtons();
		connectToServer();
	}
	
	public void connectToServer() {
		try {
			server = new Socket(ModeSelect.hostname, ModeSelect.port);
			out = server.getOutputStream();
			in = server.getInputStream();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// Button listeners below

	private void initializeButtons() {
		Button moveForwardButton = (Button) findViewById(R.id.moveForward);
		moveForwardButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					moveForward();
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					stop();
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
		Button killButton = (Button) findViewById(R.id.kill);
		killButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					kill();
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
	
	private void moveForward(){
		sendCommand("forward");
	}
	private void moveLeft(){
		sendCommand("left");
	}
	private void moveRight(){
		sendCommand("right");
	}
	private void stop(){
		sendCommand("stop");
	}
	private void kill(){
		sendMessage("shutdown");
	}
}
