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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ca.idi.tagin.communication.NoWifiScannerException;
import ca.idi.tagin.communication.TCPConnector;
import ca.idi.tagin.communication.TaginCommunicator;
import ca.idi.tagin.jsinterface.JavaToJSInterface;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.webkit.WebView;

public class WifiScanner {
	private static WifiScanner serviceInstance = null;
	private WebView webPage = null;
	private Context taginAppContext = null;
	private TaginCommunicator communicator = null;
	private Date lastScanTime = null;
	private List<ScanResult> latestScan = null;
	public static final int DELAY_TIME = 3000;

	// private List<ScanResult> latestScanHolder = null;

	private WifiScanner(Context c, WebView w) {
		webPage = w;
		taginAppContext = c;
		communicator = TaginCommunicator.getInstance();
	}

	public void startWifiScanner() {
		Intent serviceIntent = new Intent(taginAppContext,
				WifiServiceThread.class);
		taginAppContext.startService(serviceIntent);
	}

	public void stopWifiScanner() {
		Intent serviceIntent = new Intent(taginAppContext,
				WifiServiceThread.class);
		taginAppContext.stopService(serviceIntent);
	}

	public static WifiScanner getInstance(Context c, WebView w) {
		if (serviceInstance == null) {
			if (c == null || w == null) {
				return null;
			}
			serviceInstance = new WifiScanner(c, w);
			// serviceInstance.scanLists = new WifiScanList();
		}
		return serviceInstance;
	}

	public List<ScanResult> getLatestScan() {
		return latestScan;
	}

	/*
	 * public ScanResult extractAccessPoint() { if (latestScanHolder == null) {
	 * return null; } if (latestScanHolder.size() == 0) { return null; }
	 * ScanResult r = latestScanHolder.remove(0);
	 * System.out.println(r.toString()); return r; }
	 */

	public synchronized void updateLatestScanTime() {
		lastScanTime = Calendar.getInstance().getTime();
	}
	
	public boolean shouldScanAgian() {
		//isUsefullResultAvailable..essentially check if enough time passed
		Calendar cal = Calendar.getInstance();
		Date currentTime = cal.getTime();
		if (getLatestScanTime() == null) {
			return true;
		}
		if ((currentTime.getTime() - getLatestScanTime().getTime()) > TCPConnector.CONNECTION_TIMEOUT + DELAY_TIME) {
			return true;
		}
		return false;
	}

	public Date getLatestScanTime() {
		return lastScanTime;
	}
	
	public void updateScansList(List<ScanResult> wifiPointList) {
		latestScan = wifiPointList;
		// latestScanHolder = wifiPointList;
	}

	public void wifiDataChangeNotification() {
		try {
			communicator.requestRefreshedData();
		} catch (NoWifiScannerException e) {
			JavaToJSInterface.getInstance(null).setStatusMessage("No Wifi Scanner available!");
		}
	}

	public void androidToJSTest() {
		webPage.clearCache(true);
		webPage.loadUrl("javascript:testAndroidConnection()");
	}

	public static int frequencyToChannel(int f) {
		int channel = -1;
		if (f == 2412) {
			channel = 1;
		} else if (f == 2417) {
			channel = 2;
		} else if (f == 2422) {
			channel = 3;
		} else if (f == 2427) {
			channel = 4;
		} else if (f == 2432) {
			channel = 5;
		} else if (f == 2437) {
			channel = 6;
		} else if (f == 2442) {
			channel = 7;
		} else if (f == 2447) {
			channel = 8;
		} else if (f == 2452) {
			channel = 9;
		} else if (f == 2457) {
			channel = 10;
		} else if (f == 2462) {
			channel = 11;
		}
		return channel;
	}

}
