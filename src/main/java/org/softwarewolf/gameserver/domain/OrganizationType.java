package org.softwarewolf.gameserver.domain;

import org.softwarewolf.gameserver.domain.SimpleTag;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationType extends GameDataTypeImpl {
	private static final long serialVersionUID = 1L;

	public OrganizationType() {
		super();
	}
	
	@Override
	public SimpleTag createTag() {
		return new SimpleTag(this.getClass().getSimpleName(), this.getCampaignId());
	}
}
