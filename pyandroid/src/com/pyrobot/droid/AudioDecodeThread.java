package com.pyrobot.droid;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class AudioDecodeThread extends Thread {

	private String TAG = "AudioDecodeThread";
	private int audioBufferSize = 2048;
	private boolean alive = false;
	private ServerSocket servSock = null;
	private byte[] audioBuffer = null;
	private AudioTrack audioTrack = null;
	private Object audioLock = null;

	public AudioDecodeThread() {
		alive = true;
		audioBuffer = new byte[audioBufferSize];
		initPlay();
		this.start();
	}
	
	public void shutdown() {
		alive = false;
		if( audioTrack != null) audioTrack.stop();
	}

	private void initPlay() {
		audioTrack  = new AudioTrack(AudioManager.STREAM_MUSIC,
				11025, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, audioBufferSize,
				AudioTrack.MODE_STREAM);
		Log.i(TAG, "AudioTrack initialized");
	}

	public void run() {
		Socket client = null;
		InputStream is = null;
		BufferedInputStream bis = null;
		int size;
		try {
			servSock = new ServerSocket(AudioSendThread.PORT);
			Log.i(TAG, "Started audio server");
			client = servSock.accept();
			is = client.getInputStream();
			bis = new BufferedInputStream(is);
			size = bis.read(audioBuffer);
			if( size > 0)
				audioTrack.write(audioBuffer, 0, size);
			audioTrack.play();
			Log.i(TAG, "Received connection, started playing...");
			while (alive) {
				size = bis.read(audioBuffer);
				Log.i(TAG, "Read " + size + " bytes" );
				if( size > 0) {
					audioTrack.write(audioBuffer, 0, size);
				} else {
					alive = false;
					Log.e(TAG, "Something wrong with the stream..?");
				}
			}
			if (client != null) {
				client.close();
				servSock.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
