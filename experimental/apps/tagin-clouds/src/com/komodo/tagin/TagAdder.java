package com.komodo.tagin;

/**
 * Komodo Lab: Tagin! Project: 3D Tag Cloud
 * Google Summer of Code 2011
 * @authors Primal Pappachan
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TagAdder extends Activity {
	
	private EditText mTagName, mRating;
	private Button mSubmit;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.addtag);
		mTagName = (EditText)findViewById(R.id.Details_EditText01);
		mRating = (EditText)findViewById(R.id.Details_EditText02);
		mSubmit = (Button)findViewById(R.id.submit);
		
		mSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addTag();
			}
		});
	}
	
	private void addTag() {
		Intent intent = new Intent();
		intent.putExtra(MainActivity.EXTRA_TAG_NAME, mTagName.getText().toString());
		intent.putExtra(MainActivity.EXTRA_TAG_POPULARITY, mRating.getText().toString());
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
