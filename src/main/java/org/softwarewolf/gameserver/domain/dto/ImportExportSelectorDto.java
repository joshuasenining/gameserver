package org.softwarewolf.gameserver.domain.dto;

public class ImportExportSelectorDto {
	private String filePath;
	private String selectedCampaignId;
	private String selectedCampaignName;
	private String campaignListAsString;
	private String forwardingUrl;
	
	public ImportExportSelectorDto() {	
	}
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getSelectedCampaignId() {
		return selectedCampaignId;
	}
	public void setSelectedCampaignId(String selectedCampaignId) {
		this.selectedCampaignId = selectedCampaignId;
	}

	public String getSelectedCampaignName() {
		return selectedCampaignName;
	}
	public void setSelectedCampaignName(String selectedCampaignName) {
		this.selectedCampaignName = selectedCampaignName;
	}

	public String getCampaignListAsString() {
		return campaignListAsString;
	}
	public void setCampaignListAsString(String campaignListAsString) {
		this.campaignListAsString = campaignListAsString;
	}
	
	public String getForwardingUrl() {
		return forwardingUrl;
	}
	public void setForwardingUrl(String forwardingUrl) {
		this.forwardingUrl = forwardingUrl;
	}
}
