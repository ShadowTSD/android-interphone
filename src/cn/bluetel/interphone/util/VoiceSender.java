package cn.bluetel.interphone.util;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.lansir.ip.codecs.Speex;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Process;
import android.util.Log;
import cn.bluetel.interphone.AudioSetting;
import cn.bluetel.interphone.CommSetting;

public class VoiceSender extends Thread{
	
	private static final String TAG = "Send";

	private DatagramSocket mDatagramSocket;
	private MulticastSocket mMultiSocket;
	private DatagramPacket mPacket;
	private InetAddress mInetAddress;
	
	private AudioRecord mAudioRecord;
	private short[] mShortBuffer = new short[' '];
	private byte[] mByteBuffer;
	
	private boolean isStopped;
	
	private void initAudioRecord() {
		mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, /*AudioFormat.CHANNEL_IN_STEREO*/2, AudioFormat.ENCODING_PCM_16BIT, Config.AUDIO_RECORD_BUFFER);
		mAudioRecord.startRecording();
	}
	
	private void initSocket() throws Exception{
		switch (CommSetting.getCastType()) {
		case 0:// broadcast
			mDatagramSocket = new DatagramSocket();
			mInetAddress = InetAddress.getByName(CommSetting.getBroadcastIP());
			break;
		case 1:// multicast
			mMultiSocket = new MulticastSocket();
			mInetAddress = InetAddress.getByName(CommSetting.getMulticastIP());
			mDatagramSocket = mMultiSocket;
			break;
		case 2:// unicast
			mDatagramSocket = new DatagramSocket();
			mInetAddress = InetAddress.getByName(CommSetting.getUnicastIP());
			break;
		}
		mDatagramSocket.setSoTimeout(0);
	}
	
	private void releaseSocket() {
		if(mMultiSocket != null) {
			mMultiSocket = null;
		}
		
		if(mDatagramSocket != null) {
			mDatagramSocket.close();
			mDatagramSocket = null;
		}
	}
	
	private void releaseAudioRecord() {
		if(mAudioRecord != null) {
			if(mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
				mAudioRecord.startRecording();
			}
			mAudioRecord.release();
			mAudioRecord = null;
		}
	}
	
	@Override
	public void run() {
		super.run();
		Log.i(TAG, "run() =====>>> Begin");
		// Set Thread Priority
		Process.setThreadPriority(-19);
		// Useless
		if(isStopped()) {
			return;
		}
		
		try {
			// Initialize AudioRecord
			initAudioRecord();
		} catch (Exception e) {
			Log.e(TAG, "Initialize AudioRecord Failed!");
			e.printStackTrace();
			return;
		}
		
		try {
			// Initialize Socket
			initSocket();
		} catch (Exception e) {
			Log.e(TAG, "Initialize Socket Failed!");
			e.printStackTrace();
			return;
		}
		
		if(AudioSetting.isUseSpeex()) {
			mByteBuffer = new byte[Speex.getCompressionValue(AudioSetting.getSpeexQualityValue())];	
		} else {
			mByteBuffer = new byte[320];
		}
		mPacket = new DatagramPacket(mByteBuffer, mByteBuffer.length, mInetAddress, CommSetting.getPort());
		
		while(!isStopped()) {
			 if(AudioSetting.isUseSpeex()) {
				mAudioRecord.read(mShortBuffer, 0, 160);		
				Speex.encode(mShortBuffer, mByteBuffer);
			} else {
				mAudioRecord.read(mByteBuffer, 0, 320);		
			}
			
			if (isStopped()) {
				break;
			}
			try {
				mDatagramSocket.send(mPacket);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		releaseSocket();
		releaseAudioRecord();
		Log.i(TAG, "run() =====>>> End");
	}
	
	public synchronized boolean isStopped() {
		return isStopped;
	}
	
	public synchronized void setIsStopped(boolean isStop) {
		this.isStopped = isStop;
	}
}
