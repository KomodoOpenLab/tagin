package ca.idrc.tagin.model;

public class Neighbour implements Comparable<Neighbour> {

	private Fingerprint mFingerprint;
	private Double mRankDistance;
	
	public Neighbour(Fingerprint fingerprint, Double rankDistance) {
		mFingerprint = fingerprint;
		mRankDistance = rankDistance;
	}

	public Fingerprint getFingerprint() {
		return mFingerprint;
	}

	public void setFingerprint(Fingerprint fingerprint) {
		mFingerprint = fingerprint;
	}

	public Double getRankDistance() {
		return mRankDistance;
	}

	public void setRankDistance(Double rankDistance) {
		mRankDistance = rankDistance;
	}
	
	@Override
	public int compareTo(Neighbour n) {
		if (n.getRankDistance() > mRankDistance) return -1;
		if (n.getRankDistance() < mRankDistance) return 1;
		return 0;
	}

	public String toString() {
		return getClass().getSimpleName() +
				"[rankDistance: " + getRankDistance() +
				", fingerprint: " + getFingerprint() + "]";
	}
	
}
