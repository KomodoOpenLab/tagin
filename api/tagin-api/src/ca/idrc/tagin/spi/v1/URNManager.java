package ca.idrc.tagin.spi.v1;

import java.util.List;
import java.util.UUID;

import ca.idrc.tagin.dao.TaginDao;
import ca.idrc.tagin.model.Beacon;
import ca.idrc.tagin.model.Fingerprint;
import ca.idrc.tagin.model.Neighbour;
import ca.idrc.tagin.model.Pattern;

public class URNManager {
	
	private static TaginDao dao;
	
	public static void generateURN(TaginDao taginDao, Fingerprint fp) {
		dao = taginDao;
		List<Neighbour> neighbours = fp.findCloseNeighbours();
		if (neighbours.isEmpty()) {
			UUID urn = UUID.randomUUID();
			fp.setUrn(urn.toString().replace("-", ""));
			dao.persistFingerprint(fp);
		} else {
			Neighbour n = neighbours.get(0);
			Fingerprint existingFp = dao.getFingerprint(n.getFingerprintId());
			Pattern p1 = existingFp.getPattern();
			existingFp.merge(fp);
			fp.setUrn(existingFp.getUrn());
			
			// Push away any neighbour that has now become too close
			List<Beacon> changeVector = p1.calculateChangeVector(existingFp.getPattern());
			pushAwayNeighbours(n.getFingerprintId(), changeVector);
		}
	}
	
	private static void pushAwayNeighbours(Long id, List<Beacon> changeVector) {
		Fingerprint fp = dao.getFingerprint(id);
		List<Neighbour> neighbours = fp.findCloseNeighbours();
		for (Neighbour n : neighbours) {
			Fingerprint fn = dao.getFingerprint(n.getFingerprintId());
			fn.displaceBy(changeVector);
		}
		// Propagate changes
		for (Neighbour n : neighbours) {
			pushAwayNeighbours(n.getFingerprintId(), changeVector);
		}
	}
	
}
