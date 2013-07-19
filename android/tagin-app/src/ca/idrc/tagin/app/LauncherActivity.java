package ca.idrc.tagin.app;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ca.idrc.tagin.lib.TaginManager;
import ca.idrc.tagin.lib.TaginService;

import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tagin.model.Fingerprint;
import com.google.api.services.tagin.model.FingerprintCollection;

public class LauncherActivity extends Activity {

	private TaginManager mTaginManager;
	
	private Button mURNRequestButton;
	private Button mListFingerprintsButton;
	private Button mFindNeighboursButton;
	
	private TextView mURNTextView;
	private TextView mListFPTextView;
	private EditText mNeighboursEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);
		mURNRequestButton = (Button) findViewById(R.id.requestURN);
		mListFingerprintsButton = (Button) findViewById(R.id.listFingerprints);
		mFindNeighboursButton = (Button) findViewById(R.id.findButton);
		mURNTextView = (TextView) findViewById(R.id.textView1);
		mListFPTextView = (TextView) findViewById(R.id.textView2);
		mNeighboursEditText = (EditText) findViewById(R.id.editText3);
		
		mTaginManager = new TaginManager(this);
		registerReceiver(mReceiver, new IntentFilter(TaginService.ACTION_URN_READY));
		registerReceiver(mReceiver, new IntentFilter(TaginService.ACTION_NEIGHBOURS_READY));
		registerReceiver(mReceiver, new IntentFilter(TaginService.ACTION_FINGERPRINTS_READY));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.launcher, menu);
		return true;
	}

	public void onRequestURN(View view) {
		mURNRequestButton.setText("Requesting URN...");
		mTaginManager.apiRequest(TaginService.REQUEST_URN);
	}
	
	public void onListFingerprints(View view) {
		mListFingerprintsButton.setText("Requesting fingerprints list...");
		mTaginManager.apiRequest(TaginService.REQUEST_LIST_FINGERPRINTS);
	}
	
	public void onFindNeighbours(View view) {
		if (mURNTextView.getText().length() != 32) {
			Toast.makeText(this, "Invalid URN", Toast.LENGTH_SHORT).show();
		} else {
			mFindNeighboursButton.setText("Searching for nearby neighbours...");
			mTaginManager.apiRequest(TaginService.REQUEST_NEIGHBOURS, mURNTextView.getText().toString());
		}
	}
	

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(TaginService.ACTION_URN_READY)) {
				String urn = intent.getStringExtra(TaginService.EXTRA_QUERY_RESULT);
				handleURNResponse(urn);
			} else if (intent.getAction().equals(TaginService.ACTION_FINGERPRINTS_READY)) {
				String str = intent.getStringExtra(TaginService.EXTRA_QUERY_RESULT);
				handleFingerprintsResponse(str);
			} else if (intent.getAction().equals(TaginService.ACTION_NEIGHBOURS_READY)) {
				String str = intent.getStringExtra(TaginService.EXTRA_QUERY_RESULT);
				handleNeighboursResponse(str);
			}
		}
	};
	
	private void handleFingerprintsResponse(String result) {
		FingerprintCollection fps = null;
		
		try {
			fps = new GsonFactory().fromString(result, FingerprintCollection.class);
		} catch (IOException e) {
			Log.e("tagin!", "Deserialization error: " + e.getMessage());
		}
		
		if (fps != null) {
			StringBuffer sb = new StringBuffer();
			List<Fingerprint> items = fps.getItems();
			if (items != null) {
				for (Fingerprint fp : items) {
					sb.append("ID:     " + fp.getId() + "\nURN:   " + fp.getUrn() + "\n\n");
				}
			}
			mListFPTextView.setText(sb.toString());
		} else {
			mListFPTextView.setText("Failed to list fingerprints");
		}
		mListFingerprintsButton.setText("fingerprints.list()");
	}
	
	private void handleURNResponse(String urn) {
		if (urn != null) {
			mURNTextView.setText(urn);
		} else {
			mURNTextView.setText("Failed to acquire URN");
		}
		mURNRequestButton.setText("Request URN");
	}
	
	private void handleNeighboursResponse(String result) {
		if (result != null) {
			mNeighboursEditText.setText(result);
		} else {
			mNeighboursEditText.setText("Could not find neighbours");
		}
		mFindNeighboursButton.setText("Find neighbours");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

}