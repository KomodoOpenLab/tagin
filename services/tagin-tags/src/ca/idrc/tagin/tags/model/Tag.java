package ca.idrc.tagin.tags.model;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Tag {

	@Id
	private String urn;

	@ElementCollection
	private List<String> labels;
	
	public Tag() {
		
	}
	
	public Tag(String urn, List<String> labels) {
		this.urn = urn;
		this.labels = labels;
	}

	public String getUrn() {
		return urn;
	}

	public void setUrn(String urn) {
		this.urn = urn;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}
	
	public void putLabel(String label) {
		labels.add(label);
	}

	public String toString() {
		return getClass().getSimpleName() + "[" + 
				"URN: " + getUrn() + 
				", labels: " + getLabels() + "]";
	}

}
