package ca.idrc.tagin.spi.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import ca.idrc.tagin.dao.EMFService;
import ca.idrc.tagin.model.Beacon;
import ca.idrc.tagin.model.Fingerprint;

public class URNManager {

	public static void generateURN(Fingerprint fp) {
		List<Fingerprint> neighbours = getNeighbours(fp);
		// TODO: merge with neighbours
		UUID urn = java.util.UUID.randomUUID();
		fp.setUrn(urn.toString());
	}

	@SuppressWarnings("unchecked")
	public static List<Fingerprint> getNeighbours(Fingerprint fp) {
		List<Fingerprint> neighbours = new ArrayList<Fingerprint>();
		EntityManager em = EMFService.createEntityManager();
		for (Beacon b : fp.getPattern().getBeacons().values()) {
			Query query = em.createQuery("select b from Beacon b where b.id = '" + b.getId() + "'");
			List<Beacon> beacons = query.getResultList();
			if (beacons.size() > 0) {
				Beacon beacon = beacons.get(0);
				Fingerprint f = em.find(Fingerprint.class, beacon.getKey().getParent().getParent());
				neighbours.add(f);
			}
		}
		return neighbours;
	}

}
