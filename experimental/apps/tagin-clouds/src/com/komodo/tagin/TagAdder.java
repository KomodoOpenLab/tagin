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

	private String tag_name, time, bssid, urn;
	private int mPopularity;
	private long tagId;
	
	private EditText mTagName, mRating;
	private Button mSubmit;
	private Helper mHelper; 
	private TagsDatabase db;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.addtag);
		
		Intent intent = getIntent();
		urn  = intent.getStringExtra(MainActivity.EXTRA_URN);
		
		db = new TagsDatabase(this);
		db.open();
	
		mHelper = Helper.getInstance();
		
		mTagName = (EditText)findViewById(R.id.Details_EditText01);
		mRating = (EditText)findViewById(R.id.Details_EditText02);
	
		mSubmit = (Button)findViewById(R.id.submit);
		
		mSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tag_name = mTagName.getText().toString();
				mPopularity = Integer.parseInt(mRating.getText().toString());
				time = mHelper.getTime();
				bssid = mHelper.getDeviceId(TagAdder.this);
				tagId = db.addTagDetails(tag_name, time, bssid, mPopularity);
				Log.i(Helper.TAG, "Tag Details Added" + Long.toString(tagId));
				addTag();
			}
		});
	}
	
	private void addTag() {
		db.addTag(tagId, urn);
		Intent intent = new Intent();
		intent.putExtra(MainActivity.EXTRA_TAG_NAME, tag_name);
		intent.putExtra(MainActivity.EXTRA_TAG_POPULARITY, mPopularity);
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
