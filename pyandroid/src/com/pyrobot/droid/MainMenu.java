package com.pyrobot.droid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainMenu extends Activity {

	private static String robotHostName = "192.168.1.14";
	private static Integer robotHostPort = 5000;

	private Socket robotSocket;
	private OutputStream robotOutputStream;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button connectButton = (Button) findViewById(R.id.connect);
		connectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	EditText ip_view = (EditText) findViewById(R.id.ip_edit);
        		EditText port_view = (EditText) findViewById(R.id.port_edit);
                
            	connectToBot(ip_view.getText().toString(),port_view.getText().toString());

        		initializeButtons();
        		startHeartbeat();
            }
        });
	}

	public void launchControllerActivity(View v) {
		Toast.makeText(MainMenu.this, "Controller launched", Toast.LENGTH_LONG)
				.show();

	}

	private void startHeartbeat() {
		Runnable heartbeat = new Runnable() {
			public void run() {
				try {
					Socket heartbeatSocket = new Socket(robotHostName,
							robotHostPort + 1);
					OutputStream out = heartbeatSocket.getOutputStream();
					InputStream in = heartbeatSocket.getInputStream();
					byte[] message = new byte[4];
					String ping = "ping";
					String pong = "pong";
					System.out.println("heartbeat GO");
					while (true) {
						if (in.read(message) == -1)
							break;
						if (ping.equals(new String(message).toLowerCase())) {
							System.out.println("ping");
							out.write(pong.getBytes());
							out.flush();
							System.out.println("pong");
						}
					}
				} catch (UnknownHostException e) {
					alert(e.toString());
				} catch (IOException e) {
					alert(e.toString());
				}
			}
		};
		Thread heartbeatThread = new Thread(heartbeat);
		heartbeatThread.start();
	}

	private void alert(String message) {
		Toast.makeText(MainMenu.this, message, Toast.LENGTH_SHORT).show();
	}

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
		Button moveBackwardButton = (Button) findViewById(R.id.moveBackward);
		moveBackwardButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
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
	
	public void kill() {
		sendMessage("SHUTDOWN");
	}

	public void stop() {
		Instructions instructions = new Instructions();
		instructions.setVelocity(0);
		sendMessage(instructions.toString());
	}

	public void moveForward() {
		Instructions instructions = new Instructions();
		instructions.setVelocity(500);
		sendMessage(instructions.toString());
	}

	public void moveBackward() {
		Instructions instructions = new Instructions();
		instructions.setVelocity(-500);
		instructions.setIgnoreBump(true);
		instructions.setIgnoreCliff(true);
		sendMessage(instructions.toString());
	}

	public void moveLeft() {
		Instructions instructions = new Instructions();
		instructions.setRadius(1);
		sendMessage(instructions.toString());
	}

	public void moveRight() {
		Instructions instructions = new Instructions();
		instructions.setRadius(-1);
		sendMessage(instructions.toString());
	}

	private void connectToBot(String hostname, String port) {
		try {
			robotSocket = new Socket(hostname, Integer.parseInt(port));
			robotOutputStream = robotSocket.getOutputStream();
			alert("Robot connected");
			robotHostName = hostname;
			robotHostPort = Integer.parseInt(port); 
		} catch (UnknownHostException e) {
			alert(e.toString());
		} catch (IOException e) {
			alert(e.toString());
		}
	}

	public byte[] intToByteArray(int value) {
		return new byte[] { (byte) value, (byte) (value >>> 8),
				(byte) (value >>> 16), (byte) (value >>> 24) };
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