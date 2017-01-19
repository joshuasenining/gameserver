package org.softwarewolf.gameserver.domain.dto;

public class MessagePreview {
	private String messageId;
	private String subject;
	private String message;
	private String posterName;
	private String posterId;
	private String postedDateTime;
	private String parentMessageId;
	private String threadId;
	
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getPosterName() {
		return posterName;
	}
	public void setPosterName(String posterName) {
		this.posterName = posterName;
	}
	public String getPosterId() {
		return posterId;
	}
	public void setPosterId(String posterId) {
		this.posterId = posterId;
	}
	public String getPostedDateTime() {
		return postedDateTime;
	}
	public void setPostedDateTime(String postedDateTime) {
		this.postedDateTime = postedDateTime;
	}
	public String getParentMessageId() {
		return parentMessageId;
	}
	public void setParentMessageId(String parentMessageId) {
		this.parentMessageId = parentMessageId;
	}
	public String getThreadId() {
		return threadId;
	}
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
}
