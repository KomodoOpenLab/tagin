package ca.idrc.tagin.tags.dao;

import javax.persistence.EntityManager;

import ca.idrc.tagin.tags.model.Tag;

public class TagsEntityManager implements TagsDao {

	private EntityManager mEntityManager;

	public TagsEntityManager() {
		mEntityManager = EMFService.createEntityManager();
	}
	
	public void assignLabel(String urn, String label) {
		Tag tag = new Tag(urn, label);
		mEntityManager.persist(tag);
	}

	@Override
	public void close() {
		mEntityManager.close();
	}
	
	@Override
	public void beginTransaction() {
		mEntityManager.getTransaction().begin();
	}
	
	@Override
	public void commitTransaction() {
		mEntityManager.getTransaction().commit();
	}

}
