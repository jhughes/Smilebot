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
		try {
			servSock = new ServerSocket(AudioSendThread.PORT);
			Log.i(TAG, "Started audio server");
			client = servSock.accept();
			is = client.getInputStream();
			bis = new BufferedInputStream(is);
			audioTrack.play();
			while (alive) {
				bis.read(audioBuffer);
				audioTrack.write(audioBuffer, 0, audioBufferSize);
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
