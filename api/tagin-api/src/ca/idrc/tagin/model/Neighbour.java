package ca.idrc.tagin.model;

public class Neighbour implements Comparable<Neighbour> {

	private Long fingerprintId;
	private Double rankDistance;
	
	public Neighbour(Long fingerprintId, Double rankDistance) {
		this.fingerprintId = fingerprintId;
		this.rankDistance = rankDistance;
	}

	public Long getFingerprintId() {
		return fingerprintId;
	}

	public void setFingerprint(Long fingerprintId) {
		this.fingerprintId = fingerprintId;
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
				", fingerprintId: " + getFingerprintId() + "]";
	}
	
}
