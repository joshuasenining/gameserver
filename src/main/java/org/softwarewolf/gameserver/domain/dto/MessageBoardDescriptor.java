package org.softwarewolf.gameserver.domain.dto;

/**
 * @author tmanchester
 *
 */
public class MessageBoardDescriptor {
	String messageBoardName;
	String messageBoardId;
	
	public MessageBoardDescriptor(String messageBoardName, String messageBoardId) {
		this.messageBoardName = messageBoardName;
		this.messageBoardId = messageBoardId;
	}
	
	public String getMessageBoardName() {
		return messageBoardName;
	}
	public void setMessageBoardName(String messageBoardName) {
		this.messageBoardName = messageBoardName;
	}
	
	public String getMessageBoardId() {
		return messageBoardId;
	}
	public void setMessageBoardId(String messageBoardId) {
		this.messageBoardId = messageBoardId;
	}
}