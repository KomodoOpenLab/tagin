/**
 * tagin!, a WiFi-based point-of-interest (POI) service for tagging 
 * outdoor and indoor locations
 * Copyright (c) 2010, Inclusive Design Research Centre
 * jsilva@ocad.ca
 *
 * You may use tagin! under the terms of either the MIT License or the
 * GNU General Public License (GPL).
 * 
 * The MIT License is recommended for most projects. It is simple and easy to
 * understand, and it places almost no restrictions on what you can do with
 * tagin! 
 * 
 * If the GPL suits your project better, you are also free to use tagin! under
 * that license.
 * 
 * You don't have to do anything special to choose one license or the other,
 * and you don't have to notify anyone which license you are using.
 * 
 * MIT License: http://scyp.idrc.ocad.ca/pub/licenses/MITL.txt
 * GPL License: http://scyp.idrc.ocad.ca/pub/licenses/GPL.txt
 * 
 * @author Susahosh Rahman
 * @email susahosh@gmail.com
 * @author Jorge Silva
 * @email jsilva@ocad.ca
 **/

package ca.idi.tagin.wifi;

import java.util.List;

import ca.idi.tagin.R;
import ca.idi.tagin.jsinterface.JavaToJSInterface;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class WifiServiceThread extends Service implements Runnable {

	private WifiScanner wifiScanner = null;
	private volatile Thread serviceThread = null;
	private WifiManager wm = null;
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			System.out.println("Handler recived message: " + msg);
		}
	};
	
	private BroadcastReceiver scanResultRecieved = new BroadcastReceiver() {
		public void onReceive(Context c, Intent i) {
			// Code to execute when SCAN_RESULTS_AVAILABLE_ACTION event occurs
			System.out.println("Reciving intent boradcast...");
			if (i.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				System.out.println("Result available");
				WifiManager w = (WifiManager) c
						.getSystemService(Context.WIFI_SERVICE);
				List<ScanResult> res = w.getScanResults(); // Returns a <list>
															// of scanResults
				System.out.println("Number of APs: " + res.size());
				if (wifiScanner == null) {
					System.out.println("No WiFi Scanner found. Aborting operation.");
					return;
				}
				wifiScanner.updateLatestScanTime();
				wifiScanner.updateScansList(res);
				wifiScanner.wifiDataChangeNotification();
				// wifiScanner.androidToJSTest();

			}
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("Wifi serivce started...");
		wifiScanner = WifiScanner.getInstance(null, null);
		serviceThread = new Thread(this);
		serviceThread.start();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(scanResultRecieved);
		serviceThread = null;
		super.onDestroy();
		System.out.println("Wifi service stopped...");
	}

	public boolean startScan() {
		if (wm == null || wifiScanner == null) {
			System.out.println("Failed to start WiFi scan. No WiFi manager/scanner available");
			return false;
		}
		if (wm.startScan()) {
			JavaToJSInterface javaToJSInterface;
			javaToJSInterface = JavaToJSInterface.getInstance(null);
			javaToJSInterface.setStatusMessage(getText(R.string.refresh_requested).toString());			
			System.out.println("Successfully started scan..");
			return true;
		} else {
			wifiScanner.wifiDataChangeNotification();
			System.out.println("Failed to start scan..");
			return false;
		}
	}

	@Override
	public void run() {
		Thread thisThread = Thread.currentThread();

		IntentFilter scanIntentFilter = new IntentFilter();
		wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		scanIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		registerReceiver(scanResultRecieved, scanIntentFilter, null, handler);

		if (startScan() == false) {
			return;
		}

		while (serviceThread == thisThread) {
			handler.sendEmptyMessage(0);
			try {
				if(wifiScanner.shouldScanAgian()) {
					startScan();
				}
				synchronized (this) {
					// logic for processing wifi data should go here
					// for now simply invoke a change notification
					// and then go to sleep
					// wifiScanner.wifiDataChangeNotification();
					// wifiScanner.androidToJSTest();
					wait(20000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
