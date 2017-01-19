package org.softwarewolf.gameserver.domain;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.TimeZone;

import org.softwarewolf.gameserver.domain.dto.MessagePreview;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document
public class GsMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	private String id;
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
		preview.setMessage(message);
		preview.setMessageId(id);
		preview.setParentMessageId(parentId);
		// TODO: Add the time info here
		//preview.setPostedDateTime(postedDateTime);
		preview.setPosterName(posterName);
		preview.setPosterId(posterId);
		preview.setSubject(subject);
		preview.setThreadId(threadId);
		return preview;
	}
}
