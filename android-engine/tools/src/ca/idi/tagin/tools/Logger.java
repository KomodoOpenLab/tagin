package ca.idi.tagin.tools;

/**
 * Komodo Lab: Tagin! Project: 3D Tag Cloud
 * Google Summer of Code 2011
 * @authors Jorge Silva and Primal Pappachan
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import ca.idi.taginsdk.Beacon;
import ca.idi.taginsdk.Fingerprint;
import ca.idi.taginsdk.Fingerprinter;
import ca.idi.taginsdk.Helper;


public class Logger extends Activity {

	private final int START = 1;
	private final int STOP = -1;

	private TextView mMonitor1, mMonitor2, mMonitor3, mMonitor4, mMonitor5;
	private ToggleButton mToggleButton;
	private Button mStopButton;
	private ImageView mLoading;
	private Fingerprint mSavedFP; // Used to backup fp for calculating rank 
	private Helper mHelper;
	private Handler mHandler;
	private AnimationDrawable loadAnimation;

	private static BufferedWriter out;
	private final String mLogHeader = "FINGERPRINT_APS, RANK_DISTANCE_TO_PREVIOUS, In-place/Moving";

	// Strings for storing previous and current fingerprint details
	private String cTime, pAP, cAP, pTime = "-Infinity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Helper.DEBUG) android.os.Debug.waitForDebugger();

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog);

		mHelper = Helper.getInstance();
		mHandler = new Handler();

		// Textviews to monitor logging process
		mMonitor1 = (TextView) findViewById(R.id.Dialog_TextView01);
		mMonitor2 = (TextView) findViewById(R.id.Dialog_TextView02);
		mMonitor3 = (TextView) findViewById(R.id.Dialog_TextView03);
		mMonitor4 = (TextView) findViewById(R.id.Dialog_TextView04);
		mMonitor5 = (TextView) findViewById(R.id.Dialog_TextView05);

		mLoading = (ImageView) findViewById(R.id.Dialog_ImageView01);
		mLoading.setImageResource(R.drawable.loading);
		loadAnimation = (AnimationDrawable) mLoading.getDrawable();
		mStopButton = (ToggleButton) findViewById(R.id.Dialog_toggleButton01);
		mToggleButton = (ToggleButton) findViewById(R.id.Dialog_toggleButton02);

		mStopButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startFingerprint(STOP);
				fileFlush();
				finish();
			}
		});

		mToggleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mToggleButton.isChecked()) {
					Helper.showToast(Logger.this, "Click again to toggle to Moving");
				} else {	
					Helper.showToast(Logger.this, "Click again to toggle to IN PLACE");
				}
			}
		});

		try {
			createFileOnDevice(false); // Append is set to false at the beginning.
		} catch (IOException e) {
			Log.e(Helper.TAG, "Exception in createFileOnDevice");
			e.printStackTrace();
		}

		// Registers a broadcast for event of Fingerprint change
		registerReceiver(mReceiver, new IntentFilter(Fingerprinter.ACTION_FINGERPRINT_CHANGED));
		startFingerprint(1);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		startLoading(); 
	}

	/*
	 * Function to initially create the log file and it also writes the time of creation to file.
	 */
	private void createFileOnDevice(Boolean append) throws IOException {
		File root = Environment.getExternalStorageDirectory();
		if (root.canWrite()) {
			File logFile = new File(root, "taginLog.txt");
			FileWriter logWriter = new FileWriter(logFile, append);
			out = new BufferedWriter(logWriter);
			out.write("Logged at" + mHelper.getTime() + "\n");
			out.write(mLogHeader + "\n");
			Log.d(Helper.TAG, mLogHeader);
		}
	}

	private void startFingerprint(int status) {
		if (status == START) {
			Helper.showToast(this, "Started Logging");
			Intent intent = new Intent(Fingerprinter.INTENT_START_SERVICE);
			intent.putExtra(Fingerprinter.EXTRA_SCANS_PER_FINGERPRINT, 5);
			startService(intent);
			//TODO: Add number of WiFi scans as intent extra
		} else if (status == STOP) {
			stopService(new Intent(Fingerprinter.INTENT_STOP_SERVICE));
			Helper.showToast(this, "To view the complete log, check taginLog file in sdcard.");
		}
	}


	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String logItem = " "; // String buffer to be written to the log file
			if (action.equals(Fingerprinter.ACTION_FINGERPRINT_CHANGED)) {
				Helper.showToast(Logger.this, "Logging");
				Fingerprint lastFP = new Fingerprint(Fingerprinter.getFingerprint().getBeacons());
				Log.d(Helper.TAG, "Fingerprint: ");
				printFP(lastFP);
				cTime = lastFP.getTime(); // Getting the time current Fingerprint was taken
				cAP = Integer.toString(lastFP.getBeacons().size());
				if (mSavedFP != null) {	
					pTime = mSavedFP.getTime();
					logItem =  cAP + "," + measureRankDistance(mSavedFP,lastFP) + "," + 
							   mToggleButton.isChecked() +"\n" ;
					pAP = Integer.toString(mSavedFP.getBeacons().size());
					Log.d(Helper.TAG, logItem);
					writeToFile(logItem); // Writing pairs of Rank Distance and Inside/Outside to log file.
					mMonitor5.setText("Distance to previous: " + Double.toString(measureRankDistance(mSavedFP,lastFP)));
				}	
				mMonitor1.setText("Taken at: " +  cTime  );
				mMonitor2.setText("Number of Beacons: " + cAP);
				mMonitor3.setText("Taken at: " + pTime );
				mMonitor4.setText("Number of Beacons:  " + pAP);
				mSavedFP = lastFP; // Backs up the current fingerprint
			}
		}
	};

	private void printFP(Fingerprint fp) {
		for (Beacon b : fp.getBeacons())
			Log.d(Helper.TAG, b.toString());
	}

	private double measureRankDistance(Fingerprint fp1, Fingerprint fp2) {
		return fp1.rankDistanceTo(fp2);
	}

	/*
	 * Function to write the message to the log file.
	 */
	private void writeToFile(String message) {
		try {
			out.write(message + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
			mReceiver = null;
		}
		stopService(new Intent(Fingerprinter.INTENT_START_SERVICE));
	}

	private void fileFlush() {
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void startLoading() {
		mHandler.post(mLoader); // Posts the runnable to the message queue thread
	}

	private Runnable mLoader = new Runnable () { // Runs the loading animation in a new thread
		@Override
		public void run() {
			Log.d(Helper.TAG, "Loading");
			loadAnimation.start();
		}
	};


}
