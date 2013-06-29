package ca.idrc.tagin.model;

public class Neighbour implements Comparable<Neighbour> {

	private Long fingerprintId;
	private String urn;
	private Double rankDistance;
	
	public Neighbour(Long fingerprintId, String urn, Double rankDistance) {
		this.fingerprintId = fingerprintId;
		this.urn = urn;
		this.rankDistance = rankDistance;
	}

	public Long getFingerprintId() {
		return fingerprintId;
	}

	public void setFingerprintId(Long fingerprintId) {
		this.fingerprintId = fingerprintId;
	}
	
	public String getUrn() {
		return urn;
	}
	
	public void setUrn(String urn) {
		this.urn = urn;
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
				", URN: " + getUrn() +
				", fingerprintId: " + getFingerprintId() + "]";
	}
	
}
