package ca.idrc.tagin.spi.v1;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import ca.idrc.tagin.model.DoubleContainer;
import ca.idrc.tagin.model.Fingerprint;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;

@Api(
	name = "tagin-api",
	version = "v1"
)
public class URNEndpoints {

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
	
}
