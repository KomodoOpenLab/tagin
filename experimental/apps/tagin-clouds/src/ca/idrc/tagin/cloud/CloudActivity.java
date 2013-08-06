package ca.idrc.tagin.cloud;

/**
 * Komodo Lab: Tagin! Project: 3D Tag Cloud
 * Google Summer of Code 2011
 * @authors Reza Shiftehfar, Sara Khosravinasr and Jorge Silva
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import ca.idrc.tagin.cloud.util.TagAdderDialog;
import ca.idrc.tagin.lib.TaginManager;
import ca.idrc.tagin.lib.TaginService;

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
		mTags = loadState();
		
		Display display = getWindowManager().getDefaultDisplay();
		mTagCloudView = new TagCloudView(this, display.getWidth(), display.getHeight(), mTags);
		setContentView(mTagCloudView);
		mTagCloudView.requestFocus();
		updateTagCloud();
	}
	
	public void addTagToCloud(Tag tag) {
		if (tag != null && !mTags.containsKey(tag.getID())) {
			mTagCloudView.addTag(tag);
			mTags.put(tag.getID(), tag);
			updateTagCloud();
			saveState();
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
	
	@SuppressWarnings("unchecked")
	private Map<String,Tag> loadState() {
		Map<String,Tag> tags = new LinkedHashMap<String,Tag>();
		File file = new File(getFilesDir() + "/state");

		if (file.exists()) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
				tags = (Map<String,Tag>) ois.readObject();
				ois.close();
			} catch (Exception e) { 
				Log.d("tagin", "exception caught: " + e.getMessage());
				e.printStackTrace();
			}
		}
		return tags;
	}
	
	private void saveState() {
		ObjectOutputStream oos = null;
		try {
			File history = new File(getFilesDir() + "/state");
			history.getParentFile().createNewFile();
			FileOutputStream fout = new FileOutputStream(history);
			oos = new ObjectOutputStream(fout);
			oos.writeObject(mTags);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();  
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (oos != null) {
					oos.flush();
					oos.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
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