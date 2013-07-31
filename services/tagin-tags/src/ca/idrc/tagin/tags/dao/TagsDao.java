package ca.idrc.tagin.tags.dao;

public interface TagsDao {
	
	public String getLabel(String urn);
	
	public void assignLabel(String urn, String label);
	
	public void close();

}
