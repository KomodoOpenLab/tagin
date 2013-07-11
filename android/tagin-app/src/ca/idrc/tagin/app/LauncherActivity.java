package ca.idrc.tagin.app;

import com.google.api.services.tagin.model.FingerprintCollection;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import ca.idrc.tagin.lib.TaginManager;
import ca.idrc.tagin.lib.TaginService;

public class LauncherActivity extends Activity {

	private TaginManager mTaginManager;
	private Button mURNRequestButton;
	private Button mListFingerprintsButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);
		mURNRequestButton = (Button) findViewById(R.id.requestURN);
		mListFingerprintsButton = (Button) findViewById(R.id.listFingerprints);
		mTaginManager = new TaginManager(this);
		registerReceiver(mReceiver, new IntentFilter(TaginService.ACTION_URN_READY));
		registerReceiver(mReceiver, new IntentFilter(TaginService.ACTION_FINGERPRINTS_READY));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.launcher, menu);
		return true;
	}

	public void onRequestURN(View view) {
		mURNRequestButton.setText("Requesting URN...");
		mTaginManager.apiRequest(TaginService.REQUEST_URN);
	}
	
	public void onListFingerprints(View view) {
		mListFingerprintsButton.setText("Requesting fingerprints list...");
		mTaginManager.apiRequest(TaginService.REQUEST_LIST_FINGERPRINTS);
	}
	

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(TaginService.ACTION_URN_READY)) {
				String urn = intent.getStringExtra(TaginService.EXTRA_QUERY_RESULT);
				if (urn != null) {
					mURNRequestButton.setText(urn);
				} else {
					mURNRequestButton.setText("Failed to acquire URN");
				}
			} else if (intent.getAction().equals(TaginService.ACTION_FINGERPRINTS_READY)) {
				String str = intent.getStringExtra(TaginService.EXTRA_QUERY_RESULT);
				FingerprintCollection fps = new Gson().fromJson(str, FingerprintCollection.class);
				if (fps != null) {
					mListFingerprintsButton.setText(fps.getItems().toString());
				} else {
					mListFingerprintsButton.setText("Failed to list fingerprints");
				}
			}
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

}