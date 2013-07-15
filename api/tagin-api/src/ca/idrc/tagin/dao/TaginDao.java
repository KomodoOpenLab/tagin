package ca.idrc.tagin.dao;

import java.util.List;

import ca.idrc.tagin.model.Fingerprint;
import ca.idrc.tagin.model.Neighbour;
import ca.idrc.tagin.model.Pattern;

public interface TaginDao {
	
	public String persistPattern(Pattern pattern);
	
	public void persistFingerprint(Fingerprint fp);
	
	public List<Pattern> listPatterns();
	
	public List<Fingerprint> listFingerprints();
	
	public Pattern getPattern(Long id);
	
	public Fingerprint getFingerprint(Long id);
	
	public Fingerprint getFingerprint(String urn);
	
	public List<Neighbour> getNeighbours(Fingerprint fp);
	
	public void beginTransaction();
	
	public void commitTransaction();
	
	public void removePattern(Long id);
	
	public void removeFingerprint(String urn);
	
	public void close();

}
