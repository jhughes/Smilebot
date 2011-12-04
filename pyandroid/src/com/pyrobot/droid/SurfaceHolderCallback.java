package com.pyrobot.droid;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.SurfaceHolder;

public class SurfaceHolderCallback implements SurfaceHolder.Callback{

	public Camera mCamera;
	public SurfaceHolder holder;
	private int width = 600;
	private int height = 480;
	
	public SurfaceHolderCallback(SurfaceHolder holder) {
		this.holder = holder;
	}
	public void initCamera(){
		mCamera = Camera.open();
	    Parameters params = mCamera.getParameters();
	    //params.setPreviewSize(width, height);
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
	
	Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			try{
				YuvImage yuvi = new YuvImage(data, ImageFormat.NV21, width, height, null);
				Rect rect = new Rect(0,0,width,height);

				BOutputStream bos = new BOutputStream();
				yuvi.compressToJpeg(rect, 70, bos);
				//bos.send_udp(serverIP, videoPort);
			} catch (Exception e) {
				// TODO: handle exception
			};
		}
	};
}
