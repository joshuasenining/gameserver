package org.softwarewolf.gameserver.domain.dto;

import java.util.List;

import org.softwarewolf.gameserver.domain.Campaign;

public class SelectCampaignDto {
	public List<Campaign> accessableCampaigns;
	public List<Campaign> inaccessableCampaigns;
	public String selectedCampaignId;
	
	public List<Campaign> getAccessableCampaigns() {
		return accessableCampaigns;
	}
	
	public void setAccessableCampaigns(List<Campaign> accessableCampaigns) {
		this.accessableCampaigns = accessableCampaigns;
	}
	
	public List<Campaign> getInaccessableCampaigns() {
		return inaccessableCampaigns;
	}
	
	public void setInaccessableCampaigns(List<Campaign> inaccessableCampaigns) {
		this.inaccessableCampaigns = inaccessableCampaigns;
	}
	
	public String getSelectedCampaignId() {
		return selectedCampaignId;
	}
	
	public void setSelectedCampaignId(String selectedCampaignId) {
		this.selectedCampaignId = selectedCampaignId;
	}
	
	public String getSelectedCampaignName() {
		String campaignName = null;
		if (selectedCampaignId != null) {
			for (Campaign campaign : accessableCampaigns) {
				if (campaign.getId().equals(selectedCampaignId)) {
					campaignName = campaign.getName();
					break;
				}
			}
		}
		return campaignName;
	}

}
