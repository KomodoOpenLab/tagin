package ca.idrc.tagin.tags.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EMFService {
	
	private static final EntityManagerFactory mFactory = Persistence.createEntityManagerFactory("transactions");
	
	private EMFService() {
		
	}
	
	public static EntityManagerFactory getInstance() {
		return mFactory;
	}
	
	public static EntityManager createEntityManager() {
		return mFactory.createEntityManager();
	}

}
