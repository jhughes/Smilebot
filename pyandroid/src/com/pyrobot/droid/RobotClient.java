package com.pyrobot.droid;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class RobotClient extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.client);
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
	private void moveForward(){}
	private void kill(){}
	private void moveLeft(){}
	private void moveRight(){}
	private void stop(){}
}
