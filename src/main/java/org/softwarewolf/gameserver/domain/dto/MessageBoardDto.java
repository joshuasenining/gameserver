package org.softwarewolf.gameserver.domain.dto;

import java.util.List;

import org.softwarewolf.gameserver.domain.ItemSelector;
import org.softwarewolf.gameserver.domain.MessageBoardUser;

public class MessageBoardDto {
	private String messageBoardId;
	private String messageBoardName;
	private String messageId;
	private String messageParentId;
	private String threadId;
	private String messageSubject;
	private String messageContent;
	private List<MessageBoardUser> userList;
	private Boolean isOwner;
	private Boolean isFirstBoard;
	private Boolean showPreviewList;
	private Boolean editMessage;
	private List<ItemSelector> messageBoardList;
	private String messagePreviewList;
	private String subjectString;

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

	public String getMessageId() {
		 return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getMessageParentId() {
		 return messageParentId;
	}
	public void setMessageParentId(String messageParentId) {
		this.messageParentId = messageParentId;
	}

	public String getThreadId() {
		 return threadId;
	}
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public String getMessageBoardName() {
		 return messageBoardName;
	}
	public void setMessageBoardName(String messageBoardName) {
		this.messageBoardName = messageBoardName;
	}
	
	public String getMessageSubject() {
		return messageSubject;
	}
	public void setMessageSubject(String messageSubject) {
		this.messageSubject = messageSubject;
	}
	
	public String getMessageContent() {
		return messageContent;
	}
	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
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

	public Boolean getShowPreviewList() {
		return showPreviewList;
	}
	public void setShowPreviewList(Boolean showPreviewList) {
		this.showPreviewList = showPreviewList;
	}

	public Boolean getEditMessage() {
		return editMessage;
	}
	public void setEditMessage(Boolean editMessage) {
		this.editMessage = editMessage;
	}

	public List<ItemSelector> getMessageBoardList() {
		return messageBoardList;
	}
	public void setMessageBoardList(List<ItemSelector> messageBoardList) {
		this.messageBoardList = messageBoardList;
	}
	
	public String getMessagePreviewList() {
		return messagePreviewList;
	}
	public void setMessagePreviewList(String messagePreviewList) {
		this.messagePreviewList = messagePreviewList;
	}

	public String getSubjectString() {
		return subjectString;
	}
	public void setSubjectString(String subjectString) {
		this.subjectString = subjectString;
	}
}
