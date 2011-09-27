package com.pyrobot.droid;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
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
		Instructions instructions = new Instructions();
		setContentView(R.layout.main);
	}

	public void launchControllerActivity(View v) {
		Toast.makeText(MainMenu.this, "Controller launched", Toast.LENGTH_LONG)
				.show();

	}

	private void alert(String message) {
		Toast.makeText(MainMenu.this, message, Toast.LENGTH_SHORT).show();
	}
	
	
	public void moveForward( View v ){
		Instructions instructions = new Instructions();
		instructions.setDistance(400);
		instructions.setVelocity(200);
		sendMessage(instructions.toString());
	}
	
	public void moveBackward( View v ){
		Instructions instructions = new Instructions();
		instructions.setDistance(400);
		instructions.setVelocity(-200);
		instructions.setIgnoreBump(true);
		instructions.setIgnoreCliff(true);
		sendMessage(instructions.toString());
	}

	public void moveLeft( View v){
		Instructions instructions = new Instructions();
		instructions.setAngle(75);
		instructions.setRadius(1);
		sendMessage(instructions.toString());
	}
	public void stop( View v) {
		Instructions instructions = new Instructions();
		instructions.setVelocity(0);
		sendMessage(instructions.toString());
	}

	public void moveRight( View v){
		Instructions instructions = new Instructions();
		instructions.setAngle(75);
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