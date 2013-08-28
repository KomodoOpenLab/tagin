package ca.idrc.tagin.dao;

import java.util.List;

import ca.idrc.tagin.model.Fingerprint;
import ca.idrc.tagin.model.Neighbour;
import ca.idrc.tagin.model.Pattern;
import ca.idrc.tagin.model.URN;

public interface TaginDao {
	
	/**
	 * Stores the specified pattern in the datastore, and results in generating a URN.
	 * @param pattern pattern to be stored.
	 * @return a new or existing URN.
	 */
	public String persistPattern(Pattern pattern);
	
	/**
	 * Stores the specified fingerprint in the datastore.
	 * @param fp fingerprint to be stored.
	 */
	public void persistFingerprint(Fingerprint fp);
	
	/**
	 * Returns a list of all stored patterns.
	 * @return list of all stored patterns.
	 */
	public List<Pattern> listPatterns();
	
	/**
	 * Returns a list of all stored fingerprints.
	 * @return list of all stored fingerprints.
	 */
	public List<Fingerprint> listFingerprints();
	
	/**
	 * Retrieves a pattern with the specified ID.
	 * @param id the pattern's ID.
	 * @return the corresponding pattern if found, or null.
	 */
	public Pattern getPattern(Long id);
	
	/**
	 * Retrieves a fingerprint with the specified ID.
	 * @param id the fingerprint's ID.
	 * @return the corresponding fingerprint if found, or null.
	 */
	public Fingerprint getFingerprint(Long id);
	
	/**
	 * Retrieves a fingerprint with the specified URN.
	 * @param urn the fingerprint's URN.
	 * @return the corresponding fingerprint if found, or null.
	 */
	public Fingerprint getFingerprint(String urn);
	
	/**
	 * Retrieves the neighbours of the specified fingerprint.
	 * @param fp
	 * @return a list of the fingerprint's neighbours.
	 */
	public List<Neighbour> getNeighbours(Fingerprint fp);
	
	/**
	 * Retrieves at most the specified number of neighbours of the specified fingerprint.
	 * @param fp
	 * @param maxCount the max number of neighbours.
	 * @return a list of the corresponding neighbours' URNs.
	 */
	public List<URN> fetchNumOfNeighbours(Fingerprint fp, Integer maxCount);
	
	/**
	 * Starts a DB transaction.
	 */
	public void beginTransaction();
	
	/**
	 * Commits a DB transaction.
	 */
	public void commitTransaction();
	
	/**
	 * Removes a pattern from the datastore.
	 * @param id the pattern's ID.
	 */
	public void removePattern(Long id);
	
	/**
	 * Removes a fingerprint from the datastore.
	 * @param urn the fingerprint's URN.
	 */
	public void removeFingerprint(String urn);
	
	/**
	 * Closes the DB.
	 */
	public void close();

}
