package org.softwarewolf.gameserver.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class Location extends GameDataImpl {
	private static final long serialVersionUID = 1L;
	
	public Location(String name, String campaignId) {
		super(name, campaignId);
	}
	
	public Location() {
		super();
	}

	@Override
	public SimpleTag getTag() {
		return new SimpleTag(this.getClass().getSimpleName(), campaignId);
	}	
}
