package com.pyrobot.droid;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class Joystick extends View {

	public final static String X = "x";
	public final static String Y = "y";
	protected static String STOPPED = "JoystickStopped";

	private Handler handler = null;
	Timer messageTimer = null;
	private float STROKE_WIDTH = 7;
	public float thumbStickX = -1;
	public float thumbStickY = -1;

	public float centerX = -1;
	public float centerY = -1;
	public float radius = -1;
	public float thumbstickRadius = 25;

	public Joystick(Context context) {
		super(context);
		init();
	}

	public Joystick(Context context, AttributeSet set) {
		super(context, set);
		init();
	}

	public Joystick(Context context, AttributeSet set, int defStyle) {
		super(context, set, defStyle);
		init();
	}

	public void init() {
		// this.setBackgroundColor(Color.argb(255, 0, 255, 255));
		this.setPadding(50, 0, 0, 0);
		centerX = this.getWidth() / 2;
		centerY = this.getHeight() / 2;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
		initTimer();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		centerX = this.getWidth() / 2;
		centerY = this.getHeight() / 2;
		radius = centerY - thumbstickRadius;
		
		Paint circlePaint = new Paint();
		circlePaint.setColor(Color.GRAY);
		circlePaint.setStyle(Paint.Style.STROKE);
		circlePaint.setStrokeWidth(STROKE_WIDTH);
		canvas.drawCircle(centerX, centerY, radius, circlePaint);

		if (thumbStickX >= 0 && thumbStickY >= 0) {
			Paint thumbStickPaint = new Paint();
			thumbStickPaint.setColor(Color.RED);
			thumbStickPaint.setStyle(Paint.Style.FILL);
			canvas.drawCircle(thumbStickX, thumbStickY, thumbstickRadius, thumbStickPaint);
		} else {
			Paint thumbStickPaint = new Paint();
			thumbStickPaint.setColor(Color.RED);
			thumbStickPaint.setStyle(Paint.Style.FILL);
			canvas.drawCircle(centerX, centerY, thumbstickRadius, thumbStickPaint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		super.onTouchEvent(ev);
		int action = ev.getAction() & MotionEvent.ACTION_MASK;

		float x = ev.getX();
		float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_POINTER_DOWN:
		case MotionEvent.ACTION_DOWN: {
			thumbStickX = x;
			thumbStickY = y;
			invalidate();
			return true;
		}
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_UP: {
			thumbStickX = -1;
			thumbStickY = -1;

			invalidate();
			
			if(handler == null) return true;
			Message msg = new Message();
			Bundle data = new Bundle();
			data.putBoolean(Joystick.STOPPED, true);
			msg.setData(data);
			handler.sendMessage(msg);
			
			return true;
		}
		}
		return false;
	}

	private void initTimer() {
		int delay = 250;
		messageTimer = new Timer();
		messageTimer.schedule(new JMessageTask(handler, this), 0, delay);
	}
}

class JMessageTask extends TimerTask {
	private Joystick joystick = null;
	private Handler handler = null;

	public JMessageTask(Handler handler, Joystick joystick) {
		this.joystick = joystick;
		this.handler = handler;
	}

	private void sendValues() {
		if (handler == null
				|| (joystick.thumbStickX < 0 && joystick.thumbStickY < 0))
			return;
		Message msg = new Message();
		Bundle data = new Bundle();
		float x = (joystick.thumbStickX - joystick.centerX) / joystick.radius;
		float y = (joystick.centerY - joystick.thumbStickY) / joystick.radius;
		data.putFloat(Joystick.X, x);
		data.putFloat(Joystick.Y, y);
		msg.setData(data);
		handler.sendMessage(msg);
	}

	@Override
	public void run() {
		sendValues();
	}
}