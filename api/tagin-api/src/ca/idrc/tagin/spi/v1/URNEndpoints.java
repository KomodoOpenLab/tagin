package ca.idrc.tagin.spi.v1;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;

import ca.idrc.tagin.dao.TaginDao;
import ca.idrc.tagin.dao.TaginEntityManager;
import ca.idrc.tagin.model.Distance;
import ca.idrc.tagin.model.Fingerprint;
import ca.idrc.tagin.model.Neighbour;
import ca.idrc.tagin.model.URN;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;

@Api(
	name = "tagin",
	version = "v1"
)
public class URNEndpoints {

	@ApiMethod(
			name = "urns.distanceto",
			path = "urns/{URN1}/distanceto/{URN2}",
			httpMethod = HttpMethod.GET
	)
	public Distance getDistanceTo(@Named("URN1") String urn1, @Named("URN2") String urn2) {
		Distance d = null;
		TaginDao dao = new TaginEntityManager();
		Fingerprint f1 = dao.getFingerprint(urn1);
		Fingerprint f2 = dao.getFingerprint(urn2);
		if (f1 != null && f2 != null) {
			d = new Distance(f1.rankDistanceTo(f2));
		}
		dao.close();
		return d;
	}

	@ApiMethod(
			name = "urns.neighbours",
			path = "urns/{URN}/neighbours",
			httpMethod = HttpMethod.GET
	)
	public List<URN> getNeighbours(@Named("URN") String urn, @Nullable @Named("max_count") Integer maxCount) {
		List<URN> neighbours = new ArrayList<URN>();
		TaginDao dao = new TaginEntityManager();
		Fingerprint fp = dao.getFingerprint(urn);
		
		for (Neighbour n : dao.getNeighbours(fp)) {
			if (n.getUrn() != null)
				neighbours.add(new URN(n.getUrn()));
			if (maxCount != null && neighbours.size() >= maxCount)
				break;
		}
		return neighbours;
	}

	@ApiMethod(
			name = "urns.remove",
			path = "urns/{URN}",
			httpMethod = HttpMethod.DELETE
	)
	public Fingerprint removeURN(@Named("URN") String urn) {
		//TODO implement functionality
		return new Fingerprint();
	}
	
}
