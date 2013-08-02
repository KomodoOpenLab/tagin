package ca.idrc.tagin.cloud;

/**
 * Komodo Lab: Tagin! Project: 3D Tag Cloud
 * Google Summer of Code 2011
 * @authors Primal Pappachan
 */

import com.komodo.tagin.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import ca.idi.taginsdk.Helper;

public class TagAdder extends Activity {
	
	private String time, bssid, urn;
	private long tagId;
	private Helper mHelper; 
	private TagsDatabase db;
	
	private EditText mTagName, mRating;
	private Button mSubmit;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.addtag);
		mTagName = (EditText)findViewById(R.id.Details_EditText01);
		mRating = (EditText)findViewById(R.id.Details_EditText02);
		mSubmit = (Button)findViewById(R.id.submit);
		mHelper = Helper.getInstance();
		
		Intent intent = getIntent();
		urn  = intent.getStringExtra(MainActivity.EXTRA_URN);
		db = new TagsDatabase(this);
		db.open();
		
		mSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//addTag();
				int popularity = Integer.parseInt(mRating.getText().toString());
				time = mHelper.getTime();
				bssid = mHelper.getDeviceId(TagAdder.this);
				tagId = db.addTagDetails(mTagName.getText().toString(), time, bssid, popularity);
				addTag();
			}
		});
	}
	
	private void addTag() {
		Log.d("tagin", "TagAdder.addTag()");
		db.addTag(tagId, urn);
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
