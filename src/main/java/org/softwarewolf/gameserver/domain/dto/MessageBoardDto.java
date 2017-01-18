package org.softwarewolf.gameserver.domain.dto;

import java.util.List;

import org.softwarewolf.gameserver.domain.ItemSelector;
import org.softwarewolf.gameserver.domain.MessageBoardUser;

public class MessageBoardDto {
	private String messageBoardId;
	private String messageBoardName;
	private String newMessageSubject;
	private String newMessageContent;
	private List<MessageBoardUser> userList;
	private Boolean isOwner;
	private Boolean isFirstBoard;
	public List<ItemSelector> messageBoardList;
	public String messageListPreview;

	private String users;
	private String forwardingUrl;
	
	public MessageBoardDto() {
	}
	
	public String getMessageBoardId() {
		 return messageBoardId;
	}
	public void setMessageBoardId(String messageBoardId) {
		this.messageBoardId = messageBoardId;
	}

	public String getMessageBoardName() {
		 return messageBoardName;
	}
	public void setMessageBoardName(String messageBoardName) {
		this.messageBoardName = messageBoardName;
	}
	
	public String getNewMessageSubject() {
		return newMessageSubject;
	}
	public void setNewMessageSubject(String newMessageSubject) {
		this.newMessageSubject = newMessageSubject;
	}
	
	public String getNewMessageContent() {
		return newMessageContent;
	}
	public void setNewMessageContent(String newMessageContent) {
		this.newMessageContent = newMessageContent;
	}
	
	public List<MessageBoardUser> getUserList() {
		return userList;
	}
	public void setUserList(List<MessageBoardUser> userList) {
		this.userList = userList;
	}
	
	public String getForwardingUrl() {
		return forwardingUrl;
	}
	public void setForwardingUrl(String forwardingUrl) {
		this.forwardingUrl = forwardingUrl;
	}
	
	public String getUsers() {
		return users;
	}
	public void setUsers(String users) {
		this.users = users;
	}
	
	public Boolean getIsOwner() {
		return isOwner;
	}
	public void setIsOwner(Boolean isOwner) {
		this.isOwner = isOwner;
	}

	public Boolean getIsFirstBoard() {
		return isFirstBoard;
	}
	public void setIsFirstBoard(Boolean isFirstBoard) {
		this.isFirstBoard = isFirstBoard;
	}
	
	public List<ItemSelector> getMessageBoardList() {
		return messageBoardList;
	}
	public void setMessageBoardList(List<ItemSelector> messageBoardList) {
		this.messageBoardList = messageBoardList;
	}
	
	public String getMessageListPreview() {
		return messageListPreview;
	}
	public void setMessageListPreview(String messageListPreview) {
		this.messageListPreview = messageListPreview;
	}
}
