package org.softwarewolf.gameserver.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.softwarewolf.gameserver.domain.SimpleTag;
import org.softwarewolf.gameserver.domain.dto.FolioDescriptor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This class is for holding the html pages created by users of the app and
 * associating them with meta data.
 * @author tmanchester
 */
@Document
public class Folio implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	private String id;
	private String title;
	private String campaignId;
	private String content;
	private List<SimpleTag> tags;
	
	public Folio() { }
	public Folio(String campaignId) {
		this.campaignId = campaignId;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getCampaignId() {
		return campaignId;
	}
	
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public List<SimpleTag> getTags() {
		if (tags == null) {
			tags = new ArrayList<>();
		}
		return tags;
	}
	
	public void setTags(List<SimpleTag> tags) {
		this.tags = tags;
	}

	// TODO: Figure out best way to create tag with/without classname
	public void addTag(String tagId) {
		
	}

	public void addTag(SimpleTag tag) {
		if (tags == null) {
			tags = new ArrayList<>();
		}
		if (!tags.contains(tag)) {
			tags.add(tag);
		}
	}

	public void removeTag(String tagName) {
		if (tags != null && tags.contains(tagName)) {
			for (SimpleTag tag : tags) {
				if (tag.getName().equals(tagName)) {
					tags.remove(tag);	
					break;
				}
			}
		}
	}
	
	public FolioDescriptor createDescriptor() {
		return new FolioDescriptor(title, id, tags);
	}

}
