package com.pyrobot.droid;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

public class MainMenu extends Activity {

	private static final String robotHostName = "192.168.1.10";
	private static final Integer robotHostPort = 5000;

	private Socket robotSocket;
	private OutputStream robotOutputStream;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		connectToBot();
		setContentView(R.layout.main);
		Button moveForwardButton = (Button) findViewById(R.id.moveForward);
		moveForwardButton.setOnTouchListener(new OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		            moveForward();
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		            stop();
		        }
		        return false;
		    }
		});
		Button moveBackwardButton = (Button) findViewById(R.id.moveBackward);
		moveBackwardButton.setOnTouchListener(new OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	moveBackward();
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
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
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
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	moveRight();
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		            stop();
		        }
		        return false;
		    }
		});
	}

	public void launchControllerActivity(View v) {
		Toast.makeText(MainMenu.this, "Controller launched", Toast.LENGTH_LONG)
				.show();

	}

	private void alert(String message) {
		Toast.makeText(MainMenu.this, message, Toast.LENGTH_SHORT).show();
	}
	
	public void stop(){
		Instructions instructions = new Instructions();
		instructions.setVelocity(0);
		sendMessage(instructions.toString());
	}
	
	public void moveForward(){
		Instructions instructions = new Instructions();
		instructions.setVelocity(200);
		sendMessage(instructions.toString());
	}
	
	public void moveBackward(){
		Instructions instructions = new Instructions();
		instructions.setVelocity(-200);
		instructions.setIgnoreBump(true);
		instructions.setIgnoreCliff(true);
		sendMessage(instructions.toString());
	}

	public void moveLeft(){
		Instructions instructions = new Instructions();
		instructions.setRadius(1);
		sendMessage(instructions.toString());
	}

	public void moveRight(){
		Instructions instructions = new Instructions();
		instructions.setRadius(-1);
		sendMessage(instructions.toString());
	}

	private void connectToBot() {
		try {
			robotSocket = new Socket(robotHostName, robotHostPort);
			robotOutputStream = robotSocket.getOutputStream();
			alert("Robot connected");
		} catch (UnknownHostException e) {
			alert(e.toString());
		} catch (IOException e) {
			alert(e.toString());
		}
	}

	public byte[] intToByteArray(int value){
		return new byte[] {
				(byte) value,
				(byte) (value >>> 8),
				(byte) (value >>> 16),
				(byte) (value >>> 24)
		};
	}
	
	private void sendMessage(String message) {
		try {
			int size = message.length();
			robotOutputStream.write(intToByteArray(size));
			robotOutputStream.write(message.getBytes());
			robotOutputStream.flush();
		} catch (IOException e) {
			alert(e.toString());
		}
	}
}