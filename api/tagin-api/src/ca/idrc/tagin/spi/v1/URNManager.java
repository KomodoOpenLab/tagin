package ca.idrc.tagin.spi.v1;

import java.util.UUID;

import ca.idrc.tagin.model.Fingerprint;

public class URNManager {
	
	public static void generateURN(Fingerprint fp) {
		// TODO: check for neighbouring fingerprints
		UUID urn = java.util.UUID.randomUUID();
		fp.setUrn(urn.toString());
	}

}
