package ca.idrc.tagin.cloud.tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TagGroup implements Serializable {
	
	private static final long serialVersionUID = 1300549683860713614L;
	
	private String mURN;
	private List<Tag> mTags;
	
	public TagGroup() {
		mURN = null;
		mTags = null;
	}
	
	public TagGroup(String urn, Tag tag) {
		mURN = urn;
		mTags = new ArrayList<Tag>();
		addTag(tag);
	}
	
	public void addTag(Tag tag) {
		mTags.add(tag);
	}
	
	public String getURN() {
		return mURN;
	}
	
	public List<Tag> getTags() {
		return mTags;
	}

}
