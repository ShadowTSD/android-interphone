package cn.bluetel.interphone.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.lansir.ip.codecs.Speex;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Process;
import android.util.Log;
import cn.bluetel.interphone.AudioSetting;
import cn.bluetel.interphone.CommSetting;

public class VoicePlayer extends Thread {
	
	private MulticastSocket mMulticastSocket;
	private DatagramSocket mDatagramSocket;
	
	private DatagramPacket mPacket;
	private InetAddress mInetAddress;
	private AudioTrack mAudioTrack;
	
	private short[] shortBuffer = new short[' '];
	private byte[] byteBuffer;
	
	private Object obj = new Object();
	private boolean isSuspended;
	private boolean isStopped;
	
	public void initAudioTrack() {
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, 2, AudioFormat.ENCODING_PCM_16BIT, /*Config.AUDIO_TRACK_BUFFER*/24576, AudioTrack.MODE_STREAM);
		mAudioTrack.play();
	}

	/**
	 * 其实这里只需要初始化个组播MultiSocket就可以了<p>
	 * 因为：组播可以接收所有广播了.
	 * @throws Exception
	 */
	private void initSocket() throws Exception{
		switch (CommSetting.getCastType()) {
		case 0: // broadcat
			mInetAddress = InetAddress.getByName(CommSetting.getBroadcastIP());
			mDatagramSocket = new DatagramSocket(CommSetting.getPort());
			break;
		case 1: // multi
			mInetAddress = InetAddress.getByName(CommSetting.getMulticastIP());
			mMulticastSocket = new MulticastSocket(CommSetting.getPort());
			mMulticastSocket.joinGroup(mInetAddress);
			mDatagramSocket = mMulticastSocket;
			break;
		case 2: // unique
			mInetAddress = InetAddress.getByName(CommSetting.getUnicastIP());
			mDatagramSocket = new DatagramSocket(CommSetting.getPort());
			break;
		}
		mDatagramSocket.setSoTimeout(0);
	}
	
	private void releaseSocket() {
		if(mMulticastSocket != null) {
			try {
				mMulticastSocket.leaveGroup(mInetAddress);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mMulticastSocket = null;
		}
		if(mDatagramSocket != null) {
			mDatagramSocket.close();
			mDatagramSocket = null;
		}
	}
	
	private void releaseAudioTrack() {
		if(mAudioTrack != null) {
			if(mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
				mAudioTrack.stop();
			}
			mAudioTrack.release();
			mAudioTrack = null;
		}
	}
	
	@Override
	public void run() {
		super.run();
		Log.i("Player", "Player run()...Start");
		// 
		Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
		
		// 初始化AudioTrack
		initAudioTrack();
		try {
			// 初始化Socket
			initSocket();
		} catch (Exception e) {
			Log.i("Player", "初始化Socket失败！");
			e.printStackTrace();
			return;
		}
		
		// 刷新过滤列表, 本机IP可能改变
		VoiceFilter.refreshFilterList();
		
		if(AudioSetting.isUseSpeex()) {
			byteBuffer = new byte[Speex.getCompressionValue(AudioSetting.getSpeexQualityValue())];
		} else {
			byteBuffer = new byte[320];
		}
		mPacket = new DatagramPacket(byteBuffer, byteBuffer.length);
		
		while(!isStopped()) {
			try {
				mDatagramSocket.receive(mPacket);	// IOException
				
				// 暂停线程
				if(isSuspended) {
					synchronized (obj) {
						try {
							obj.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
				if(!AudioSetting.isEcho() && VoiceFilter.isInFilterList(mPacket.getAddress().getHostAddress())) {
					Log.i("Player", "该段语音过滤掉了");
					continue;
				}
				
			} catch (IOException e) {
				Log.i("Player", "Socket receive()...Error. 退出while循环.");
				e.printStackTrace();
				break;
			}
			
			if(AudioSetting.isUseSpeex()) {
				Speex.decode(byteBuffer, byteBuffer.length, shortBuffer);
				mAudioTrack.write(shortBuffer, 0, 160);
			} else {
				mAudioTrack.write(byteBuffer, 0, 320);
			}
		}
		
		releaseSocket();
		releaseAudioTrack();
		Log.i("Player", "Player run()...End");
	}
	
	public boolean isStopped() {
		return isStopped;
	}
	
	public void setIsStopped() {
		isStopped = true;
		// Socket异常退出
		releaseSocket();
	}
	
	public boolean isSuspended() {
		return isSuspended;
	}
	
	public void setIsSuspended(boolean isSuspend) {
		this.isSuspended = isSuspend;
		if(!isSuspended) {
			synchronized (obj) {
				// 恢复线程
				obj.notify();
			}
		}
	}
}
