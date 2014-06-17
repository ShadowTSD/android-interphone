package cn.bluetel.interphone;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import cn.bluetel.interphone.R;

public class AudioSetting extends PreferenceActivity{

	private static boolean isUseSpeex;
	private static int speexQuality;
	private static boolean isEcho;
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings_audio);
	}
	
	public static void refreshValues(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		isUseSpeex = preferences.getBoolean("use_speex", true);
		speexQuality = Integer.parseInt(preferences.getString("speex_quality", context.getResources().getStringArray(R.array.speex_quality_values)[0]));
		isEcho = preferences.getBoolean("echo", false);
		
		Log.i("pref", "isUseSpeex = " + isUseSpeex + "  SpeexQuality = " + speexQuality + "    isEcho = " + isEcho);
	}
	
	public static boolean isUseSpeex() {
		return isUseSpeex;
	}
	
	public static int getSpeexQualityValue() {
		return speexQuality;
	}
	
	public static boolean isEcho() {
		return isEcho;
	}
}
