package ca.idrc.tagin.tags.dao;

import javax.persistence.EntityManager;

import ca.idrc.tagin.tags.model.Tag;

public class TagsEntityManager implements TagsDao {

	private EntityManager mEntityManager;

	public TagsEntityManager() {
		mEntityManager = EMFService.createEntityManager();
	}
	
	public String getLabel(String urn) {
		String label = "";
		Tag tag = mEntityManager.find(Tag.class, urn);
		if (tag != null) {
			label = tag.getLabel();
		}
		return label;
	}
	
	public void assignLabel(String urn, String label) {
		Tag tag = new Tag(urn, label);
		mEntityManager.persist(tag);
	}

	@Override
	public void close() {
		mEntityManager.close();
	}

}