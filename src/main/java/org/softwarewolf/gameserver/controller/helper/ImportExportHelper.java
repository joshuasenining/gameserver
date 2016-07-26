package org.softwarewolf.gameserver.controller.helper;

public class ImportExportHelper {
	private String importFilename;
	private String selectedCampaignId;
	private String selectedCampaignName;
	private String campaignListAsString;
	private String forwardingUrl;
	
	public ImportExportHelper() {	
	}
	
	public String getImportFilename() {
		return importFilename;
	}
	public void setImportFilename(String importFilename) {
		this.importFilename = importFilename;
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
