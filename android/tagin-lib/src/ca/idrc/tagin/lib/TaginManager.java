package ca.idrc.tagin.lib;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tagin.Tagin;

import android.content.Context;
import android.content.Intent;

public class TaginManager {
	
	private Context mContext;
	private static Tagin mTagin;

	public TaginManager(Context context) {
		Tagin.Builder builder = new Tagin.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
		mTagin = builder.build();
		mContext = context;
	}
	
	public void apiRequest(String request) {
		Intent intent = new Intent(mContext, TaginService.class);
		intent.putExtra(TaginService.EXTRA_TYPE, request);
		mContext.startService(intent);
	}
	
	public static Tagin getService() {
		return mTagin;
	}
}
