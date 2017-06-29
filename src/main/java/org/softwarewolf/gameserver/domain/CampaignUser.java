package org.softwarewolf.gameserver.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This class maps users onto campaigns. The user has a given permission in the campaign
 * ("PERMISSION_OWNER", "PERMISSION_GAMEMASTER", or "PERMISSION_PLAYER")
 * in addition, in the context where a campaign player is used, it also has a permission
 * that reflects the authority that the CampaignUser has over the object in question
 * currently only used for Folio objects and could also be any of the roles above
 */
@Document
public class CampaignUser implements Serializable, Comparable<Object> {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	@Indexed
	private String campaignId;
	// Permission in the campaign (PERMISSION_OWNER, PERMISSION_GAMEMASTER, PERMISSION_PLAYER)
	private String permission;
	private String userId;
	private String userName;
	// Permission for an item (i.e. Folio) if attached to one (PERMISSION_OWNER, PERMISSION_READ_WRITE, PERMISSION_WRITE, NO_PERMISSION) 
	@Transient
	private String itemPermission;
	
	public CampaignUser() {}
	public CampaignUser(String campaignId, String permission, String userId, String username) {
		this.campaignId = campaignId;
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
	
	public String getCampaignId() {
		return campaignId;
	}
	
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
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
	
	public String getItemPermission() {
		return itemPermission;
	}
	
	public void setItemPermission(String itemPermission) {
		this.itemPermission = itemPermission;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((campaignId == null) ? 0 : campaignId.hashCode());
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
		CampaignUser other = (CampaignUser) obj;
		if (campaignId == null) {
			if (other.campaignId != null)
				return false;
		} else if (!campaignId.equals(other.campaignId))
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
		if (o instanceof CampaignUser) {
			String otherName = ((CampaignUser)o).getUserName();
			return this.userName.compareTo(otherName);
		} else {
			return -1;
		}
	}
	
}
