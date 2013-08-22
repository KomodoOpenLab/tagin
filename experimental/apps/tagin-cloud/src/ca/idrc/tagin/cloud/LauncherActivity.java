package ca.idrc.tagin.cloud;

import java.util.List;

import com.google.api.services.tagin.model.URN;
import com.google.api.services.tagin.model.URNCollection;

import ca.idrc.tagin.cloud.tag.Tag;
import ca.idrc.tagin.cloud.util.TagMap;
import ca.idrc.tagin.lib.TaginManager;
import ca.idrc.tagin.lib.TaginService;
import ca.idrc.tagin.lib.TaginUtils;
import ca.idrc.tagin.lib.tags.GetLabelsTask;
import ca.idrc.tagin.lib.tags.GetLabelsTaskListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class LauncherActivity extends Activity implements GetLabelsTaskListener {

	public final static String EXTRA_TAGS = "ca.idrc.tagin.cloud.EXTRA_TAGS";
	private final String MAX_NEIGHBOURS = "10";
	private Integer mNeighboursCounter;
	private String mInitialURN;
	
	private LauncherActivity mInstance;
	private TagMap mTagMap;
	private TaginManager mTaginManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);
		mInstance = this;
		mTaginManager = new TaginManager(this);
		mTagMap = new TagMap();
		init();
	}
	
	private void init() {
		if (TaginCloudApp.persistence.isConnected()) {
			if (TaginCloudApp.persistence.isWifiEnabled()) {
				mTaginManager.apiRequest(TaginService.REQUEST_URN);
			} else {
				Toast.makeText(this, "Please activate your WiFi adapter", Toast.LENGTH_SHORT).show();
				// TODO show dialog
			}
		} else {
			Toast.makeText(this, "No active connection found, working in offline mode", Toast.LENGTH_SHORT).show();
			startCloud();
		}
	}
	
	public void startCloud() {
		Intent intent = new Intent(LauncherActivity.this, CloudActivity.class);
		intent.putExtra(EXTRA_TAGS, mTagMap);
		startActivity(intent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
        registerReceiver(mReceiver, new IntentFilter(TaginService.ACTION_URN_READY));
        registerReceiver(mReceiver, new IntentFilter(TaginService.ACTION_NEIGHBOURS_READY));
	}
	
	@Override
	public void onGetLabelsTaskComplete(String urn, List<String> labels) {
		if (urn.equals(mInitialURN)) {
			for (String label : labels) {
				Tag tag = new Tag(urn, label, 20);
				mTagMap.put(urn, tag);
			}
		} else {
			synchronized(mNeighboursCounter) {
				mNeighboursCounter--;
				for (String label : labels) {
					Tag tag = new Tag(urn, label, 20);
					mTagMap.put(urn, tag);
				}
				if (mNeighboursCounter == 0) {
					startCloud();
				}
			}
		}
	}
	
	public void handleNeighboursReady(String result) {
		URNCollection urns = TaginUtils.deserialize(result, URNCollection.class);
		
		if (urns != null && urns.getItems() != null && urns.getItems().size() > 0) {
			mNeighboursCounter = urns.getItems().size();
			for (URN urn : urns.getItems()) {
				GetLabelsTask<LauncherActivity> task = new GetLabelsTask<LauncherActivity>(this, urn.getValue());
				task.execute();
			}
		} else {
			Log.d(TaginCloudApp.APP_TAG, "No neighbours found");
			startCloud();
		}
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(TaginService.ACTION_URN_READY)) {
				String urn = intent.getStringExtra(TaginService.EXTRA_QUERY_RESULT);
				if (urn != null) {
					mTaginManager.apiRequest(TaginService.REQUEST_NEIGHBOURS, urn, MAX_NEIGHBOURS);
					mInitialURN = urn;
					GetLabelsTask<LauncherActivity> task = new GetLabelsTask<LauncherActivity>(mInstance, urn);
					task.execute();
				} else {
					Log.d(TaginCloudApp.APP_TAG, "Could not submit fingerprint");
					// TODO show error dialog
					startCloud();
				}
			} else if (intent.getAction().equals(TaginService.ACTION_NEIGHBOURS_READY)) {
				String result = intent.getStringExtra(TaginService.EXTRA_QUERY_RESULT);
				handleNeighboursReady(result);
			}
		}
	};

}
