package com.komodo.tagin;

/**
 * Komodo Lab: Tagin! Project: 3D Tag Cloud
 * Google Summer of Code 2011
 * @authors Reza Shiftehfar, Sara Khosravinasr and Jorge Silva
 */
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import ca.idi.taginsdk.Fingerprinter;
import ca.idi.taginsdk.Helper;
import ca.idi.taginsdk.TaginURN;

/**
 * SampleTagCloud class: this is a sample program to show how the 3D Tag Cloud
 * can be used. It Creates the activity and sets the ContentView to our
 * TagCloudView class
 */
public class MainActivity extends Activity {
	
	public static final String EXTRA_URN = "Uniform Resource Name";
	public static final String EXTRA_TAG_NAME = "tag_name";
	public static final String EXTRA_TAG_POPULARITY = "popularity";
	private String SEARCH_TEXT = "http://www.google.com/m?hl=en&q=";
	public static String URN;
	
	private List<Tag> mTags;
	private TagCloudView mTagCloudView;
	
	private int width, height;
	private boolean tagCloudCreated;
	private static final int TAG_ADDED = 1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Step2: to get a full-screen View:
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Step3: get screen resolution:
		Display display = getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();

		// Step4: start splash screen and wait for current location to be found:
		// tagCloudcreated is used to know whether we need to create or update
		// the tagcloud
		tagCloudCreated = false;
		startSplashScreen();
		// notice that creation/update of tagcloud has been
		// moved to BroadcastReceiver.onReceive() method
		
	}

	private void startSplashScreen() {
		setContentView(R.layout.splash);
	}

	private void stopSplashScreen() {
	}

	private void createTagCloud(List<Tag> tempTagList) {
		// create a TagCloudview and set it as the content of this Activity
		mTags = tempTagList;
		if (tempTagList == null || tempTagList.isEmpty()) {
			Toast.makeText(this, "No Recorded Tag Exists.", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(this, TagAdder.class);
			intent.putExtra(EXTRA_URN, URN);
			startActivityForResult(intent, TAG_ADDED);
			return;
		}
		
		mTagCloudView = new TagCloudView(this, width, height, mTags);
		setContentView(mTagCloudView);
		mTagCloudView.requestFocus();
		mTagCloudView.setFocusableInTouchMode(true);
		tagCloudCreated = true;
	}

	private void updateTagCloud(List<Tag> tempTagList) {
		if (tempTagList == null)
			return;
		List<Tag> tagListMinusTempTagList = listAMinuslistB(mTags, tempTagList);
		List<Tag> tempTagListMinusTagList = listAMinuslistB(tempTagList, mTags);
		int noOfPossibleUpdate = tagListMinusTempTagList.size();
		int noOfNeededUpdate = tempTagListMinusTagList.size();
		if (noOfNeededUpdate == 0)
			return;
		if (noOfNeededUpdate > noOfPossibleUpdate) { // we also need to add some new Tags
			// do all the possible updates
			for (int i = 0; i < noOfPossibleUpdate; i++) {
				mTagCloudView.replace(tempTagListMinusTagList.get(i),
									  tagListMinusTempTagList.get(i).getText());
				// update the RGB value and scaled textSize of the tag
				mTagCloudView.setTagRGBT(tempTagListMinusTagList.get(i), 
										 tempTagListMinusTagList.get(i).getPopularity());
				// update the tagList to reflect new tag
				replace(tempTagListMinusTagList.get(i),
						tagListMinusTempTagList.get(i).getText());
			}
			// add the remaining as new tags
			for (int i = noOfPossibleUpdate; i < noOfNeededUpdate; i++) {
				mTagCloudView.addTag(tempTagListMinusTagList.get(i));
				mTags.add(tempTagListMinusTagList.get(i));
			}
		} else { // no need to add any new tag
			// do all updates:
			for (int i = 0; i < noOfNeededUpdate; i++) {
				mTagCloudView.replace(tempTagListMinusTagList.get(i),
									  tagListMinusTempTagList.get(i).getText());
				// update the RGB value and scaled textSize of the tag
				mTagCloudView.setTagRGBT(tempTagListMinusTagList.get(i), 
										 tempTagListMinusTagList.get(i).getPopularity());
				// update the tagList to reflect new tag
				replace(tempTagListMinusTagList.get(i),
						tagListMinusTempTagList.get(i).getText());
			}
		}
		Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
	}

	public void replace(Tag newTag, String oldTagText) {
		for (Tag tag : mTags) {
			if (oldTagText.equalsIgnoreCase(tag.getText())) {
				tag.setPopularity(newTag.getPopularity());
				tag.setText(newTag.getText());
				tag.setUrl(newTag.getUrl());
				break;
			}
		}
	}

	private List<Tag> listAMinuslistB(List<Tag> listA, List<Tag> listB) {
		List<Tag> tempList = new ArrayList<Tag>();
		for (int i = 0; i < listA.size(); i++) {
			String listAText = listA.get(i).getText();
			boolean found = false;
			for (int j = 0; j < listB.size(); j++) {
				if (listB.get(j).getText().equalsIgnoreCase(listAText)) {
					found = true;
					break;
				}
			}
			if (!found)
				tempList.add(listA.get(i));
		}
		return tempList;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.add_tag:
			Intent intent = new Intent(this, TagAdder.class);
			intent.putExtra(EXTRA_URN, URN);
			startActivityForResult(intent, TAG_ADDED);
			break;
		case R.id.exit_app:
			finish();
			break;
		}
		return true;
	}

	// Executed whenever a new tag is created
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAG_ADDED:
			if (data != null) {
				String tagName = data.getStringExtra(EXTRA_TAG_NAME); 
				String popularity = data.getStringExtra(EXTRA_TAG_POPULARITY);
				Tag tempTag = new Tag(tagName, Integer.parseInt(popularity), SEARCH_TEXT + tagName );			
				List<Tag> tempTagList = new ArrayList<Tag>();
				if (!tagCloudCreated) { // first time: stop splash and create TagCloud
					stopSplashScreen();
					tempTagList.add(tempTag);
					createTagCloud(tempTagList);
				} else { // after initial creation of Tag Cloud, just update it
					tempTagList.addAll(mTags);
					tempTagList.add(tempTag);
					updateTagCloud(tempTagList);
				}
				registerReceiver(mReceiver, new IntentFilter(TaginURN.ACTION_URN_READY));
			}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	@Override
	protected void onStop() {
		stopService(new Intent(TaginURN.INTENT_URN_SERVICE));
		super.onStop();
	}

	protected void onResume() {
		super.onResume();
	}

	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
			mReceiver = null;
		}
		stopService(new Intent(Fingerprinter.INTENT_STOP_SERVICE));
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(TaginURN.ACTION_URN_READY)) {
				URN = TaginURN.getURN();
				Log.i(Helper.TAG, URN);
				List<Tag> tempTagList = new ArrayList<Tag>();
				//tempTagList = fetchTags(URN);
				if (!tagCloudCreated) { // first time: stop splash and create TagCloud
					stopSplashScreen();
					createTagCloud(tempTagList);
				} else { // after initial creation of Tag Cloud, just update it
					updateTagCloud(tempTagList);
				}
			}
		}
	};
}