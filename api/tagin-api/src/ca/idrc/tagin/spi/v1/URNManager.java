package ca.idrc.tagin.spi.v1;

import java.util.UUID;

import ca.idrc.tagin.dao.TaginDao;
import ca.idrc.tagin.dao.TaginEntityManager;
import ca.idrc.tagin.model.Fingerprint;
import ca.idrc.tagin.model.Neighbour;

public class URNManager {

	public static void generateURN(Fingerprint fp) {
		TaginDao dao = new TaginEntityManager();
		Neighbour neighbour = fp.getClosestNeighbour();
		if (neighbour == null) {
			UUID urn = java.util.UUID.randomUUID();
			fp.setUrn(urn.toString());
			dao.persistFingerprint(fp);
		} else {
			Fingerprint neighbourFp = dao.getFingerprint(neighbour.getFingerprint().getId());
			neighbourFp.merge(fp);
			fp.setUrn(neighbourFp.getUrn());
			// TODO: push away overlapping neighbours
		}
		dao.close();
	}
	
}
