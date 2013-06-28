package ca.idrc.tagin.model;

public class Distance {
	
	private Double distance;
	
	public Distance() {
		this.distance = 0.0;
	}
	
	public Distance(Double distance) {
		this.distance = distance;
	}
	
	public void setValue(Double distance) {
		this.distance = distance;
	}
	
	public Double getValue() {
		return distance;
	}

}
