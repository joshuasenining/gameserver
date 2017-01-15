package org.softwarewolf.gameserver.domain.dto;

import java.util.List;

import org.softwarewolf.gameserver.domain.ItemSelector;
import org.softwarewolf.gameserver.domain.MessageBoard;
import org.softwarewolf.gameserver.domain.MessageBoardUser;

public class EditMessageBoardDto {
	private MessageBoard messageBoard;
	private List<MessageBoardUser> userList;
	private Boolean isOwner;
	private Boolean isFirstBoard;
	public List<ItemSelector> messageBoardList;

	private String users;
	private String forwardingUrl;
	
	public EditMessageBoardDto() {
		messageBoard = new MessageBoard();
	}
	
	public MessageBoard getMessageBoard() {
		 return messageBoard;
	}
	public void setMessageBoard(MessageBoard messageBoard) {
		this.messageBoard = messageBoard;
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
}
