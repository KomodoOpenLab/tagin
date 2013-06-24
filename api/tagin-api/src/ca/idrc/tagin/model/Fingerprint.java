package ca.idrc.tagin.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import ca.idrc.tagin.dao.TaginEntityManager;

@Entity
public class Fingerprint {

	private static final Double THRESHOLD = 0.25;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne(cascade = CascadeType.ALL)
	private Pattern pattern;
	
	@Basic
	private String urn;

	public Fingerprint() {

	}
	
	public Fingerprint(Pattern pattern) {
		this.pattern = pattern;
		this.urn = null;
	}
	
	public List<Neighbour> getNeighbours() {
		List<Neighbour> neighbours = new ArrayList<Neighbour>();
		TaginEntityManager em = new TaginEntityManager();
		neighbours = em.getNeighbours(this);
		em.close();
		return neighbours;
	}
	
	public Neighbour getClosestNeighbour() {
		Neighbour neighbour = null;
		List<Neighbour> neighbours = getNeighbours();
		if (neighbours.isEmpty())
			return neighbour;
		Collections.sort(neighbours);
		neighbour = neighbours.get(0);
		return neighbour.getRankDistance() < THRESHOLD ? neighbour : null;
	}
	
	/**
	 * Calculates the relative distance between two fingerprints.
	 * The value is between 0 and 1 and is maximal when two fingerprints
	 * don't share a beacon and minimal where they share a lot of beacons.
	 */
	public Double rankDistanceTo(Fingerprint fp) {
		double d = 0.0;    // Euclidean distance between two beacons having same BSSID in two fingerprints.
		double maxD = 0.0; // Maximum possible fingerprint distance when two fingerprints don't share any beacon.
		Map<String,Beacon> beacons = new HashMap<String,Beacon>();
		for (Beacon beacon : this.getPattern().getBeacons().values()) {
			maxD += Math.pow(beacon.getRank(), 2.0);
			d += Math.pow(beacon.getRank(), 2.0);
			beacons.put(beacon.getId(), beacon);
		}

		for (Beacon beacon : fp.getPattern().getBeacons().values()) {
			maxD += Math.pow(beacon.getRank(), 2.0);
			if (beacons.containsKey(beacon.getId())) {
				Beacon b = beacons.get(beacon.getId());
				d += Math.pow(beacon.getRank() - b.getRank(), 2.0) - Math.pow(b.getRank(), 2.0);
			} else {
				d += Math.pow(beacon.getRank(), 2.0);
			}
		}
		return Math.sqrt(d) / Math.sqrt(maxD);
	}

	public Long getId() {
		return id;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public String getUrn() {
		return urn;
	}

	public void setUrn(String urn) {
		this.urn = urn;
	}
	
	public String toString() {
		return getClass().getSimpleName() +
				"[ID: " + getId() +
				", URN: " + getUrn() +
				", pattern: " + getPattern().toString() + "]";
	}
	
}
