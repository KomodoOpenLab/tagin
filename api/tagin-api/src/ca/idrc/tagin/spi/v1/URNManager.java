package ca.idrc.tagin.spi.v1;

import java.util.UUID;

import ca.idrc.tagin.model.Fingerprint;
import ca.idrc.tagin.model.Neighbour;

public class URNManager {

	public static void generateURN(Fingerprint fp) {
		Neighbour neighbour = fp.getClosestNeighbour();
		if (neighbour == null) {
			UUID urn = java.util.UUID.randomUUID();
			fp.setUrn(urn.toString());
		} else {
			// TODO: merge with neighbours
			// Dummy code, pending implementation of merge
			UUID urn = java.util.UUID.randomUUID();
			fp.setUrn(urn.toString());
		}
	}


}
