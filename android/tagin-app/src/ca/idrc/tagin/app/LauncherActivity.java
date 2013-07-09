package ca.idrc.tagin.app;

import java.io.IOException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ca.idrc.tagin.lib.TaginService;

import com.google.api.services.tagin.Tagin;
import com.google.api.services.tagin.model.Pattern;
import com.google.api.services.tagin.model.URN;

public class LauncherActivity extends Activity {

	private Tagin mTaginService;
	private Button mRequestButton;
	private WifiManager mWifiManager;
	private Handler mHandler;
	
	private Pattern mPattern;
	private int mScanIterations;
	private final int MAX_SCANS = 3;
	private final int SCAN_INTERVAL = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);
		mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		mRequestButton = (Button) findViewById(R.id.requestURN);
		mHandler = new Handler();
		mTaginService = TaginService.newInstance();
		registerReceiver(mReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.launcher, menu);
		return true;
	}

	public void onRequestURN(View view) {
		if (mWifiManager.isWifiEnabled()) {
			mRequestButton.setText("Scanning...");
			mScanIterations = 0;
			mPattern = new Pattern();
			mScanRunnable.run();
		} else {
			Toast.makeText(this, "Please enable your WiFi", Toast.LENGTH_SHORT).show();
		}
	}

	private class RequestURNTask extends AsyncTask<Void, Void, URN> {

		@Override
		protected void onPreExecute() {
			mRequestButton.setText("Fetching URN...");
		}

		@Override
		protected URN doInBackground(Void... params) {
			URN urn = null;
			try {
				urn = mTaginService.patterns().add(mPattern).execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return urn;
		}

		@Override
		protected void onPostExecute(URN urn) {
			if (urn != null)
				mRequestButton.setText(urn.get("value").toString());
			else
				mRequestButton.setText("Could not fetch URN");
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				if (mScanIterations++ < MAX_SCANS) {
					mPattern.addBeaconsFromScanResult(mWifiManager.getScanResults());
					mHandler.postDelayed(mScanRunnable, SCAN_INTERVAL);
				} else {
					mPattern.updateRanks();
					new RequestURNTask().execute();
				}
			}
		}
	};

	private Runnable mScanRunnable = new Runnable () {
		@Override
		public void run() {
			mWifiManager.startScan();
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

}