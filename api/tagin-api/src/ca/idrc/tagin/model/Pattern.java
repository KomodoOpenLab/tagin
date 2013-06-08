package ca.idrc.tagin.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Pattern implements Comparable<Pattern> {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long id;
	private String bssid;
	private Integer rssi;
	
	public Pattern() {
		
	}
	
	public Long getId() {
		return id;
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
