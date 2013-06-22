package ca.idrc.tagin.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import ca.idrc.tagin.model.Fingerprint;
import ca.idrc.tagin.model.Pattern;
import ca.idrc.tagin.spi.v1.URNManager;

public class TaginEntityManager {

	private EntityManager mEntityManager;

	public TaginEntityManager() {
		mEntityManager = EMFService.createEntityManager();
	}

	public String save(Pattern pattern) {
		Fingerprint fp = new Fingerprint(pattern);
		URNManager.generateURN(fp);
		String urn = fp.getUrn();
		mEntityManager.persist(fp);
		return urn;
	}
	
	@SuppressWarnings("unchecked")
	public List<Pattern> listPatterns() {
		Query query = mEntityManager.createQuery("select p from Pattern p");
		List<Pattern> patterns = query.getResultList();
		for (Pattern p : patterns) {
			p.getBeacons(); // Forces eager-loading
		}
		return patterns;
	}
	
	public Pattern getPattern(Long id) {
		Pattern p = mEntityManager.find(Pattern.class, id);
		if (p != null) 
			p.getBeacons(); // Forces eager-loading
		return p;
	}
	
	public <T> void remove(Class<T> clazz, Long id) {
		T entity = mEntityManager.find(clazz, id);
		mEntityManager.remove(entity);
	}
	
	public void close() {
		mEntityManager.close();
	}

}
