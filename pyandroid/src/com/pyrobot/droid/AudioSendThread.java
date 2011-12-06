package com.pyrobot.droid;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioSendThread extends Thread {

	public static final int PORT = ModeSelect.port + 9991;
	private int audioBufferSize;
	private AudioRecord audioRecord;
	private byte[] audioBuffer;
	private boolean alive;
	private boolean isServer = false;
	private String TAG = "AudioSendThread";
	private boolean shouldSendAudio = false;
	private Object lock = new Object();

	public AudioSendThread(boolean isServer) {
		this.isServer = isServer;
		initAudioRecord();
		alive = true;
		this.start();
	}
	
	public AudioSendThread() {
		initAudioRecord();
		alive = true;
		this.isServer = false;
		this.start();
	}

	public void shutdown() {
		alive = false;
		if( audioRecord != null) {
			audioRecord.release();
		}
	}

	public void sendAudio() {
		synchronized (lock) {
			if(shouldSendAudio) return; // Already sending audio
			shouldSendAudio = true;
			audioRecord.startRecording();
			Log.i(TAG, "Sending audio..");
		}
	}
	public void stopSendingAudio() {
		synchronized (lock) {
			if(!shouldSendAudio) return; // Already not sending audio
			shouldSendAudio = false;
			audioRecord.stop();
			Log.i(TAG, "Stopping audio sending...");
		}
	}
		
	public void initAudioRecord() {

		try {
			int frequency = 11025;
			int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
			int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
			audioBufferSize = AudioRecord.getMinBufferSize(frequency,
					channelConfiguration, audioEncoding);
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
					frequency, channelConfiguration, audioEncoding,
					audioBufferSize);
			audioBuffer = new byte[audioBufferSize];
			// audioRecord = findAudioRecord();
			if( audioRecord != null) {
//				audioRecord.getState();
				if( isServer) {
					audioRecord.startRecording();
					shouldSendAudio = true;
				}
				Log.i(TAG, "Audio recording enabled..");
			} else {
				Log.e(TAG, "Audio record is null");
			}
			Log.i(TAG , "audio buffer size: " + audioBufferSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getHostname() throws Exception {
		if( !isServer) {
			return ModeSelect.hostname;
		} else {
			int size = 0;
			if( RobotServer.clients != null) {
				size = RobotServer.clients.size();
			} else {
				Log.i(TAG, "No clients");
				throw new Exception();
			}
			if( size <= 0) {
				Log.i(TAG, "No client.. trying later");
				throw new Exception();
			}
			Socket client = RobotServer.clients.get(0);
			Log.i(TAG, "No client.. trying later");
			return client.getInetAddress().getHostName();
		}
	}
	

	public void run() {
		int size = 0;
		String hostname = null;
		while(alive) {
			try {
				hostname = getHostname();
				Socket socket = new Socket(hostname, AudioSendThread.PORT);
				OutputStream os = socket.getOutputStream();
				Log.i(TAG, "Got output stream");
				BufferedOutputStream bos = new BufferedOutputStream(os);
				DataOutputStream dos = new DataOutputStream(bos);
				while (alive) {
					synchronized (lock ) {
						if( shouldSendAudio || isServer ) {
							int bufferReadResult = audioRecord.read(audioBuffer, 0,
									audioBufferSize);
							Log.i(TAG, "read " + bufferReadResult + " bytes of audio");
							dos.write(audioBuffer);
							dos.flush();
						}
					}
				}
				dos.close();
			}
			catch (Exception e) {
				Log.e(TAG, "Maybe " + hostname + ":" + AudioSendThread.PORT + " is not up..?");
				e.printStackTrace();
				try {
					// Check to see in 500ms to see if the server is up
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					Log.e(TAG, "Welcome to try/catch Java hell");
					e1.printStackTrace();
				}
			}
		}
	}
}