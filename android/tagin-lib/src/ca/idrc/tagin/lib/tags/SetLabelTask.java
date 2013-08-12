package ca.idrc.tagin.lib.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.AsyncTask;
import ca.idrc.tagin.lib.TaginManager;

public class SetLabelTask<T extends Context & SetLabelTaskListener> extends AsyncTask<Void, Void, Boolean> {

	private T mContext;
	private String mURN;
	private String mLabel;
	
	public SetLabelTask(T context, String urn, String label) {
		mContext = context;
		mURN = urn;
		mLabel = label;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		String urn = mURN;
		String label = mLabel;

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(TaginManager.TAGS_APP_URL);
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
		mContext.onSetLabelTaskComplete(isSuccessful);
	}
	
}