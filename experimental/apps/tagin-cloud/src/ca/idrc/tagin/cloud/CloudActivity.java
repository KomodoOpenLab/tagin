package ca.idrc.tagin.cloud;

/**
 * Komodo Lab: Tagin! Project: 3D Tag Cloud
 * Google Summer of Code 2011
 * @authors Reza Shiftehfar, Sara Khosravinasr and Jorge Silva
 */

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
import ca.idrc.tagin.cloud.tag.Tag;
import ca.idrc.tagin.cloud.tag.TagCloudView;
import ca.idrc.tagin.cloud.util.TagAdderDialog;
import ca.idrc.tagin.cloud.util.TagMap;
import ca.idrc.tagin.lib.TaginManager;
import ca.idrc.tagin.lib.TaginService;
import ca.idrc.tagin.lib.tags.GetLabelTask;
import ca.idrc.tagin.lib.tags.GetLabelTaskListener;
import ca.idrc.tagin.lib.tags.SetLabelTask;
import ca.idrc.tagin.lib.tags.SetLabelTaskListener;

public class CloudActivity extends Activity implements GetLabelTaskListener, SetLabelTaskListener {
	
	private CloudActivity mInstance;
	private TagMap mTagMap;
	private TaginManager mTaginManager;
	private TagCloudView mTagCloudView;
	private TagAdderDialog mTagAdderDialog;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInstance = this;
		mTaginManager = new TaginManager(this);
		mTagAdderDialog = new TagAdderDialog(this);
		mTagMap = (TagMap) getIntent().getSerializableExtra(LauncherActivity.EXTRA_TAGS);
		createTagCloud();
	}

	private void createTagCloud() {
		Display display = getWindowManager().getDefaultDisplay();
		mTagCloudView = new TagCloudView(this, display.getWidth(), display.getHeight(), mTagMap);
		setContentView(mTagCloudView);
		mTagCloudView.requestFocus();
		updateTagCloud();
	}
	
	public void submitTag(Tag tag) {
		SetLabelTask<CloudActivity> task = new SetLabelTask<CloudActivity>(mInstance, tag.getID(), tag.getText());
		task.execute();
		addTagToCloud(tag);
	}
	
	public void addTagToCloud(Tag tag) {
		if (tag != null) {
			mTagCloudView.addTag(tag);
			mTagMap.put(tag.getID(), tag);
			updateTagCloud();
		}
	}
	
	private void updateTagCloud() {
		for (Tag tag : mTagMap.values()) {
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
		case R.id.menu_add_tag:
			mTagAdderDialog.showDialog();
			break;
		case R.id.menu_settings:
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
				GetLabelTask<CloudActivity> task = new GetLabelTask<CloudActivity>(mInstance, urn);
				task.execute();
			}
		}
	};

	@Override
	public void onGetLabelTaskComplete(String urn, String label) {
		if (label != null) {
			mTagAdderDialog.getLabelTextView().setText(label);
		}
	}

	@Override
	public void onSetLabelTaskComplete(Boolean isSuccessful) {
		
	}

}