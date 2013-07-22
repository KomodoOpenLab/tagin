package com.komodo.tagin;

/**
 * Komodo Lab: Tagin! Project: 3D Tag Cloud
 * Google Summer of Code 2011
 * @authors Primal Pappachan
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import ca.idi.taginsdk.Helper;

public class TagAdder extends Activity {

	private EditText mTagName, mRating;
	private Button mSubmit;
	private String tag_name, time, bssid, urn;
	private int popularity;
	private TagsDatabase tDatabase;
	private long tag_id;
	private Helper mHelper; 
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.addtag);
		
		Intent intent = getIntent();
		urn  = intent.getStringExtra(MainActivity.EXTRA_URN);
		
		tDatabase = new TagsDatabase(this);
		tDatabase.open();
	
		mHelper = Helper.getInstance();
		
		mTagName = (EditText)findViewById(R.id.Details_EditText01);
		mRating = (EditText)findViewById(R.id.Details_EditText02);
	
		mSubmit = (Button)findViewById(R.id.submit);
		
		mSubmit.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				tag_name = mTagName.getText().toString();
				popularity = Integer.parseInt(mRating.getText().toString());
				time = mHelper.getTime();
				bssid = mHelper.getDeviceId(TagAdder.this);
				tag_id = tDatabase.addTagDetails(tag_name, time, bssid, popularity);
				Log.i(Helper.TAG, "Tag Details Added" + Long.toString(tag_id));
				addTag();
			}
		});
	}
	
	private void addTag(){
		tDatabase.addTag(tag_id, urn);
		Intent intent = new Intent();
		intent.putExtra(MainActivity.EXTRA_TAG_NAME, tag_name);
		intent.putExtra(MainActivity.EXTRA_TAG_POPULARITY, popularity);
		setResult(1, intent);
		finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
}
