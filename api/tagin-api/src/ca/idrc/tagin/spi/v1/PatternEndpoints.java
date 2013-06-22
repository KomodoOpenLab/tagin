package ca.idrc.tagin.spi.v1;

import java.util.List;

import javax.inject.Named;

import ca.idrc.tagin.dao.TaginEntityManager;
import ca.idrc.tagin.model.Pattern;
import ca.idrc.tagin.model.URN;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;

@Api(
	name = "tagin",
	version = "v1"
)
public class PatternEndpoints {

	@ApiMethod(
			name = "patterns.add",
			path = "patterns",
			httpMethod = HttpMethod.POST
	)
	public URN addPattern(Pattern pattern) {
		TaginEntityManager em = new TaginEntityManager();
		String urn = em.save(pattern);
		em.close();
		return new URN(urn);
	}
	
	@ApiMethod(
			name = "patterns.test",
			path = "patterns/test",
			httpMethod = HttpMethod.POST
	)
	public URN addPatterns() {
		TaginEntityManager em = new TaginEntityManager();
		Pattern p = new Pattern();
		p.put("bssid1", 2400, -65);
		p.put("bssid2", 2600, -70);
		String urn = em.save(p);
		em.close();
		return new URN(urn);
	}

	@ApiMethod(
			name = "patterns.list",
			path = "patterns",
			httpMethod = HttpMethod.GET
	)
	public List<Pattern> listPatterns() {
		TaginEntityManager em = new TaginEntityManager();
		List<Pattern> patterns = em.listPatterns();
		em.close();
		return patterns;
	}
	
	@ApiMethod(
			name = "patterns.get",
			path = "patterns/{pattern_id}",
			httpMethod = HttpMethod.GET
	)
	public Pattern getPattern(@Named("pattern_id") Long id) {
		TaginEntityManager em = new TaginEntityManager();
		Pattern p = em.getPattern(id);
		em.close();
		return p;
	}
	
	@ApiMethod(
			name = "patterns.remove",
			path = "patterns/{pattern_id}",
			httpMethod = HttpMethod.DELETE
	)
	public void removePattern(@Named("pattern_id") Long id) {
		TaginEntityManager em = new TaginEntityManager();
		em.remove(Pattern.class, id);
		em.close();
	}
	
}
