package ca.idrc.tagin.lib;

import java.io.IOException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tagin.Tagin;
import com.google.api.services.tagin.model.Pattern;
import com.google.api.services.tagin.model.URN;

public class TaginService extends Service {
	
	private Tagin mTagin;
	private WifiManager mWifiManager;
	private Handler mHandler;
	
	private Pattern mPattern;
	private int mScanIterations;
	private final int MAX_SCANS = 3;
	private final int SCAN_INTERVAL = 1000;
	
	public static final String ACTION_REQUEST_URN = "ca.idrc.tagin.lib.ACTION_REQUEST_URN";
	public static final String ACTION_URN_READY = "ca.idrc.tagin.lib.ACTION_URN_READY";
	public static final String EXTRA_TYPE = "ca.idrc.tagin.lib.EXTRA_TYPE";
	public static final String EXTRA_URN_RESULT = "ca.idrc.tagin.lib.EXTRA_URN_RESULT";
	
	@Override
	public void onCreate() {
		Tagin.Builder builder = new Tagin.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
		mTagin = builder.build();
		mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		mHandler = new Handler();
		registerReceiver(mReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}

	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {
		String type = intent.getStringExtra(EXTRA_TYPE);
		if (type.equals(ACTION_REQUEST_URN)) {
			mScanIterations = 0;
			mPattern = new Pattern();
			mHandler.post(mScanRunnable);
		}
		return START_NOT_STICKY;
	}
	
	private Runnable mScanRunnable = new Runnable () {
		@Override
		public void run() {
			mWifiManager.startScan();
		}
	};
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				if (mScanIterations++ < MAX_SCANS) {
					mPattern.addBeaconsFromScanResult(mWifiManager.getScanResults());
					mHandler.postDelayed(mScanRunnable, SCAN_INTERVAL);
				} else {
					new FetchURNTask().execute();
				}
			}
		}
	};

	private class FetchURNTask extends AsyncTask<Void, Integer, Void> {
		
		private String result = null;

		@Override
		protected Void doInBackground(Void... params) {
			mPattern.updateRanks();
			try {
				URN urn = mTagin.patterns().add(mPattern).execute();
				result = urn.getValue();
			} catch (IOException e) {
				Log.e("tagin!", "Failed to submit fingerprint: " + e.getMessage());
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void param) {
			Intent broadcastIntent = new Intent();
			broadcastIntent.setAction(ACTION_URN_READY);
			broadcastIntent.putExtra(EXTRA_URN_RESULT, result);
			sendBroadcast(broadcastIntent);
		}
	};
	
	@Override
	public void onDestroy() {
		unregisterReceiver(mReceiver);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
}
