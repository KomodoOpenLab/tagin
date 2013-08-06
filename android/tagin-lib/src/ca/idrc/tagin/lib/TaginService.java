package ca.idrc.tagin.lib;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;

import ca.idrc.tagin.lib.requests.FindNeighboursRequest;
import ca.idrc.tagin.lib.requests.ListFingerprintsRequest;
import ca.idrc.tagin.lib.requests.TaginApiCall;
import ca.idrc.tagin.lib.requests.URNRequest;

import com.google.api.services.tagin.Tagin;
import com.google.api.services.tagin.model.Pattern;

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
			new ApiRequestTask().execute(new ListFingerprintsRequest(mTagin));
		} else if (type.equals(REQUEST_NEIGHBOURS)) {
			String urn = intent.getStringExtra(EXTRA_PARAM_1);
			String count = intent.getStringExtra(EXTRA_PARAM_2);
			new ApiRequestTask().execute(new FindNeighboursRequest(mTagin, urn, count));
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
					new ApiRequestTask().execute(new URNRequest(mTagin, mPattern));
				}
			}
		}
	};

	private class ApiRequestTask extends AsyncTask<TaginApiCall, Void, String> {
		
		private TaginApiCall apiCall = null;

		@Override
		protected String doInBackground(TaginApiCall... params) {
			apiCall = params[0];
			return apiCall.execute();
		}
		
		@Override
		protected void onPostExecute(String result) {
			broadcastResult(apiCall.getBroadcastAction(), result);
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
