package ca.idrc.tagin.cloud.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class Persistence {
	
	private ConnectivityManager mConnectivityManager;
	
	public Persistence(Context context) {
		mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
