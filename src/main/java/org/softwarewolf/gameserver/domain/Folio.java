package org.softwarewolf.gameserver.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.softwarewolf.gameserver.controller.helper.ControllerUtils;
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
	private List<String> owners;
	private List<String> users;
	
	public Folio() { 
		owners = new ArrayList<>();
		users = new ArrayList<>();
	}
	public Folio(String campaignId) {
		this();
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

	public List<String> getOwners() {
		if (owners == null) {
			owners = new ArrayList<>();
		}
		return owners;
	}
	
	public void setOwners(List<String> owners) {
		this.owners = owners;
	}

	public void addOwner(String owner) {
		if (owners == null) {
			owners = new ArrayList<>();
		}
		if (!owners.contains(owner)) {
			owners.add(owner);
		}
		if (users != null && users.contains(owner)) {
			removeUser(owner);
		}
	}

	public void removeOwner(String removeOwner) {
		if (owners != null && owners.size() == 1 && owners.get(0).equals(removeOwner)) {
			throw new RuntimeException("You can't remove the last owner");
		}
		if (owners != null && owners.contains(removeOwner)) {
			Iterator<String> iter =owners.iterator();
			while (iter.hasNext()) {
				String currentOwner = iter.next();
				if (removeOwner.equals(currentOwner)) {
					owners.remove(currentOwner);	
					break;
				}				
			}
		}
	}

	public List<String> getUsers() {
		if (users == null) {
			users = new ArrayList<>();
		}
		return users;
	}
	
	public void setUsers(List<String> users) {
		this.users = users;
	}
	
	public void addUser(String user) {
		if (users == null) {
			users = new ArrayList<>();
		}
		if (!users.contains(user)) {
			users.add(user);
		}
		if (owners != null && owners.contains(user)) {
			removeOwner(user);
		}
	}
	
	public String getUserPermission(String userId) {
		if (owners.contains(userId)) { 
			return ControllerUtils.ROLE_OWNER;
		} else if (users.contains(userId)) {
			return ControllerUtils.ROLE_USER;
		} else {
			return "";
		}
	}

	public void removeUser(String removeUser) {
		if (users != null && users.contains(removeUser)) {
			Iterator<String> iter =users.iterator();
			while (iter.hasNext()) {
				String currentUser = iter.next();
				if (removeUser.equals(currentUser)) {
					users.remove(currentUser);	
					break;
				}				
			}
		}
	}	
	
	public FolioDescriptor createDescriptor() {
		return new FolioDescriptor(title, id, tags);
	}

}
