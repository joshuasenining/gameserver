package org.softwarewolf.gameserver.domain.dto;

import java.util.List;

import org.softwarewolf.gameserver.domain.Campaign;
import org.softwarewolf.gameserver.domain.ItemSelector;
import org.softwarewolf.gameserver.domain.Folio;

public class CampaignDto {
	public String users;
	public Folio campaignFolio;
	public Campaign campaign;
	public Boolean isOwner;
	public List<ItemSelector> campaignList;
	
	public CampaignDto() {
		campaign = new Campaign();
	}
	
	public String getUsers() {
		return users;
	}	
	public void setUsers(String users) {
		this.users = users;
	}
	
	public String getFolioTitle() {
		return campaignFolio.getTitle();
	}
	public void setFolioTitle(String folioTitle) {
		this.campaignFolio.setTitle(folioTitle);
	}
	
	public String getFolioId() {
		return campaignFolio.getId();
	}
	public void setFolioId(String folioId) {
		this.campaignFolio.setId(folioId);
	}
	
	public Campaign getCampaign() {
		return campaign;
	}
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
	
	public Folio getCampaignFolio() {
		return campaignFolio;
	}
	public void setCampaignFolio(Folio campaignFolio) {
		this.campaignFolio = campaignFolio;
	}
	
	public Boolean getIsOwner() {
		return isOwner;
	}
	public void setIsOwner(Boolean isOwner) {
		this.isOwner = isOwner;
	}
	
	public List<ItemSelector> getCampaignList() {
		return campaignList;
	}
	public void setCampaignList(List<ItemSelector> campaignList) {
		this.campaignList = campaignList;
	}
}
