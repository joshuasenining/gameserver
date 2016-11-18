package org.softwarewolf.gameserver.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class CampaignUser implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String campaignId;
	private String role;
	private String userId;
	
	public CampaignUser() {}
	public CampaignUser(String campaignId, String role, String userId) {
		this.campaignId = campaignId;
		this.role = role;
		this.userId = userId;
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
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
		
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
