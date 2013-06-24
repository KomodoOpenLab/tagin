package ca.idrc.tagin.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class Pattern {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key key;
	
	@Basic
	private Long id;

	@ElementCollection
	private Map<String,Beacon> beacons;

	@Basic
	private Double maxRssi;

	public Pattern() {
		this.id = null;
		this.beacons = new HashMap<String,Beacon>();
		this.maxRssi = Beacon.NULL_RSSI;
	}

	public Key getKey() {
		return key;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public Map<String,Beacon> getBeacons() {
		return beacons;
	}

	public Double getMaxRssi() {
		return maxRssi;
	}

	public void put(String bssid, Integer frequency, Integer dbm) {
		Beacon beacon = new Beacon(bssid, frequency, dbm);
		beacons.put(beacon.getId(), beacon);
	}
	
	public boolean contains(String bssid, Integer frequency) {
		return beacons.containsKey(bssid + ";" + frequency);
	}
	
	public void updateRanks() {
		ArrayList<Beacon> values = new ArrayList<Beacon>(beacons.values());
		Collections.sort(values);
		maxRssi = values.get(0).getRssi();
		for (Beacon beacon : beacons.values()) {
			beacon.updateRank(maxRssi);
		}
	}

	public String toString() {
		return getClass().getSimpleName() +
				"[Key: " + getKey() +
				", maxRSSI: " + getMaxRssi() +
				", beacons: " + getBeacons().toString() + "]";
	}

}
