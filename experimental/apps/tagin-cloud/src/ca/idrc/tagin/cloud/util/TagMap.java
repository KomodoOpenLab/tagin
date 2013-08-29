package ca.idrc.tagin.cloud.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import ca.idrc.tagin.cloud.tag.Tag;

public class TagMap implements Serializable {

	private static final long serialVersionUID = 9111294008139563193L;
	private Map<String,Tag> mTags;
	
	public TagMap() {
		mTags = new LinkedHashMap<String,Tag>();
	}
	
	public void put(String key, Tag tag) {
		if (mTags.containsKey(key)) {
			Tag t = mTags.get(key);
			t.setText(t.getText() + "\n" + tag.getText());
		} else {
			mTags.put(key, tag);
		}
	}
	
	public Tag get(String key) {
		return mTags.get(key);
	}
	
	public boolean containsKey(String key) {
		return mTags.containsKey(key);
	}
	
	public Collection<Tag> values() {
		return mTags.values();
	}
	
	public int size() {
		return mTags.size();
	}
	
	public boolean isEmpty() {
		return mTags.isEmpty();
	}
}
