package ca.idrc.tagin.tags.dao;

public interface TagsDao {
	
	public void assignLabel(String urn, String label);
	
	public void beginTransaction();
	
	public void commitTransaction();
	
	public void close();

}
