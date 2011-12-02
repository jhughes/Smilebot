package com.pyrobot.droid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.hardware.Camera;

public class RobotServer extends Activity  {

	private SurfaceView mSurfaceView;	
	private SurfaceHolder holder;
	private Camera mCamera;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server);
		init();
	}
	
	public void init(){
		initHolder();
	}
	
	public void initHolder(){
		mSurfaceView = (SurfaceView) findViewById(R.id.video_surface_view);
		SurfaceHolder holder = mSurfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		SurfaceHolderCallback callback = new SurfaceHolderCallback(holder);
		holder.addCallback(callback);
	}

}
