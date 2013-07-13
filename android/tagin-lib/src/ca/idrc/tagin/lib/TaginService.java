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

import com.google.api.services.tagin.Tagin;
import com.google.api.services.tagin.model.FingerprintCollection;
import com.google.api.services.tagin.model.Pattern;
import com.google.api.services.tagin.model.URN;
import com.google.api.services.tagin.model.URNCollection;

public class TaginService extends Service {
	
	private Tagin mTagin;
	private WifiManager mWifiManager;
	private Handler mHandler;
	private Pattern mPattern;
	
	private int mScanIterations;
	private final int MAX_SCANS = 3;
	private final int SCAN_INTERVAL = 1000;
	
	// API requests
	public static final String REQUEST_LIST_FINGERPRINTS = "ca.idrc.tagin.lib.REQUEST_LIST_FINGERPRINTS";
	public static final String REQUEST_NEIGHBOURS = "ca.idrc.tagin.lib.REQUEST_NEIGHBOURS";
	public static final String REQUEST_URN = "ca.idrc.tagin.lib.REQUEST_URN";
	
	// Broadcasted actions
	public static final String ACTION_FINGERPRINTS_READY = "ca.idrc.tagin.lib.ACTION_FINGERPRINTS_READY";
	public static final String ACTION_NEIGHBOURS_READY = "ca.idrc.tagin.lib.ACTION_NEIGHBOURS_READY";
	public static final String ACTION_URN_READY = "ca.idrc.tagin.lib.ACTION_URN_READY";
	
	// Extras
	public static final String EXTRA_TYPE = "ca.idrc.tagin.lib.EXTRA_TYPE";
	public static final String EXTRA_PARAM_1 = "ca.idrc.tagin.lib.EXTRA_PARAM_1";
	public static final String EXTRA_PARAM_2 = "ca.idrc.tagin.lib.EXTRA_PARAM_2";
	public static final String EXTRA_QUERY_RESULT = "ca.idrc.tagin.lib.EXTRA_QUERY_RESULT";
	
	@Override
	public void onCreate() {
		mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		mTagin = TaginManager.getService();
		mHandler = new Handler();
		registerReceiver(mReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}

	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {
		String type = intent.getStringExtra(EXTRA_TYPE);
		if (type.equals(REQUEST_URN)) {
			mScanIterations = 0;
			mPattern = new Pattern();
			mHandler.post(mScanRunnable);
		} else if (type.equals(REQUEST_LIST_FINGERPRINTS)) {
			new ListFingerprintsTask().execute();
		} else if (type.equals(REQUEST_NEIGHBOURS)) {
			new FindNeighboursTask().execute(intent.getStringExtra(EXTRA_PARAM_1));
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
				Log.e(TaginManager.TAG, "Failed to submit fingerprint: " + e.getMessage());
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void param) {
			broadcastResult(ACTION_URN_READY, result);
		}
	};
	
	private class ListFingerprintsTask extends AsyncTask<Void, Integer, Void> {
		
		private FingerprintCollection result = null;

		@Override
		protected Void doInBackground(Void... params) {
			try {
				result = mTagin.fingerprints().list().execute();
			} catch (IOException e) {
				Log.e(TaginManager.TAG, "Failed to list fingerprints: " + e.getMessage());
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void param) {
			broadcastResult(ACTION_FINGERPRINTS_READY, result.toString());
		}
	};
	
	private class FindNeighboursTask extends AsyncTask<String, Integer, Void> {
		
		private URNCollection result = null;

		@Override
		protected Void doInBackground(String... params) {
			try {
				result = mTagin.urns().neighbours(params[0]).execute();
			} catch (IOException e) {
				Log.e(TaginManager.TAG, "Failed to find neighbours: " + e.getMessage());
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void param) {
			broadcastResult(ACTION_NEIGHBOURS_READY, result.toString());
		}
	};
	
	private void broadcastResult(String action, String result) {
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(action);
		broadcastIntent.putExtra(EXTRA_QUERY_RESULT, result);
		sendBroadcast(broadcastIntent);
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(mReceiver);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
}
