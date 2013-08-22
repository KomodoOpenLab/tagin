package ca.idrc.tagin.app;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ca.idrc.tagin.lib.tags.GetLabelsTask;
import ca.idrc.tagin.lib.tags.GetLabelsTaskListener;
import ca.idrc.tagin.lib.tags.SetLabelTask;
import ca.idrc.tagin.lib.tags.SetLabelTaskListener;

public class TagsActivity extends Activity implements GetLabelsTaskListener, SetLabelTaskListener {
	
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
		mGetLabelButton.setText(R.string.fetching_label);
		String urn = mURNText1.getText().toString();
		GetLabelsTask<TagsActivity> task = new GetLabelsTask<TagsActivity>(this, urn);
		task.execute();
	}
	
	public void onSetLabel(View view) {
		mSetLabelButton.setText(R.string.saving_tag);
		String urn = mURNText2.getText().toString();
		String label = mLabelText.getText().toString();
		SetLabelTask<TagsActivity> task = new SetLabelTask<TagsActivity>(this, urn, label);
		task.execute();
	}
	
	@Override
	public void onGetLabelsTaskComplete(String urn, List<String> labels) {
		mLabelView.setText(labels.toString());
		mGetLabelButton.setText(R.string.get_label);
	}

	@Override
	public void onSetLabelTaskComplete(Boolean isSuccessful) {
		mURNText2.setText("");
		mLabelText.setText("");
		mSetLabelButton.setText(R.string.set_label);
		if (isSuccessful) {
			Toast.makeText(mContext, R.string.tag_saved, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(mContext, R.string.tag_save_failed, Toast.LENGTH_SHORT).show();
		}
	}
}
