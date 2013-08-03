package ca.idrc.tagin.cloud;

/**
 * Komodo Lab: Tagin! Project: 3D Tag Cloud
 * Google Summer of Code 2011
 * @authors Reza Shiftehfar, Sara Khosravinasr and Jorge Silva
 */

import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import ca.idrc.tagin.cloud.util.TagAdderDialog;
import ca.idrc.tagin.lib.TaginManager;
import ca.idrc.tagin.lib.TaginService;

/**
 * SampleTagCloud class: this is a sample program to show how the 3D Tag Cloud
 * can be used. It Creates the activity and sets the ContentView to our
 * TagCloudView class
 */
public class CloudActivity extends Activity {
	
	private Map<String,Tag> mTags;
	private TaginManager mTaginManager;
	private TagCloudView mTagCloudView;
	private TagAdderDialog mTagAdderDialog;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startSplashScreen();
		mTaginManager = new TaginManager(this);
		mTagAdderDialog = new TagAdderDialog(this);
        createTagCloud();
	}

	private void startSplashScreen() {
		setContentView(R.layout.splash);
	}

	private void createTagCloud() {
		mTags = new LinkedHashMap<String, Tag>();
		
		Display display = getWindowManager().getDefaultDisplay();
		mTagCloudView = new TagCloudView(this, display.getWidth(), display.getHeight(), mTags);
		setContentView(mTagCloudView);
		mTagCloudView.requestFocus();
	}
	
	public void addTagToCloud(Tag tag) {
		if (tag != null && !mTags.containsKey(tag.getID())) {
			mTagCloudView.addTag(tag);
			mTags.put(tag.getID(), tag);
			updateTagCloud();
		}
	}
	
	private void updateTagCloud() {
		for (Tag tag : mTags.values()) {
			mTagCloudView.setTagRGBT(tag);
		}
	}
	
	public void onGetURNClick(View view) {
		mTaginManager.apiRequest(TaginService.REQUEST_URN);
		mTagAdderDialog.getURNTextView().setText("Fetching URN...");
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.add_tag:
			mTagAdderDialog.showDialog();
			break;
		case R.id.exit_app:
			finish();
			break;
		}
		return true;
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(TaginService.ACTION_URN_READY)) {
				String urn = intent.getStringExtra(TaginService.EXTRA_QUERY_RESULT);
				mTagAdderDialog.getURNTextView().setText(urn);
			}
		}
	};
}