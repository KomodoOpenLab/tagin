package ca.idi.taginsdk;


/**
 * Komodo Lab: Tagin! Project: 3D Tag Cloud
 * Google Summer of Code 2011
 * @authors Jorge Silva and Primal Pappachan
 */

public class Beacon implements Comparable<Beacon> {
	
	private String bssid; // MAC ID
	private Integer rssi; // Received Signal Strength Indication
	private Double rank; // Rank value in the range [0, 1]

	public Beacon() {
		this.bssid = null;
		this.rssi = null;
		this.rank = null;
	}

	public Beacon(String bssid, Integer rssi, int maxRSSIEver) {
		this.bssid = bssid;
		this.rssi = rssi;
		this.rank = calculateRank(rssi, maxRSSIEver);
	}

	public void setBSSID(String bssid) { this.bssid = bssid; }
	public void setRSSI(Integer rssi) { this.rssi = rssi; }
	public void setRank(Double rank) { this.rank = rank; }
	public Double getRank() { return rank; }
	public String getBSSID() { return bssid; }
	public Integer getRSSI() { return rssi; }

	@Override
	public int compareTo(Beacon b) {
		return b.getRSSI() - getRSSI();
	}
	
	/**
	 * Calculates the Rank of the beacon by normalizing with respect to Max RSSI
	 * @param rssi - received signal strength indication
	 * @param maxRSSIEver - Maximum RSSI ever recorded by this device
	 * @return Normalized rank value in the range [0, 1]
	 */
	private Double calculateRank(int rssi, int maxRSSIEver){
		Double maxPowerEver = dBm2Power(maxRSSIEver);
		return Math.pow(dBm2Power(rssi) / maxPowerEver, 0.25);
	}
	
	private double dBm2Power(int rssi) {
		return Math.pow(10.0, Double.valueOf(rssi - 30) / 10.0);
	}
	
	public String toString() {
		return getClass().getName() + "[" + 
			"BSSID: " + getBSSID() + 
			", RSSI: " + getRSSI() + 
			", rank: " + getRank() + "]";
	}

}