package ca.idrc.tagin.tags.dao;

import java.util.List;

public interface TagsDao {
	
	public List<String> getLabels(String urn);
	
	public void assignLabel(String urn, String label);
	
	public void close();

}
