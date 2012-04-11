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

package ca.idi.tagin.jsinterface;

import android.net.wifi.ScanResult;
import java.util.ArrayList;
import java.util.List;

import ca.idi.tagin.*;
import ca.idi.tagin.communication.NoWifiScannerException;
import ca.idi.tagin.communication.TaginCommunicator;
import ca.idi.tagin.wifi.WifiScanner;

public final class JSToJavaInterface {

	public JSToJavaInterface() {
	}

	public void addTags(String tagsXML) {
		try {
			TaginCommunicator.getInstance().addTags(tagsXML);
		} catch (NoWifiScannerException e) {
			JavaToJSInterface.getInstance(null).setStatusMessage("No Wifi Scanner available!");
		}
	}
	
	public void addLocation(String locationXML) {
		try {
			TaginCommunicator.getInstance().addLocation(locationXML);
		} catch (NoWifiScannerException e) {
			JavaToJSInterface.getInstance(null).setStatusMessage("No Wifi Scanner available!");
		}		
	}
	
	public void setWritingModeOn() {
		TaginCommunicator.getInstance().pauseCommunicator();
	}
	
	public void setWritingModeOff() {
		TaginCommunicator.getInstance().resumeCommunicator();		
	}

	public void requestRefreshedData() {
		try {
			TaginCommunicator.getInstance().requestRefreshedData();
		} catch (NoWifiScannerException e) {
			JavaToJSInterface.getInstance(null).setStatusMessage(
					"No Wifi Scanner available!");
		}
	}

	public void pageLoadedNotification() {
		WifiScanner.getInstance(null, null).startWifiScanner();
		TaginCommunicator.getInstance().startCommunitator();
		// JavaToJSInterface.getInstance(null).setStatusMessage("Page ready");
	}

	public ArrayList<ScanResult> getLatestAPs() {
		WifiScanner scanner = WifiScanner.getInstance(null, null);
		List<ScanResult> tmp = scanner.getLatestScan();
		if (tmp == null) {
			return null; // new ArrayList<ScanResult>();
		}
		System.out.println("Returning latest AP list...");
		return (ArrayList<ScanResult>) tmp;
	}

	/*
	 * public ScanResult popAP() { WifiScanner scanner =
	 * WifiScanner.getInstance(null, null); return scanner.extractAccessPoint();
	 * }
	 */

	public MyObject getMyObject() {
		return new MyObject();
	}

}
