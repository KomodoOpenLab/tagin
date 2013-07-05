package ca.idrc.tagin.model;

public class Neighbour implements Comparable<Neighbour> {

	private Fingerprint fingerprint;
	private Double rankDistance;
	
	public Neighbour(Fingerprint fingerprint, Double rankDistance) {
		this.fingerprint= fingerprint;
		this.rankDistance = rankDistance;
	}

	public Fingerprint getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(Fingerprint fingerprint) {
		this.fingerprint = fingerprint;
	}

	public Double getRankDistance() {
		return rankDistance;
	}

	public void setRankDistance(Double rankDistance) {
		this.rankDistance = rankDistance;
	}
	
	@Override
	public int compareTo(Neighbour n) {
		if (n.getRankDistance() > rankDistance) return -1;
		if (n.getRankDistance() < rankDistance) return 1;
		return 0;
	}

	public String toString() {
		return getClass().getSimpleName() +
				"[rankDistance: " + getRankDistance() +
				", fingerprint: " + getFingerprint() + "]";
	}
	
}
