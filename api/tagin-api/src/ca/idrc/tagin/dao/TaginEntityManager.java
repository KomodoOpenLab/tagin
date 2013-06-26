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
	public String persistPattern(Pattern pattern) {
		Fingerprint fp = new Fingerprint(pattern);
		URNManager.generateURN(fp);
		return fp.getUrn();
	}

	@Override
	public void persistFingerprint(Fingerprint fp) {
		mEntityManager.getTransaction().begin();
		mEntityManager.persist(fp);
		mEntityManager.flush();
		fp.getPattern().setId(fp.getPattern().getKey().getId());
		mEntityManager.getTransaction().commit();
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
	
	@Override
	public Pattern getPattern(Long id) {
		Pattern p = findPattern(id);
		if (p != null)
			p.getBeacons(); // Forces eager-loading
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
			// TODO use this query instead when KEY() method is implemented in Google Datanucleus JPQL
			//Query query = mEntityManager.createQuery("select p from Pattern p join p.beacons b where KEY(b) = :bId");
			//query.setParameter("bId", "'" + b.getId() + "'");
			Query query = mEntityManager.createQuery("select p from Pattern p");
			List<Pattern> patterns = query.getResultList();

			for (Pattern p : patterns) {
				if (p.contains(b.getId())) {
					Fingerprint f = mEntityManager.find(Fingerprint.class, p.getKey().getParent());
					if (f.getId() != fp.getId()) {
						f.getPattern().getBeacons(); // Forces eager-loading
						neighbours.add(new Neighbour(f, fp.rankDistanceTo(f)));
					}
				}
			}
		}
		return neighbours;
	}
	
	@SuppressWarnings("unchecked")
	private Pattern findPattern(Long id) {
		Pattern p = null;
		Query query = mEntityManager.createQuery("select p from Pattern p where p.id = " + id);
		List<Pattern> result = query.getResultList();
		if (result.size() > 0) {
			p = result.get(0);
		}
		return p;
	}
	
	@Override
	public void removePattern(Long id) {
		Pattern p = findPattern(id);
		Fingerprint parent = mEntityManager.find(Fingerprint.class, p.getKey().getParent());
		mEntityManager.remove(parent);
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
