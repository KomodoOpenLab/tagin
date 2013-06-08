package ca.idrc.tagin.spi.v1;

import java.util.Arrays;
import java.util.List;

import ca.idrc.tagin.model.Fingerprint;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;

@Api(
	name = "tagin-api",
	version = "v1"
)
public class FingerprintEndpoints {


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
	
}
