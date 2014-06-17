package cn.bluetel.interphone;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * 设置对讲方式 : 单播 组播 广播
 * @author zhugg
 *
 */
public class CommSetting extends PreferenceActivity{

	private static String broadcastIP;
	private static String multicastIP;
	private static String unicastIP;
	private static int castType;
	private static int port;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings_comm);
	}

	public static void refreshValues(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Resources res = context.getResources();
		castType = Integer.parseInt(preferences.getString("cast_type", res.getStringArray(R.array.cast_types_values)[0]));
		port = Integer.parseInt(preferences.getString("port", res.getString(R.string.port_default)));
		broadcastIP = preferences.getString("broadcast_addr", res.getString(R.string.broadcast_addr_default));
		multicastIP = preferences.getString("multicast_addr", res.getString(R.string.multicast_addr_default));
		unicastIP = preferences.getString("unicast_addr", res.getString(R.string.unicast_addr_default));
//			a = InetAddress.getByName(preferences.getString("broadcast_addr", res.getString(R.string.broadcast_addr_default)));
//			b = InetAddress.getByName(preferences.getString("multicast_addr", res.getString(R.string.multicast_addr_default)));
//			c = InetAddress.getByName(preferences.getString("unicast_addr", res.getString(R.string.unicast_addr_default)));
		Log.i("pref", "castType = " + castType + "  port = " + port + "    broadcastIP = " + broadcastIP + "     multiIP = " + multicastIP + "    uniIP = " + unicastIP);

	}
	
	public static int getCastType() {
		return castType;
	}
	
	public static int getPort() {
		return port;
	}
	
	public static String getBroadcastIP() {
		return broadcastIP;
	}
	
	public static String getMulticastIP() {
		return multicastIP;
	}
	
	public static String getUnicastIP() {
		return unicastIP;
	}
}
