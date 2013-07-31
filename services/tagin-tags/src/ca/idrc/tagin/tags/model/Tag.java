package ca.idrc.tagin.tags.model;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Tag {

	@Id
	private String urn;

	@Basic
	private String label;
	
	public Tag() {
		
	}
	
	public Tag(String urn, String label) {
		this.urn = urn;
		this.label = label;
	}

	public String getUrn() {
		return urn;
	}

	public void setUrn(String urn) {
		this.urn = urn;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String toString() {
		return getClass().getSimpleName() + "[" + 
				"URN: " + getUrn() + 
				", label: " + getLabel() + "]";
	}

}
