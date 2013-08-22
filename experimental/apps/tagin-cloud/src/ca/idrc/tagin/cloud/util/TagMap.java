package ca.idrc.tagin.cloud.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ca.idrc.tagin.cloud.tag.Tag;
import ca.idrc.tagin.cloud.tag.TagGroup;

public class TagMap implements Serializable {

	private static final long serialVersionUID = 9111294008139563193L;
	private Map<String,TagGroup> mTags;
	
	public TagMap() {
		mTags = new LinkedHashMap<String,TagGroup>();
	}
	
	public void put(String key, Tag tag) {
		if (mTags.containsKey(key)) {
			mTags.get(key).addTag(tag);
		} else {
			mTags.put(key, new TagGroup(key, tag));
		}
	}
	
	public TagGroup get(String key) {
		return mTags.get(key);
	}
	
	public boolean containsKey(String key) {
		return mTags.containsKey(key);
	}
	
	public List<Tag> values() {
		List<Tag> tags = new ArrayList<Tag>();
		for (TagGroup tagGroup : mTags.values()) {
			tags.addAll(tagGroup.getTags());
		}
		return tags;
	}
	
	public int size() {
		return mTags.size();
	}
}
