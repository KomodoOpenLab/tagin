package ca.idrc.tagin.model;

import java.io.Serializable;
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

import ca.idrc.tagin.dao.TaginDao;
import ca.idrc.tagin.dao.TaginEntityManager;

@Entity
public class Fingerprint implements Serializable {

	private static final long serialVersionUID = 4658250121896413119L;
	public static final Double THRESHOLD = 0.33;

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

	public List<Neighbour> findNeighbours() {
		List<Neighbour> neighbours = new ArrayList<Neighbour>();
		TaginDao dao = new TaginEntityManager();
		neighbours = dao.getNeighbours(this);
		dao.close();
		return neighbours;
	}

	public List<Neighbour> findCloseNeighbours() {
		List<Neighbour> closeNeighbours = new ArrayList<Neighbour>();
		List<Neighbour> neighbours = findNeighbours();
		Collections.sort(neighbours);
		for (Neighbour n : neighbours) {
			if (n.getRankDistance() < THRESHOLD) {
				closeNeighbours.add(n);
			} else {
				break;
			}
		}
		return closeNeighbours;
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

	public void merge(Fingerprint fp) {
		Map<String,Beacon> beacons = new HashMap<String,Beacon>();
		for (Beacon beacon : this.getPattern().getBeacons().values()) {
			beacon.setRank(beacon.getRank() / 2);
			beacons.put(beacon.getId(), beacon);
		}

		for (Beacon beacon : fp.getPattern().getBeacons().values()) {
			if (beacons.containsKey(beacon.getId())) {
				Beacon b = beacons.get(beacon.getId());
				b.setRank(b.getRank() + (beacon.getRank() / 2));
				beacons.put(b.getId(), b);
			} else {
				beacon.setRank(beacon.getRank() / 2);
				beacons.put(beacon.getId(), beacon);
			}
		}
		pattern.setBeacons(beacons);
	}
	
	public void displaceBy(List<Beacon> changeVector) {
		Map<String,Beacon> beacons = new HashMap<String,Beacon>();
		for (Beacon beacon : this.getPattern().getBeacons().values()) {
			beacons.put(beacon.getId(), beacon);
		}

		for (Beacon beacon : changeVector) {
			if (beacons.containsKey(beacon.getId())) {
				Beacon b = beacons.get(beacon.getId());
				b.setRank(b.getRank() + beacon.getRank());
				beacons.put(beacon.getId(), b);
			} else {
				beacons.put(beacon.getId(), beacon);
			}
		}
		pattern.setBeacons(beacons);
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
