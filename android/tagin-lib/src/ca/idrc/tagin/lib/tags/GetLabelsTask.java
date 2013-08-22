package ca.idrc.tagin.lib.tags;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import ca.idrc.tagin.lib.TaginManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class GetLabelsTask<T extends Context & GetLabelsTaskListener> extends AsyncTask<Void, Void, List<String>> {
	
	private T mContext;
	private String mURN;
	
	public GetLabelsTask(T context, String urn) {
		mContext = context;
		mURN = urn;
	}

	@Override
	protected List<String> doInBackground(Void... params) {
		String urn = mURN;
		List<String> labels = new ArrayList<String>();
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(TaginManager.TAGS_APP_URL + "?urn=" + urn);
		try {
			HttpResponse response = client.execute(request);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";
			String json = "";
			while ((line = reader.readLine()) != null) {
				json += line;
			}
			labels = new Gson().fromJson(json, new TypeToken<List<String>>(){}.getType());
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		return labels;
	}
	
	@Override
	protected void onPostExecute(List<String> labels) {
		mContext.onGetLabelsTaskComplete(mURN, labels);
	}
	
}
