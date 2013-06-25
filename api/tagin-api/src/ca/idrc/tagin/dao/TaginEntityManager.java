package ca.idrc.tagin.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import ca.idrc.tagin.model.Beacon;
import ca.idrc.tagin.model.Fingerprint;
import ca.idrc.tagin.model.Neighbour;
import ca.idrc.tagin.model.Pattern;
import ca.idrc.tagin.spi.v1.URNManager;

public class TaginEntityManager implements TaginDao {

	private EntityManager mEntityManager;

	public TaginEntityManager() {
		mEntityManager = EMFService.createEntityManager();
	}

	@Override
	public String save(Pattern pattern) {
		Fingerprint fp = new Fingerprint(pattern);
		URNManager.generateURN(fp);
		return fp.getUrn();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Pattern> listPatterns() {
		Query query = mEntityManager.createQuery("select p from Pattern p");
		List<Pattern> patterns = query.getResultList();
		for (Pattern p : patterns) {
			p.getBeacons(); // Forces eager-loading
		}
		return patterns;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Fingerprint> listFingerprints() {
		Query query = mEntityManager.createQuery("select f from Fingerprint f");
		List<Fingerprint> fingerprints = query.getResultList();
		return fingerprints;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Pattern getPattern(Long id) {
		Pattern p = null;
		Query query = mEntityManager.createQuery("select p from Pattern p where p.id = " + id);
		List<Pattern> result = query.getResultList();
		if (result.size() > 0) {
			p = result.get(0);
			if (p != null) 
				p.getBeacons(); // Forces eager-loading
		}
		return p;
	}

	@Override
	public Fingerprint getFingerprint(Long id) {
		Fingerprint fp = mEntityManager.find(Fingerprint.class, id);
		if (fp != null)
			fp.getPattern().getBeacons(); // Forces eager-loading
		return fp;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Neighbour> getNeighbours(Fingerprint fp) {
		List<Neighbour> neighbours = new ArrayList<Neighbour>();
		for (Beacon b : fp.getPattern().getBeacons().values()) {
			Query query = mEntityManager.createQuery("select b from Beacon b where b.id = '" + b.getId() + "'");
			List<Beacon> beacons = query.getResultList();
			if (beacons.size() > 0) {
				Beacon beacon = beacons.get(0);
				Fingerprint f = mEntityManager.find(Fingerprint.class, 
						beacon.getKey().getParent().getParent());
				f.getPattern().getBeacons(); // Forces eager-loading
				neighbours.add(new Neighbour(f, fp.rankDistanceTo(f)));
			}
		}
		return neighbours;
	}

	@Override
	public <T> void remove(Class<T> clazz, Long id) {
		T entity = mEntityManager.find(clazz, id);
		mEntityManager.remove(entity);
	}
	
	public void persistFingerprint(Fingerprint fp) {
		mEntityManager.getTransaction().begin();
		mEntityManager.persist(fp);
		mEntityManager.flush();
		fp.getPattern().setId(fp.getPattern().getKey().getId());
		mEntityManager.getTransaction().commit();
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
