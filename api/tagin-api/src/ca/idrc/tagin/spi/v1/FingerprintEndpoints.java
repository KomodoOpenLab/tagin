package ca.idrc.tagin.spi.v1;

import java.util.List;

import javax.inject.Named;

import ca.idrc.tagin.dao.TaginEntityManager;
import ca.idrc.tagin.model.Fingerprint;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;

@Api(
	name = "tagin",
	version = "v1"
)
public class FingerprintEndpoints {


	@ApiMethod(
			name = "fingerprints.list",
			path = "fingerprints",
			httpMethod = HttpMethod.GET
	)
	public List<Fingerprint> listFingerprints() {
		TaginEntityManager em = new TaginEntityManager();
		List<Fingerprint> fingerprints = em.listFingerprints();
		em.close();
		return fingerprints;
	}

	@ApiMethod(
			name = "fingerprints.get",
			path = "fingerprints/{fingerprint_id}",
			httpMethod = HttpMethod.GET
	)
	public Fingerprint getFingerprint(@Named("fingerprint_id") Long id) {
		TaginEntityManager em = new TaginEntityManager();
		Fingerprint fp = em.getFingerprint(id);
		em.close();
		return fp;
	}

}
