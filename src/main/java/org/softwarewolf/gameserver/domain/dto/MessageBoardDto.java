package org.softwarewolf.gameserver.domain.dto;

import java.util.List;

import org.softwarewolf.gameserver.domain.MessageBoard;
import org.softwarewolf.gameserver.domain.MessageBoardUser;

public class MessageBoardDto {
	private MessageBoard messageBoard;
	private List<MessageBoardDescriptor> messageBoardDescriptorList;
	private List<MessageBoardUser> userList;
	private Boolean isOwner;
	private Boolean isFirstBoard;

	private String users;
	private String forwardingUrl;
	
	public MessageBoardDto() {
		messageBoard = new MessageBoard();
	}
	
	public MessageBoard getMessageBoard() {
		 return messageBoard;
	}
	public void setMessageBoard(MessageBoard messageBoard) {
		this.messageBoard = messageBoard;
	}
	
	public List<MessageBoardDescriptor> getMessageBoardDescriptorList() {
		return messageBoardDescriptorList;
	}
	public void setMessageBoardDescriptorList(List<MessageBoardDescriptor> messageBoardDescriptorList) {
		this.messageBoardDescriptorList = messageBoardDescriptorList;
		if (messageBoardDescriptorList == null || messageBoardDescriptorList.isEmpty()) {
			setIsFirstBoard(Boolean.TRUE);
		} else {
			setIsFirstBoard(Boolean.FALSE);
		}
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
}
