package com.starbaby.diyBook.utils;
/**
 * 检测网络连接类
 */
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class checkNetUtils {
	public static boolean checkNet(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	public static boolean checkWifi(Context context){//检测是否连接wifi
		ConnectivityManager connManager = (ConnectivityManager)context. getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (mWifi.isConnected()) {
			return true;
		}
		return false;
	}
}
