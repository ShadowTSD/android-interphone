package cn.bluetel.interphone.util;

import java.util.LinkedList;

import android.text.TextUtils;
import android.util.Log;

public class VoiceFilter {

	private static LinkedList<String> mFilterList;
	
	public static void refreshFilterList() {
		String localIP = CommUtils.getLocalIP();
		if(!TextUtils.isEmpty(localIP)) {
			if(mFilterList == null) {
				mFilterList = new LinkedList<String>();
			} else {
				mFilterList.clear();
			}
			mFilterList.add(localIP);
		}
	}
	
	public static boolean isInFilterList(String ipStr) {
		if(TextUtils.isEmpty(ipStr)) {
			return false;
		}
		Log.i("Filter", "这个IP是否在过滤列表中" + ipStr);
		return mFilterList.contains(ipStr);
	}
}
