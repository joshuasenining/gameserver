package org.softwarewolf.gameserver.domain;

import org.softwarewolf.gameserver.domain.SimpleTag;

public interface GameDataType {
	public String getId();	
	public void setId(String id);
	
	public String getName();	
	public void setName(String name);
	
	public String getDescription();
	public void setDescription(String description);
	
	public String getCampaignId();	
	public void setCampaignId(String campaignId);
	
	public SimpleTag createTag();
	
	@Override
	public int hashCode();
	@Override
	public boolean equals(Object obj);
	
}
