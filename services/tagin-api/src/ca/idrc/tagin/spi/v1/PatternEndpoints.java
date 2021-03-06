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
