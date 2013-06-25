package ca.idrc.tagin.dao;

import java.util.List;

import ca.idrc.tagin.model.Fingerprint;
import ca.idrc.tagin.model.Neighbour;
import ca.idrc.tagin.model.Pattern;

public interface TaginDao {
	
	public String save(Pattern pattern);
	
	public List<Pattern> listPatterns();
	
	public List<Fingerprint> listFingerprints();
	
	public Pattern getPattern(Long id);
	
	public Fingerprint getFingerprint(Long id);
	
	public List<Neighbour> getNeighbours(Fingerprint fp);

	public void persistFingerprint(Fingerprint fp);
	
	public void beginTransaction();
	
	public void commitTransaction();
	
	public <T> void remove(Class<T> clazz, Long id);
	
	public void close();

}
