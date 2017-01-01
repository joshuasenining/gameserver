package org.softwarewolf.gameserver.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This class maps users onto message boards. The user has a given permission on the board
 * ("PERMISSION_OWNER", "PERMISSION_READ_WRITE", or "PERMISSION_READ")
 */
@Document
public class MessageBoardUser implements Serializable, Comparable<Object> {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	// Permission on the board (PERMISSION_OWNER, PERMISSION_GAMEMASTER, PERMISSION_PLAYER)
	private String permission;
	private String userId;
	private String userName;
	
	public MessageBoardUser() {}
	public MessageBoardUser(String permission, String userId, String username) {
		this.permission = permission;
		this.userId = userId;
		this.userName = username;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}
		
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((permission == null) ? 0 : permission.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessageBoardUser other = (MessageBoardUser) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (permission == null) {
			if (other.permission != null)
				return false;
		} else if (!permission.equals(other.permission))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(Object o) {
		if (o instanceof MessageBoardUser) {
			String otherName = ((MessageBoardUser)o).getUserName();
			return this.userName.compareTo(otherName);
		} else {
			return -1;
		}
	}
	
}
