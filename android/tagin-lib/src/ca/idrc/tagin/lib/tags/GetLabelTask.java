package ca.idrc.tagin.lib.tags;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import ca.idrc.tagin.lib.TaginManager;


public class GetLabelTask<T extends Context & GetLabelTaskListener> extends AsyncTask<Void, Void, String> {
	
	private T mContext;
	private String mURN;
	
	public GetLabelTask(T context, String urn) {
		mContext = context;
		mURN = urn;
	}

	@Override
	protected String doInBackground(Void... params) {
		String urn = mURN;
		String result = "";
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(TaginManager.TAGS_APP_URL + "?urn=" + urn);
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
		mContext.onGetLabelTaskComplete(result);
	}
	
}
