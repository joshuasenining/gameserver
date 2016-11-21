package org.softwarewolf.gameserver.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

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
	private String ownerId;
	private String content;
	private List<SimpleTag> tags;
	private List<String> allowedUsers;
	
	public Folio() { }
	public Folio(String campaignId) {
		this.campaignId = campaignId;
		this.id = UUID.randomUUID().toString();
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
	
	public String getOwnerId() {
		return ownerId;
	}
	
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
		addAllowedUser(ownerId);
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

	public void addTag(SimpleTag tag) {
		if (tags == null) {
			tags = new ArrayList<>();
		}
		if (!tags.contains(tag)) {
			tags.add(tag);
		}
	}

	public void removeTag(SimpleTag tag) {
		if (tags != null && tags.contains(tag)) {
			Iterator<SimpleTag> iter = tags.iterator();
			while (iter.hasNext()) {
				SimpleTag currentTag = iter.next();
				if (currentTag.equals(tag)) {
					tags.remove(tag);	
					break;
				}				
			}
		}
	}

	public List<String> getAllowedUsers() {
		if (allowedUsers == null) {
			allowedUsers = new ArrayList<>();
		}
		return allowedUsers;
	}
	
	public void setAllowedUsers(List<String> allowedUsers) {
		this.allowedUsers = allowedUsers;
		if (!allowedUsers.contains(ownerId)) {
			addAllowedUser(ownerId);
		}
	}

	public void addAllowedUser(String allowedUser) {
		if (allowedUsers == null) {
			allowedUsers = new ArrayList<>();
		}
		if (!allowedUsers.contains(allowedUser)) {
			allowedUsers.add(allowedUser);
		}
	}

	public void removeAllowedUser(String removeUser) {
		if (removeUser.equals(ownerId)) {
			return;
		}
		if (allowedUsers != null && allowedUsers.contains(removeUser)) {
			Iterator<String> iter = allowedUsers.iterator();
			while (iter.hasNext()) {
				String currentUser = iter.next();
				if (removeUser.equals(currentUser)) {
					allowedUsers.remove(currentUser);	
					break;
				}				
			}
		}
	}
	
	public FolioDescriptor createDescriptor() {
		return new FolioDescriptor(title, id, tags);
	}

}
