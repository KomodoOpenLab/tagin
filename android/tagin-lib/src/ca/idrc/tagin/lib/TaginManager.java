package ca.idrc.tagin.lib;

import android.content.Context;
import android.content.Intent;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tagin.Tagin;

public class TaginManager {
	
	private Context mContext;
	private static Tagin mTagin;
	
	public static final String TAG = "tagin-lib";

	public TaginManager(Context context) {
		Tagin.Builder builder = new Tagin.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
		mTagin = builder.build();
		mContext = context;
	}
	
	public void apiRequest(String request) {
		apiRequest(request, null, null);
	}
	
	public void apiRequest(String request, String param) {
		apiRequest(request, param, null);
	}
	
	public void apiRequest(String request, String param1, String param2) {
		Intent intent = new Intent(mContext, TaginService.class);
		intent.putExtra(TaginService.EXTRA_TYPE, request);
		intent.putExtra(TaginService.EXTRA_PARAM_1, param1);
		intent.putExtra(TaginService.EXTRA_PARAM_2, param2);
		mContext.startService(intent);
	}
	
	public static Tagin getService() {
		return mTagin;
	}
}
