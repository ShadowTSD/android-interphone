package cn.bluetel.interphone.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtils {

	public static class PrefKey {
		/** 是否使用Speex进行编解码 */
		public static String use_speex = "use_speex";
		/** Speex压缩质量 */
		public static String speex_quality = "speex_quality";
		/** 是否启用回放 */
		public static String use_echo = "echo";
		
		/** 广播类型  */
		public static String cast_type = "cast_type";
		/** 端口 */
		public static String port = "port";
		/** 广播地址 */
		public static String broadcast_addr = "broadcast_addr";
		/** 组播地址 */
		public static String multicast_addr = "multicast_addr";
		/** 单播地址 */
		public static String unicast_addr = "unicast_addr";
	}
	
	public static int getInt(Context context, String key, int defValue) {
		return getSharedPreference(context).getInt(key, defValue);
	}
	
	public static String getString(Context context, String key, String defValue) {
		return getSharedPreference(context).getString(key, defValue);
	}
	
	public static void clear(Context context) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit();
	}
	
	private static SharedPreferences getSharedPreference(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
