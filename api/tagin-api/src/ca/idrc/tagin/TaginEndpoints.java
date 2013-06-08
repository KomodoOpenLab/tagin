package ca.idrc.tagin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import ca.idrc.tagin.dao.EMFService;
import ca.idrc.tagin.model.DoubleContainer;
import ca.idrc.tagin.model.Fingerprint;
import ca.idrc.tagin.model.Pattern;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;

@Api(
	name = "tagin-api",
	version = "v1"
)
public class TaginEndpoints {

	@ApiMethod(path = "patterns", httpMethod = HttpMethod.POST)
	public Pattern addPattern(@Named("pattern_bssid") String patternBssid, 
							  @Named("pattern_rssi") Integer patternRssi) {
		EntityManager m = EMFService.createEntityManager();
		Pattern p = new Pattern();
		p.setBSSID(patternBssid);
		p.setRSSI(patternRssi);
		m.persist(p);
		m.close();
		return p;
	}

	@ApiMethod(path = "patterns", httpMethod = HttpMethod.GET)
	public List<Pattern> listPatterns() {
		EntityManager m = EMFService.createEntityManager();
		Query query = m.createQuery("select p from Pattern p");
		List<Pattern> patterns = query.getResultList();
		return patterns;
	}
	
	@ApiMethod(path = "patterns/{pattern_id}", httpMethod = HttpMethod.GET)
	public Pattern getPattern(@Named("pattern_id") String patternId) {
		//TODO implement functionality
		return new Pattern();
	}

	@ApiMethod(path = "fingerprints", httpMethod = HttpMethod.GET)
	public List<Fingerprint> listFingerprints() {
		//TODO implement functionality
		Fingerprint fp1 = new Fingerprint();
		Fingerprint fp2 = new Fingerprint();
		List<Fingerprint> fingerprints = Arrays.asList(fp1, fp2);
		return fingerprints;
	}

	@ApiMethod(path = "fingerprints/{fingerprint_id}", httpMethod = HttpMethod.GET)
	public Fingerprint getFingerprint() {
		//TODO implement functionality
		return new Fingerprint();
	}

	@ApiMethod(path = "URNs/{URN1}/distanceto/{URN2}", httpMethod = HttpMethod.GET)
	public DoubleContainer getDistanceTo(@Named("URN1") String urn1, @Named("URN2") String urn2) {
		//TODO implement functionality
		DoubleContainer d = new DoubleContainer();
		d.setDouble(0.0);
		return d;
	}

	@ApiMethod(path = "URNs/{URN}/neighbours", httpMethod = HttpMethod.GET)
	public List<String> updateTag(@Named("URN") String urn, @Named("max_count") int maxCount) {
		//TODO implement functionality
		return new ArrayList<String>();
	}

	@ApiMethod(path = "URNs/{URN}", httpMethod = HttpMethod.DELETE)
	public Fingerprint removeURN(@Named("URN") String urn) {
		//TODO implement functionality
		return new Fingerprint();
	}

	@ApiMethod(path = "patterns/{pattern_id}", httpMethod = HttpMethod.DELETE)
	public Pattern removePattern(@Named("pattern_id") String patternId) {
		//TODO implement functionality
		return new Pattern();
	}
	
}
