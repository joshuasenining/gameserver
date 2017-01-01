package org.softwarewolf.gameserver.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.softwarewolf.gameserver.domain.dto.MessageBoardDescriptor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class MessageBoard implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String name;
	private List<String> ownerList;
	private List<String> writerList;
	private List<String> readerList;
	private String description;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<String> getOwnerList() {
		if (ownerList == null) {
			ownerList = new ArrayList<>();
		}
		return ownerList;
	}
	public void setOwnerList(List<String> ownerList) {
		this.ownerList = ownerList;
	}
	public void addOwner(String owner) {
		addUser(getOwnerList(), owner);
	}
	
	public List<String> getWriterList() {
		if (writerList == null) {
			writerList = new ArrayList<>();
		}
		return writerList;
	}
	public void setWriterList(List<String> writerList) {
		this.writerList = writerList;
	}
	public void addWriter(String writer) {
		addUser(getWriterList(), writer);
	}
	
	public List<String> getReaderList() {
		if (readerList == null) {
			readerList = new ArrayList<>();
		}
		return readerList;
	}
	public void setReaderList(List<String> readerList) {
		this.readerList = readerList;
	}
	public void addReader(String reader) { 
		addUser(getReaderList(), reader);
	}
	
	private void addUser(List<String> userList, String user) {
		if (userList == null) {
			userList = new ArrayList<>();
		}
		if (!userList.contains(user)) {
			userList.add(user);
		}
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public MessageBoardDescriptor createDescriptor() {
		return new MessageBoardDescriptor(name, id);
	}
	
	public boolean isOwner(String userId) {
		return getOwnerList().contains(userId);
	}
	
	public boolean canView(String userId) {
		return (getOwnerList().contains(userId) || getWriterList().contains(userId) || 
				getReaderList().contains(userId));
	}	
}

