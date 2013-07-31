package ca.idrc.tagin.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TagsActivity extends Activity {
	
	private Context mContext;
	
	private EditText mURNText1;
	private EditText mURNText2;
	private EditText mLabelText;
	
	private TextView mLabelView;
	private Button mSetLabelButton;
	private Button mGetLabelButton;
	
	private final String APP_URL = "http://tagin-tags.appspot.com/tagin-tags";
	
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
		new GetLabelTask().execute();
	}
	
	public void onSetLabel(View view) {
		mSetLabelButton.setText("Saving tag...");
		new SetLabelTask().execute();
	}
	
	private class GetLabelTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String urn = mURNText1.getText().toString();
			String result = "";
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(APP_URL + "?urn=" + urn);
			try {
				HttpResponse response = client.execute(request);
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				String line = "";
				while ((line = reader.readLine()) != null) {
					result += line;
				}
			} catch (ClientProtocolException e1) {
				e1.printStackTrace();
				return null;
			} catch (IOException e1) {
				e1.printStackTrace();
				return null;
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				mLabelView.setText(result);
			}
			mGetLabelButton.setText("Get label");
		}
		
	}	
	
	private class SetLabelTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			String urn = mURNText2.getText().toString();
			String label = mLabelText.getText().toString();

			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(APP_URL);
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("urn", urn));
				nameValuePairs.add(new BasicNameValuePair("label", label));
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				client.execute(post);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean isSuccessful) {
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


}
