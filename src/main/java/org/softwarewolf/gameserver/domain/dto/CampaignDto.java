package org.softwarewolf.gameserver.domain.dto;

import java.util.List;

import org.softwarewolf.gameserver.domain.Campaign;
import org.softwarewolf.gameserver.domain.CampaignSelector;
import org.softwarewolf.gameserver.domain.Folio;

public class CampaignDto {
	public String users;
	public String folioTitle;
	public String folioId;
	public Folio campaignFolio;
	public Campaign campaign;
	public Boolean isOwner;
	public List<CampaignSelector> campaignList;
	
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
		return folioTitle;
	}
	public void setFolioTitle(String folioTitle) {
		this.folioTitle = folioTitle;
	}
	
	public String getFolioId() {
		return folioId;
	}
	public void setFolioId(String folioId) {
		this.folioId = folioId;
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
	
	public List<CampaignSelector> getCampaignList() {
		return campaignList;
	}
	public void setCampaignList(List<CampaignSelector> campaignList) {
		this.campaignList = campaignList;
	}
}
