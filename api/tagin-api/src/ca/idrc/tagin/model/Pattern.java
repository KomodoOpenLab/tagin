package ca.idrc.tagin.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Pattern {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long id;
	private Map<String,Integer> mBeacons; // BSSID / RSSI pairs
	
	public Pattern() {
		mBeacons = new HashMap<String,Integer>();
	}
	
	public Long getId() {
		return id;
	}
	
	public Map<String,Integer> getBeacons() {
		return mBeacons;
	}
	
	public String toString() {
		return getClass().getName() +
			"[ID: " + getId() + ", values: " + getBeacons().toString();
	}

}
