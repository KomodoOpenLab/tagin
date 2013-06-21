package ca.idrc.tagin.model;

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

	@ElementCollection
	private Map<String,Beacon> beacons;

	@Basic
	private Integer maxRssi;

	public Pattern() {
		this.beacons = new HashMap<String,Beacon>();
		this.maxRssi = Beacon.NULL_RSSI;
	}

	public Key getKey() {
		return key;
	}

	public Map<String,Beacon> getBeacons() {
		return beacons;
	}

	public Integer getMaxRssi() {
		return maxRssi;
	}

	public void put(String bssid, Integer frequency, Integer rssi) {
		Beacon beacon = new Beacon(bssid, frequency, rssi, maxRssi);
		beacons.put(beacon.getId(), beacon);
		if (beacon.getRssi() > maxRssi) {
			maxRssi = beacon.getRssi();
		}
	}

	public String toString() {
		return getClass().getName() +
				"[Key: " + getKey() +
				", maxRSSI: " + getMaxRssi() +
				", beacons: " + getBeacons().toString() + "]";
	}

}
