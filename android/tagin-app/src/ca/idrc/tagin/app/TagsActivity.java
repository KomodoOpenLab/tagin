package ca.idrc.tagin.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ca.idrc.tagin.lib.tags.GetLabelTask;
import ca.idrc.tagin.lib.tags.GetLabelTaskListener;
import ca.idrc.tagin.lib.tags.SetLabelTask;
import ca.idrc.tagin.lib.tags.SetLabelTaskListener;

public class TagsActivity extends Activity implements GetLabelTaskListener, SetLabelTaskListener {
	
	private Context mContext;
	
	private EditText mURNText1;
	private EditText mURNText2;
	private EditText mLabelText;
	
	private TextView mLabelView;
	private Button mSetLabelButton;
	private Button mGetLabelButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tags);
		mContext = this;
		
		mURNText1 = (EditText) findViewById(R.id.tags_urn1_edit);
		mURNText2 = (EditText) findViewById(R.id.tags_urn2_edit);
		mLabelText = (EditText) findViewById(R.id.tags_label_edit);
		mLabelView = (TextView) findViewById(R.id.tags_tv_1);
		mSetLabelButton = (Button) findViewById(R.id.tags_set_label_btn);
		mGetLabelButton = (Button) findViewById(R.id.tags_get_label_btn);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.launcher, menu);
		return true;
	}
	
	public void onGetLabel(View view) {
		mGetLabelButton.setText("Fetching label...");
		String urn = mURNText1.getText().toString();
		GetLabelTask<TagsActivity> task = new GetLabelTask<TagsActivity>(this, urn);
		task.execute();
	}
	
	public void onSetLabel(View view) {
		mSetLabelButton.setText("Saving tag...");
		String urn = mURNText2.getText().toString();
		String label = mLabelText.getText().toString();
		SetLabelTask<TagsActivity> task = new SetLabelTask<TagsActivity>(this, urn, label);
		task.execute();
	}
	
	@Override
	public void onGetLabelTaskComplete(String urn, String result) {
		if (result != null) {
			mLabelView.setText(result);
		}
		mGetLabelButton.setText("Get label");
	}

	@Override
	public void onSetLabelTaskComplete(Boolean isSuccessful) {
		mURNText2.setText("");
		mLabelText.setText("");
		mSetLabelButton.setText("Set label");
		if (isSuccessful) {
			Toast.makeText(mContext, "Tag successfully saved", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(mContext, "Failed to save tag", Toast.LENGTH_SHORT).show();
		}
	}

}
