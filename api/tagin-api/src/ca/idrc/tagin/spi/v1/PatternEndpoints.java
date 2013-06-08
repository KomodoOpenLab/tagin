package ca.idrc.tagin.spi.v1;

import java.util.List;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import ca.idrc.tagin.dao.EMFService;
import ca.idrc.tagin.model.Pattern;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;

@Api(
	name = "tagin-api",
	version = "v1"
)
public class PatternEndpoints {

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

	@ApiMethod(path = "patterns/{pattern_id}", httpMethod = HttpMethod.DELETE)
	public Pattern removePattern(@Named("pattern_id") String patternId) {
		//TODO implement functionality
		return new Pattern();
	}
	
}
