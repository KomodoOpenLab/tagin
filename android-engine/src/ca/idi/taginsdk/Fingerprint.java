package ca.idi.taginsdk;


/**
 * Komodo Lab: Tagin! Project: 3D Tag Cloud
 * Google Summer of Code 2011
 * @authors Jorge Silva and Primal Pappachan
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.net.wifi.ScanResult;


public class Fingerprint {

	private List<Beacon> mBeacons;
	private String mTime;
	private Helper mHelper = Helper.getInstance();


	public Fingerprint() {
		mBeacons = new ArrayList<Beacon>();
		mTime = mHelper.getTime();
	}

	public Fingerprint(List<Beacon> beacons) {
		setBeacons(beacons);
	}

	public void setBeacons(List<Beacon> beacons) {
		mBeacons = beacons;
		mTime = mHelper.getTime();
	}

	public void setBeaconsFromScanResult(List<ScanResult> scanResults, int maxRSSIEver) {
		mBeacons = new ArrayList<Beacon>();
		for (ScanResult scanResult : scanResults) {
			mBeacons.add(new Beacon(scanResult.BSSID, scanResult.level, maxRSSIEver));
		}
		mTime = mHelper.getTime();
	}

	/**
	 * Instead of substituting the fingerprint's beacons, add a list from another scan.
	 * This is useful when performing multiple scans in a single fingerprint to increase
	 * its accuracy.
	 * @param scanResults: Results from a WiFi scan
	 * @param n: Iteration per fingerprint that this scan represents (for calculating an average)
	 **/
	public void addBeaconsFromScanResult(List<ScanResult> scanResults, int n, int maxRSSIEver) {
		Map<String,Beacon> beacons = new HashMap<String,Beacon>();
		for (Beacon beacon : this.getBeacons()) {
			beacons.put(beacon.getBSSID(), new Beacon(beacon.getBSSID(), 
												movingRSSIAvg(Helper.NULL_RSSI, beacon.getRSSI(), n), 
												maxRSSIEver));
		}

		for (ScanResult scanResult: scanResults) {
			if (beacons.containsKey(scanResult.BSSID)) {
				Beacon b = beacons.get(scanResult.BSSID);
				beacons.put(scanResult.BSSID, new Beacon(scanResult.BSSID,
													movingRSSIAvg(b.getRSSI(), scanResult.level, n),
													maxRSSIEver));
			} else {
				beacons.put(scanResult.BSSID, new Beacon(scanResult.BSSID, 
													movingRSSIAvg(Helper.NULL_RSSI, scanResult.level, n),
													maxRSSIEver));
			}
		}
		mBeacons = new ArrayList<Beacon>(beacons.values());
		mTime = mHelper.getTime();
	}

	private double dBm2Power(int rssi) {
		return Math.pow(10.0, Double.valueOf(rssi - 30) / 10.0);
	}

	private int power2dBm(double power) {
		return (int) Math.round(10 * Math.log10(power)) + 30;
	}

	private int movingRSSIAvg (int avgRSSI, int newRSSI, int n) {
		double startPower = avgRSSI == Helper.NULL_RSSI? 0 : dBm2Power(avgRSSI);
		double newPower = newRSSI == Helper.NULL_RSSI? 0 : dBm2Power(newRSSI);
		double avgPower = ((startPower * Double.valueOf(n-1)) + newPower) / Double.valueOf(n); 
		// Until (n-1)th scan the value was startPower and for nth scan the value is newPower.
		return power2dBm(avgPower);
	}

	/**
	 * Calculates the relative distance between two fingerprints.
	 * The value is between 0 and 1 and is maximal when two fingerprints
	 * don't share a beacon and minimal where they share a lot of beacons.
	 */
	public double rankDistanceTo(Fingerprint fp) {
		double d = 0.0;    // Euclidean distance between two beacons having same BSSID in two fingerprints.
		double maxD = 0.0; // Maximum possible fingerprint distance when two fingerprints don't share any beacon.
		Map<String,Beacon> beacons = new HashMap<String,Beacon>();
		for (Beacon beacon : this.getBeacons()) {
			maxD += Math.pow(beacon.getRank(), 2.0);
			d += Math.pow(beacon.getRank(), 2.0);
			beacons.put(beacon.getBSSID(), beacon);
		}

		for (Beacon beacon : fp.getBeacons()) {
			maxD += Math.pow(beacon.getRank(), 2.0);
			if (beacons.containsKey(beacon.getBSSID())) {
				Beacon b = beacons.get(beacon.getBSSID());
				d += Math.pow(beacon.getRank() - b.getRank(), 2.0) - Math.pow(b.getRank(), 2.0);
			} else {
				d += Math.pow(beacon.getRank(), 2.0);
			}
		}
		return Math.sqrt(d) / Math.sqrt(maxD);
	}


	public int getMaxRSSI() {
		Collections.sort(mBeacons);
		return mBeacons.get(0).getRSSI();
	}

	/**
	 * Displaces the fingerprint by the change vector.
	 * @param changeVector - An Array of Beacons
	 */
	public void applyDisplacement(List<Beacon> displacementVector) {
		Map<String,Beacon> beacons = new HashMap<String,Beacon>();
		for (Beacon beacon : this.getBeacons()) {
			beacons.put(beacon.getBSSID(), beacon);
		}

		for (Beacon beacon : displacementVector) {
			if (beacons.containsKey(beacon.getBSSID())) {
				Beacon b = beacons.get(beacon.getBSSID());
				b.setRank(b.getRank() + beacon.getRank()); //TODO check if this operation is valid
				beacons.put(beacon.getBSSID(), b);
			} else {
				beacons.put(beacon.getBSSID(), beacon);
			}
		}
		setBeacons(new ArrayList<Beacon>(beacons.values()));
	}

	/**
	 * Merges two fingerprints
	 * @return
	 */
	public void merge(Fingerprint fp) {
		Map<String,Beacon> beacons = new HashMap<String,Beacon>();
		for (Beacon beacon : this.getBeacons()) {
			beacon.setRank(beacon.getRank() / 2);
			beacons.put(beacon.getBSSID(), beacon);
		}

		for (Beacon beacon : fp.getBeacons()) {
			if (beacons.containsKey(beacon.getBSSID())) {
				Beacon b = beacons.get(beacon.getBSSID());
				b.setRank(b.getRank() + (beacon.getRank() / 2));
				beacons.put(b.getBSSID(), b);
			} else {
				beacon.setRank(beacon.getRank() / 2);
				beacons.put(beacon.getBSSID(), beacon);
			}
		}
		setBeacons(new ArrayList<Beacon>(beacons.values()));
	}

	public List<Beacon> getBeacons() { 
		return mBeacons;
	}

	public String getTime() { 
		return mTime;
	}

}
