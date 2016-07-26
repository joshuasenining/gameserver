package org.softwarewolf.gameserver.repository;

import java.util.List;

import org.softwarewolf.gameserver.domain.LocationType;

public interface LocationTypeRepositoryCustom {
	List<LocationType> findByCampaignId(String campaignId);
}
