package ca.idrc.tagin.model;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class Beacon implements Comparable<Beacon> {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key key;
	@Basic
	private String id; // represented by BSSID + frequency
	@Basic
	private Integer rssi;
	@Basic
	private Double rank;

	public Beacon() {
		this.id = null;
		this.rssi = null;
		this.rank = null;
	}

	public Beacon(String bssid, Integer frequency, Integer rssi, int maxRSSIEver) {
		this.id = bssid + ";" + frequency;
		this.rssi = rssi;
		this.rank = Util.calculateRank(rssi, maxRSSIEver);
	}

	public void setId(String bssid, Integer frequency) {
		this.id = bssid + ";" + frequency;
	}

	public void setRSSI(Integer rssi) {
		this.rssi = rssi;
	}

	public void setRank(Double rank) {
		this.rank = rank;
	}

	public Double getRank() {
		return rank;
	}

	public String getId() {
		return id;
	}

	public Integer getRSSI() {
		return rssi;
	}

	@Override
	public int compareTo(Beacon beacon) {
		return beacon.getRSSI() - getRSSI();
	}

	public String toString() {
		return getClass().getName() + "[" + 
				"ID: " + getId() + 
				", RSSI: " + getRSSI() + 
				", rank: " + getRank() + "]";
	}

}
