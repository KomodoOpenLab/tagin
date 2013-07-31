package com.komodo.tagin;

/**
 * Komodo Lab: Tagin! Project: 3D Tag Cloud
 * Google Summer of Code 2011
 * @authors Reza Shiftehfar, Sara Khosravinasr and Jorge Silva
 */

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
import android.view.Window;
import android.view.WindowManager;
import ca.idrc.tagin.lib.TaginManager;
import ca.idrc.tagin.lib.TaginService;

/**
 * SampleTagCloud class: this is a sample program to show how the 3D Tag Cloud
 * can be used. It Creates the activity and sets the ContentView to our
 * TagCloudView class
 */
public class CloudActivity extends Activity {
	
	public static final String EXTRA_URN = "Uniform Resource Name";
	public static final String EXTRA_TAG_NAME = "tag_name";
	public static final String EXTRA_TAG_POPULARITY = "popularity";
	private String SEARCH_TEXT = "http://www.google.com/m?hl=en&q=";
	
	private boolean isCloudCreated;
	
	private Map<String,Tag> mTags;
	private TaginManager mTaginManager;
	private TagCloudView mTagCloudView;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startSplashScreen();
		isCloudCreated = false;

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		mTaginManager = new TaginManager(this);
        registerReceiver(mReceiver, new IntentFilter(TaginService.ACTION_URN_READY));
		
	}

	private void startSplashScreen() {
		setContentView(R.layout.splash);
	}

	private void createTagCloud(Map<String,Tag> tags) {
		// create a TagCloudview and set it as the content of this Activity
		mTags = tags;
		
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		mTagCloudView = new TagCloudView(this, width, height, mTags);
		setContentView(mTagCloudView);
		mTagCloudView.requestFocus();
		mTagCloudView.setFocusableInTouchMode(true);
		isCloudCreated = true;
	}
	
	private void addTagToCloud(Tag tag) {
		if (tag != null && !mTags.containsKey(tag.getText())) {
			mTagCloudView.addTag(tag);
			mTags.put(tag.getText(), tag);
			updateTagCloud();
		}
	}
	
	private void updateTagCloud() {
		for (Tag tag : mTags.values()) {
			mTagCloudView.setTagRGBT(tag);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.cloud, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
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
			}
		}
	};
}