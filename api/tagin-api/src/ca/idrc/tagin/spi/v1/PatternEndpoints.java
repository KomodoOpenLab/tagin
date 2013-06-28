package ca.idrc.tagin.spi.v1;

import java.util.List;

import javax.inject.Named;

import ca.idrc.tagin.dao.TaginDao;
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
		TaginDao dao = new TaginEntityManager();
		String urn = dao.persistPattern(pattern);
		dao.close();
		return new URN(urn);
	}

	@ApiMethod(
			name = "patterns.test",
			path = "patterns/test",
			httpMethod = HttpMethod.POST
	)
	public URN addPatterns() {
		Pattern p1 = new Pattern();
		p1.put("id1", 2400, -75);
		p1.put("id2", 2600, -50);
		p1.updateRanks();
		
		Pattern p2 = new Pattern();
		p2.put("id2", 2600, -52);
		p2.put("id3", 2400, -78);
		p2.updateRanks();
		
		Pattern p3 = new Pattern();
		p3.put("id2", 2600, -48);
		p3.put("id4", 2400, -45);
		p3.updateRanks();
		
		Pattern p4 = new Pattern();
		p4.put("id8", 2400, -45);
		p4.put("id9", 2400, -60);
		p4.updateRanks();
		
		TaginDao dao = new TaginEntityManager();
		String urn1 = dao.persistPattern(p1);
		String urn2 = dao.persistPattern(p2);
		String urn3 = dao.persistPattern(p3);
		String urn4 = dao.persistPattern(p4);
		dao.close();
		
		System.out.println("URN1: " + urn1);
		System.out.println("URN2: " + urn2);
		System.out.println("URN3: " + urn3);
		System.out.println("URN4: " + urn4);
		return new URN(urn1);
	}

	@ApiMethod(
			name = "patterns.list",
			path = "patterns",
			httpMethod = HttpMethod.GET
	)
	public List<Pattern> listPatterns() {
		TaginDao dao = new TaginEntityManager();
		List<Pattern> patterns = dao.listPatterns();
		dao.close();
		return patterns;
	}
	
	@ApiMethod(
			name = "patterns.get",
			path = "patterns/{pattern_id}",
			httpMethod = HttpMethod.GET
	)
	public Pattern getPattern(@Named("pattern_id") Long id) {
		TaginDao dao = new TaginEntityManager();
		Pattern p = dao.getPattern(id);
		dao.close();
		return p;
	}
	
	@ApiMethod(
			name = "patterns.remove",
			path = "patterns/{pattern_id}",
			httpMethod = HttpMethod.DELETE
	)
	public void removePattern(@Named("pattern_id") Long id) {
		TaginDao dao = new TaginEntityManager();
		dao.removePattern(id);
		dao.close();
	}
	
}
