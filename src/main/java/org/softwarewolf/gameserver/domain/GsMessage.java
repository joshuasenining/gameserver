package org.softwarewolf.gameserver.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import org.softwarewolf.gameserver.domain.dto.MessagePreview;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document
public class GsMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	private String id;
	private String messageBoardId;
	private String subject;
	private String message;
	private String posterName;
	private String posterId;
	private List<String> readByList;
	private String threadId;
	private String parentId;
	private Instant created;
	
	public GsMessage() {
		this.created = Instant.now();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getMessageBoardId() {
		return messageBoardId;
	}
	public void setMessageBoardId(String messageBoardId) {
		this.messageBoardId = messageBoardId;
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
	
	public List<String> getReadByList() {
		return readByList;
	}
	public void setReadByList(List<String> readByList) {
		this.readByList = readByList;
	}
	
	public String getThreadId() {
		return threadId;
	}
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
	
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public Instant getCreated() {
		return created;
	}
	public void setCreated(Instant created) {
		this.created = created;
	}
	
	@JsonIgnore
	public MessagePreview createPreview() {
		MessagePreview preview = new MessagePreview();
		preview.setMessageId(id);
		preview.setThreadId(threadId);
		preview.setParentMessageId(parentId);
		preview.setSubject(subject);
		preview.setMessage(message);
		preview.setCreated(created);
		preview.setPosterName(posterName);
		preview.setPosterId(posterId);
		preview.setCreated(created);
		return preview;
	}
}
