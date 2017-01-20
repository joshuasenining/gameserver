package org.softwarewolf.gameserver.domain.dto;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MessagePreview {
	private String messageId;
	private String subject;
	private String message;
	private String posterName;
	private String posterId;
	private String created;
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
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public void setCreated(Instant created) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy  hh:mm a").withZone(ZoneId.systemDefault());
		this.created = formatter.format(created);
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
