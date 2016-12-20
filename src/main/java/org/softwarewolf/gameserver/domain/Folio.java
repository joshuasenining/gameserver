package org.softwarewolf.gameserver.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
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
	private List<String> writers;
	private List<String> readers;
	
	public Folio() { 
		owners = new ArrayList<>();
		writers = new ArrayList<>();
		readers = new ArrayList<>();
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
		if (writers != null && writers.contains(owner)) {
			removeUser(writers, owner);
		}
		if (readers != null && readers.contains(owner)) {
			removeUser(readers, owner);
		}
	}

	public void removeOwner(String owner) {
		removeUser(owners, owner);
	}
	
	public List<String> getWriters() {
		if (writers == null) {
			writers = new ArrayList<>();
		}
		return writers;
	}
	
	public void setWriters(List<String> writers) {
		this.writers = writers;
	}
	
	public void addWriter(String writer) {
		if (writers == null) {
			writers = new ArrayList<>();
		}
		if (!writers.contains(writer)) {
			writers.add(writer);
		}
		if (owners != null && owners.contains(writer)) {
			removeUser(owners, writer);
		}
		if (readers != null && readers.contains(writer)) {
			removeUser(readers, writer);
		}
	}
	
	public void removeWriter(String writer) {
		removeUser(writers, writer);
	}
	
	public List<String> getReaders() {
		if (readers == null) {
			readers = new ArrayList<>();
		}
		return readers;
	}

	public void setReaders(List<String> readers) {
		this.readers = readers;
	}
	
	public void addReader(String reader) {
		if (readers == null) {
			readers = new ArrayList<>();
		}
		if (!readers.contains(reader)) {
			readers.add(reader);
		}
		if (owners != null && owners.contains(reader)) {
			removeUser(owners, reader);
		}
		if (writers != null && readers.contains(reader)) {
			removeUser(writers, reader);
		}
	}
	
	public void removeReader(String reader) {
		removeUser(readers, reader);
	}	
	
	public void removeUser(List<String> userList, String removeOwner) {
		if (userList != null && userList.size() == 1 && userList.get(0).equals(removeOwner)) {
			String message = ControllerUtils.getI18nMessage("editFolio.error.canNotRemoveLastOwner");
			throw new RuntimeException(message);
		}
		if (owners != null && owners.contains(removeOwner)) {
			List<String> newUserList = owners.stream().filter(o -> !removeOwner.equals(o)).collect(Collectors.toList());
			userList = newUserList;
		}
	}

	public String getUserPermission(String userId) {
		if (owners.contains(userId)) { 
			return ControllerUtils.PERMISSION_OWNER;
		} else if (writers != null && writers.contains(userId)) {
			return ControllerUtils.PERMISSION_READ_WRITE;
		} else if (readers != null && readers.contains(userId)) { 
			return ControllerUtils.PERMISSION_READ;
		} else {
			return ControllerUtils.NO_ACCESS;
		}
	}

	public FolioDescriptor createDescriptor() {
		return new FolioDescriptor(title, id, tags);
	}

}
