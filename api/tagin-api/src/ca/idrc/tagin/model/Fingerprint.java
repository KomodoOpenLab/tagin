package ca.idrc.tagin.model;

public class Fingerprint implements Comparable<Fingerprint> {

	private Pattern mPattern;
	private Double mRank;
	
	public Fingerprint() {
		
	}
	
	public Pattern getPattern() {
		return mPattern;
	}

	public void setPattern(Pattern mPattern) {
		this.mPattern = mPattern;
	}

	public Double getRank() {
		return mRank;
	}

	public void setRank(Double mRank) {
		this.mRank = mRank;
	}
	
	@Override
	public int compareTo(Fingerprint f) {
		return 0;
	}

}
