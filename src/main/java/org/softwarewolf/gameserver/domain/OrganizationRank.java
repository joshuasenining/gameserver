package org.softwarewolf.gameserver.domain;

import org.softwarewolf.gameserver.domain.SimpleTag;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationRank extends GameDataImpl {
	private static final long serialVersionUID = 1L;
	private String organizationId;
	
	public OrganizationRank(String name, String campaignId, String organizationId) {
		super(name, campaignId);
		this.organizationId = organizationId;
		setDisplayName(name);
	}
	
	public OrganizationRank() {
		super();
	}
	
	public String getOrganizationId() {
		return organizationId;
	}
	
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
	
	@Override
	public String getDisplayName() {
		return getName();
	}

	@Override
	public SimpleTag getTag() {
		return new SimpleTag(this.getClass().getSimpleName(), campaignId);
	}
}
