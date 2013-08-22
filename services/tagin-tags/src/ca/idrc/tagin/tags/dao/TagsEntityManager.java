package ca.idrc.tagin.tags.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import ca.idrc.tagin.tags.model.Tag;

public class TagsEntityManager implements TagsDao {

	private EntityManager mEntityManager;

	public TagsEntityManager() {
		mEntityManager = EMFService.createEntityManager();
	}
	
	public List<String> getLabels(String urn) {
		List<String> labels = new ArrayList<String>();
		Tag tag = mEntityManager.find(Tag.class, urn);
		if (tag != null) {
			labels = tag.getLabels();
		}
		return labels;
	}
	
	public void assignLabel(String urn, String label) {
		Tag tag = mEntityManager.find(Tag.class, urn);
		if (tag != null) {
			tag.putLabel(label);
		} else {
			List<String> labels = new ArrayList<String>();
			labels.add(label);
			Tag t = new Tag(urn, labels);
			mEntityManager.persist(t);
		}
	}

	@Override
	public void close() {
		mEntityManager.close();
	}

}
