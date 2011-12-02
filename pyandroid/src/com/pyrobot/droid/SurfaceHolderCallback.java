package com.pyrobot.droid;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.SurfaceHolder;

public class SurfaceHolderCallback implements SurfaceHolder.Callback{

	public Camera mCamera;
	public SurfaceHolder holder;
	
	public SurfaceHolderCallback(SurfaceHolder holder) {
		this.holder = holder;
	}
	public void initCamera(){
		mCamera = Camera.open();
	    Parameters params = mCamera.getParameters();
	    params.setFlashMode(Parameters.FLASH_MODE_ON);
	    mCamera.setParameters(params);

		try {
			mCamera.setPreviewDisplay(holder);
		} catch (Exception e) {
			Log.e("exception", e.toString());
			return;
		}
		mCamera.startPreview();
	}


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		initCamera();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mCamera.stopPreview();
		mCamera.release();
	}
}
