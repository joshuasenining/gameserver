package org.softwarewolf.gameserver.domain;

import org.softwarewolf.gameserver.domain.SimpleTag;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class LocationType extends GameDataTypeImpl {
	private static final long serialVersionUID = 1L;

	public LocationType() {
		super();
	}

	@Override
	public SimpleTag createTag() {
		return new SimpleTag(this.getClass().getSimpleName(), this.getCampaignId());
	}	
}
