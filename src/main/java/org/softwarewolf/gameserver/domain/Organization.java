package org.softwarewolf.gameserver.domain;

import org.softwarewolf.gameserver.domain.SimpleTag;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class Organization extends GameDataImpl {
	private static final long serialVersionUID = 1L;
	
	public Organization(String name, String campaignId) {
		super(name, campaignId);
	}
	
	public Organization() {
		super();
	}
	
	@Override
	public SimpleTag getTag() {
		return new SimpleTag(this.getClass().getSimpleName(), campaignId);
	}
}
