package com.pyrobot.droid;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ClientSurface extends View {
	public final static String TYPE = "ClientSurface";
	public final static String LEFT_X_KEY = "leftX";
	public final static String LEFT_Y_KEY = "leftY";
	public final static String LEFT_Y_RATIO_KEY = "leftYRatio";
	public final static String RIGHT_X_KEY = "rightX";
	public final static String RIGHT_Y_KEY = "rightY";
	public final static String RIGHT_Y_RATIO_KEY = "rightYRatio";
	public final static String STOPPED = "stopped";
	
	
	private String TAG = "ClientSurface";
	public float leftX = -1;
	public float leftY = -1;
	private float lastLeftX = -1;
	private float lastLeftY = -1;
	
	public float rightX = -1;
	public float rightY = -1;
	
	private float lastRightX = -1;
	private float lastRightY = -1;
	
	private Handler handler = null;
	Timer messageTimer = null;

	public ClientSurface(Context context) {
		super(context);
	}

	public ClientSurface(Context context, AttributeSet set) {
		super(context, set);
	}

	public ClientSurface(Context context, AttributeSet set, int defStyle) {
		super(context, set, defStyle);
	}
	
	public void setHandler( Handler handler ) {
		this.handler = handler;
		initTimer();
	}
	
	public void removeHandler() {
		this.handler = null;
	}
	
	private void initTimer() {
		int delay = 250;
		messageTimer = new Timer();
		messageTimer.schedule(new MessageTimer(handler, this), 0, delay);
//		Log.i(TAG, "Timer initialized");
	}
	
	private void setValues( float x0, float y0, float x1, float y1) {
		
		lastLeftX = leftX;
		lastLeftY = leftY;
		lastRightX = rightX;
		lastRightY = rightY;
		
		if( x0 < x1 ) {
			leftX = x0;
			leftY = y0;
			rightX = x1;
			rightY = y1;
		} else {
			rightX = x0;
			rightY = y0;
			leftX = x1;
			leftY = y1;
		}
//		sendValues(leftX, leftY, rightX, rightY);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		super.onTouchEvent(ev);
		int action = ev.getAction() & MotionEvent.ACTION_MASK;

		if (ev.getPointerCount() >= 2) {

			float x0 = ev.getX(0);
			float y0 = ev.getY(0);
			float x1 = ev.getX(1);
			float y1 = ev.getY(1);
			
			switch (action) {
			case MotionEvent.ACTION_MOVE:
			case MotionEvent.ACTION_POINTER_DOWN:
			case MotionEvent.ACTION_DOWN: {
				setValues(x0, y0, x1, y1);
				break;
			}
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_UP: {
				clearValues();
				break;
			}
			default: {
				Log.i(TAG, "...wtf? " + ev.toString());
				break;
			}
			}
		}
		return true;
	}

	private void clearValues() {
		leftX = -1;
		leftY = -1;
		lastLeftX = -1;
		lastLeftY = -1;
		rightX = -1;
		rightY = -1;
		lastRightX = -1;
		lastRightY = -1;
		
		if(handler == null) return;
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putBoolean(ClientSurface.STOPPED, true);
		msg.setData(data);
		handler.sendMessage(msg);
	}
}

class MessageTimer extends TimerTask {
	private ClientSurface surface = null;
	private Handler handler = null;
	public MessageTimer(Handler handler, ClientSurface surface) {
		this.surface = surface;
		this.handler = handler;
	}

	private void sendValues() {
		if( handler == null) return;
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putBoolean(ClientSurface.TYPE, true);
		float leftYRatio = surface.leftY / surface.getHeight();
		float rightYRatio = surface.rightY / surface.getHeight();
		data.putFloat(ClientSurface.LEFT_Y_RATIO_KEY, leftYRatio);
		data.putFloat(ClientSurface.RIGHT_Y_RATIO_KEY, rightYRatio);
		msg.setData(data);
		handler.sendMessage(msg);
	}
	
	@Override
	public void run() {
//		Log.i("Timer", "Timer fired");
		if( surface.leftY < 0 || surface.rightY < 0) return;
		sendValues();
	}
}