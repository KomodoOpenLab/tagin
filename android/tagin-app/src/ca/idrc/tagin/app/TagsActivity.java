package ca.idrc.tagin.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TagsActivity extends Activity {
	
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
		
	}
	
	public void onSetLabel(View view) {
		
	}


}
