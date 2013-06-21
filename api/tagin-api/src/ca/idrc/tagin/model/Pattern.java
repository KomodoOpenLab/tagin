package ca.idrc.tagin.model;

import java.util.HashMap;
import java.util.Map;

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
	
	public Pattern() {
		beacons = new HashMap<String,Beacon>();
	}
	
	public Key getKey() {
		return key;
	}

	public Map<String,Beacon> getBeacons() {
		return beacons;
	}

	public void setBeacons(Map<String,Beacon> beacons) {
		this.beacons = beacons;
	}
	
	public String toString() {
		return getClass().getName() +
			"[Key: " + getKey() + ", values: " + getBeacons().toString() + "]";
	}

}
