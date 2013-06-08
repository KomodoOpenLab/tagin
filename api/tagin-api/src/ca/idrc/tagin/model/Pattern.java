package ca.idrc.tagin.model;

public class Pattern implements Comparable<Pattern> {
	
	private Integer id;   // Global ID
	private String bssid; // MAC ID
	private Integer rssi; // Received Signal Strength Indicator
	
	public Pattern() {
		
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getBSSID() {
		return bssid;
	}

	public void setBSSID(String bssid) {
		this.bssid = bssid;
	}

	public Integer getRSSI() {
		return rssi;
	}

	public void setRSSI(Integer rssi) {
		this.rssi = rssi;
	}
	
	@Override
	public int compareTo(Pattern p) {
		return p.getRSSI() - getRSSI();
	}
	
	public String toString() {
		return getClass().getName() +
			"[ID: " + getId() + 
			", BSSID: " + getBSSID() + 
			", RSSI: " + getRSSI() + "]";
	}

}
