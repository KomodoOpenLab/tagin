package ca.idrc.tagin.cloud.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;

public class Persistence {
	
	private WifiManager mWifiManager;
	private ConnectivityManager mConnectivityManager;
	
	public Persistence(Context context) {
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	public boolean isWifiEnabled() {
		return mWifiManager.isWifiEnabled();
	}

	public boolean isMobileConnected() {
		State state = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		return state == NetworkInfo.State.CONNECTED;
	}

	public boolean isWifiConnected() {
		State state = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		return state == NetworkInfo.State.CONNECTED;
	}

	public boolean isConnected() {
		return isMobileConnected() || isWifiConnected();
	}

}
