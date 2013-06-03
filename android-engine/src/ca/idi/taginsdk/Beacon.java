package ca.idi.taginsdk;


/**
 * Komodo Lab: Tagin! Project: 3D Tag Cloud
 * Google Summer of Code 2011
 * @authors Jorge Silva and Primal Pappachan
 */

public class Beacon implements Comparable<Beacon> {
	
	private String BSSID; //MAC ID
	private Integer RSSI; //Recieved Signal Strength
	private Double rank; //Rank value in the range [0, 1]

	// Constructors
	public Beacon() {
		this.BSSID = null;
		this.RSSI = null;
		this.rank = null;
	}

	public Beacon(String bssid, Integer rssi, int maxRSSIEver) {
		this.BSSID = bssid;
		this.RSSI = rssi;
		this.rank = calculateRank(rssi, maxRSSIEver); //TODO Check 
	}
	
	public Beacon(String bssid, Integer rssi, Double rank) {
		this.BSSID = bssid;
		this.RSSI = rssi;
		this.rank = rank;
	}

	public void setBSSID(String bssid) { this.BSSID = bssid; }
	public void setRSSI(Integer rssi) { this.RSSI = rssi; }
	public void setRank(Double rank) { this.rank = rank; }
	public Double getRank() { return rank; }
	public String getBSSID() { return BSSID; }
	public Integer getRSSI() { return RSSI; }

	@Override
	public int compareTo(Beacon another) {
		/*Natural Comparison method of the class. The ordering given by this method is considered 
		the natural ordering of the objects of the class.
		*/
		return another.getRSSI() - RSSI;
	}
	
	/**
	 * Calculates the Rank of the beacon by normalizing with respect to Max RSSI
	 * @param rssi - received signal strength indication
	 * @param maxRSSIEver - Maximum RSSI ever recorded by this device
	 * @return Normalized rank value in the range [0, 1]
	 */
	public Double calculateRank(int rssi, int maxRSSIEver){
		Double maxPowerEver = dBm2Power(maxRSSIEver);
		return Math.pow(dBm2Power(rssi) / maxPowerEver, 0.25);
	}
	
	private double dBm2Power(int rssi) {
		return Math.pow(10.0, Double.valueOf(rssi - 30) / 10.0);
	}
	
	public String toString() {
		return "BSSID:" + getBSSID() + ", RSSI: " + getRSSI() + ", rank: " + getRank();
	}

}