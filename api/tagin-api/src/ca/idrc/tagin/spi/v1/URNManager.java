package ca.idrc.tagin.spi.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ca.idrc.tagin.dao.TaginEntityManager;
import ca.idrc.tagin.model.Fingerprint;

public class URNManager {

	public static void generateURN(Fingerprint fp) {
		List<Fingerprint> neighbours = findNeighbours(fp);
		// TODO: merge with neighbours
		UUID urn = java.util.UUID.randomUUID();
		fp.setUrn(urn.toString());
	}

	public static List<Fingerprint> findNeighbours(Fingerprint fp) {
		List<Fingerprint> neighbours = new ArrayList<Fingerprint>();
		TaginEntityManager em = new TaginEntityManager();
		neighbours = em.findNeighbours(fp);
		em.close();
		return neighbours;
	}

}
