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
	/**
	 * Individual beacons that this fingerprint contains
	**/
	private List<Beacon> mBeacons;
	private String mTime;
	private Helper mHelper = Helper.getInstance();
	
	// Constructors
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
	
	public void setBeaconsFromScanResult(List<ScanResult> scanResults, int mMaxRSSIEver) {
		mBeacons = new ArrayList<Beacon>();
		for (ScanResult scanResult : scanResults) {
			mBeacons.add(new Beacon(scanResult.BSSID, scanResult.level, mMaxRSSIEver));
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
	public void addBeaconsFromScanResult(List<ScanResult> scanResults, int n, int mMaxRSSIEver) {
		ArrayList<Beacon> allBeacons = new ArrayList<Beacon>();
		Integer thisLength = mBeacons.size();
		Integer otherLength = scanResults.size();
		Boolean dupFound; int i, j;
		// Initialize boolean masks for identifying identical beacons
		Boolean[] otherUsed = new Boolean[otherLength];
		for (i = 0; i < otherLength; i++) {
			otherUsed[i] = false;
		}
		// 
		for (i=0; i < thisLength; i++) {
			j = 0; dupFound = false;
			while (!dupFound && (j < otherLength)) {
				if (!otherUsed[j]) {
					if (mBeacons.get(i).getBSSID().equals(scanResults.get(j).BSSID)) {
						// The same beacon was found in the other fingerprint.
						// Calculate the average
						allBeacons.add(new Beacon(mBeacons.get(i).getBSSID(),
												  movingRSSIAvg(mBeacons.get(i).getRSSI(), scanResults.get(j).level, n),
												  mMaxRSSIEver));
						otherUsed[j] = true;
						dupFound = true;
					}
				}
				j++;
			}
			if (!dupFound) {
				allBeacons.add(new Beacon(mBeacons.get(i).getBSSID(), 
										  movingRSSIAvg(Helper.NULL_RSSI, mBeacons.get(i).getRSSI(), n), 
										  mMaxRSSIEver));
			}
		}
		// Add non-duplicate scan results
		for (i = 0; i < otherLength; i++) {
			if (!otherUsed[i]) {
				allBeacons.add(new Beacon(scanResults.get(i).BSSID,
										  movingRSSIAvg(Helper.NULL_RSSI,scanResults.get(i).level,n),
										  mMaxRSSIEver));
			}
		}
		mBeacons = allBeacons;
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
		//Until (n-1)th scan the value was startPower and for nth scan the value is newPower.
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
	public void applyDisplacement(List<Beacon> changeVector){
		List<Beacon> thisBeacons = mBeacons;
		Integer thisLength = mBeacons.size();
		Integer vectorLength = changeVector.size();
		int i, j;
		Boolean [] vectorUsed = new Boolean[vectorLength];
		for(i=0; i<vectorLength; i++){
			vectorUsed[i] = false;
		}
		for(i=0; i<thisLength; i++){
			j = 0; 
			while (j < vectorLength) {
				if (!vectorUsed[j]) {
					if (thisBeacons.get(i).getBSSID().equals(changeVector.get(j).getBSSID())) {
						// The same beacon was found in the change vector
						thisBeacons.get(i).setRank(thisBeacons.get(i).getRank() + changeVector.get(j).getRank());
						vectorUsed[j] = true; //The identical beacon found. 
					}
				}
				j++;
			}
		}
		// Converting to an ArrayList to add the new beacons
		ArrayList<Beacon> beaconList = new ArrayList<Beacon>(thisBeacons); 
		// Add remaining beacons from the Change Vector
		for ( i = 0; i < vectorLength; i++ )
			if (!vectorUsed[i])
				beaconList.add(changeVector.get(i));
		this.setBeacons(beaconList);
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
