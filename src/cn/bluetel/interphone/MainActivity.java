package cn.bluetel.interphone;

import java.util.ArrayList;
import java.util.List;

import org.lansir.ip.codecs.Speex;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import cn.bluetel.interphone.adapter.ConfigInfoAdapter;
import cn.bluetel.interphone.util.CommUtils;
import cn.bluetel.interphone.util.DialogUtils;
import cn.bluetel.interphone.util.PrefUtils;
import cn.bluetel.interphone.util.VoicePlayer;
import cn.bluetel.interphone.util.VoiceSender;

public class MainActivity extends Activity implements OnTouchListener {
	
	private static String TAG = "Interphone";
	
	private VoiceSender mVoiceSender;
	private VoicePlayer mVoicePlayer;

	private ListView listView;
	private ImageView btnTalk;
	
	private List<String> mInfos;
	private ConfigInfoAdapter mConfigInfoAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		PreferenceManager.setDefaultValues(this, R.xml.settings_audio, false);
		PreferenceManager.setDefaultValues(this, R.xml.settings_comm, false);
		
		AudioSetting.refreshValues(this);
		CommSetting.refreshValues(this);
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		listView = (ListView) findViewById(R.id.list);
		btnTalk = (ImageView) findViewById(R.id.talk);
		btnTalk.setOnTouchListener(this);
		
		mInfos = new ArrayList<String>();
		mConfigInfoAdapter = new ConfigInfoAdapter(this, mInfos);
		listView.setAdapter(mConfigInfoAdapter);
	}
	
	private void refreshConfigInfo() {
		List<String> temp = new ArrayList<String>();
		temp.add(CommUtils.getLocalIP());
		switch (CommSetting.getCastType()) {
		case 0:
			temp.add("广播");
			temp.add(CommSetting.getBroadcastIP());
			break;
		case 1:
			temp.add("组播");
			temp.add(CommSetting.getMulticastIP());
			break;
		case 2:
			temp.add("单播");
			temp.add(CommSetting.getUnicastIP());
			break;
		}
		temp.add(CommSetting.getPort() + "");
		temp.add((AudioSetting.isUseSpeex()? "Speex编解码" : "系统编解码"));
		temp.add(AudioSetting.isEcho()? "启用" : "禁止");
		
		mInfos.clear();
		mInfos.addAll(temp);
		mConfigInfoAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		System.out.println("==================" + AudioSetting.getSpeexQualityValue());
		Speex.open(AudioSetting.getSpeexQualityValue());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refreshConfigInfo();
		startRecv();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		stopRecv();
		Speex.close();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		mPlayer.stopPlay();
	}
	
	private void startRecv() {
		stopRecv();
		mVoicePlayer = new VoicePlayer();
		mVoicePlayer.start();
	}
	
	private void stopRecv() {
		if(mVoicePlayer != null) {
			mVoicePlayer.setIsStopped();
			mVoicePlayer = null;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings_comm:
			startActivityForResult(new Intent(this, CommSetting.class), 100);
			break;
		case R.id.settings_audio:
			startActivityForResult(new Intent(this, AudioSetting.class), 100);
			break;
		case R.id.settings_reset_all:
			PrefUtils.clear(this);
			PreferenceManager.setDefaultValues(this, R.xml.settings_audio, true);
			PreferenceManager.setDefaultValues(this, R.xml.settings_comm, true);
			refreshConfigInfo();
			Toast.makeText(this, "所有设置都已恢复到默认值", Toast.LENGTH_LONG).show();
			break;
		case R.id.settings_localip:
			DialogUtils.show(this, getString(R.string.localip_label), CommUtils.getLocalIP());
			break;
		case R.id.program_help:
			DialogUtils.show(this, R.string.menu_help, R.string.menu_help_content);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		AudioSetting.refreshValues(this);
		CommSetting.refreshValues(this);
		Log.i(TAG, "onActivityResult()...");
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			btnTalk.setBackgroundResource(R.drawable.microphone_p);
			if(mVoiceSender != null) {
				mVoiceSender.setIsStopped(true);
			}
			mVoiceSender = new VoiceSender();
			mVoiceSender.start();
			break;
		case MotionEvent.ACTION_UP:
			btnTalk.setBackgroundResource(R.drawable.microphone_n);
			if(mVoiceSender != null) {
				mVoiceSender.setIsStopped(true);
			}
			break;
		}
		return true;
	}
}
