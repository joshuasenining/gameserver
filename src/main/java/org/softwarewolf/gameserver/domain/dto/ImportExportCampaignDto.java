package org.softwarewolf.gameserver.domain.dto;

import java.util.List;

import org.softwarewolf.gameserver.domain.Campaign;
import org.softwarewolf.gameserver.domain.CampaignUser;
import org.softwarewolf.gameserver.domain.Folio;
import org.softwarewolf.gameserver.domain.SimpleTag;
import org.softwarewolf.gameserver.domain.User;

public class ImportExportCampaignDto {
	private Campaign campaign;
	private List<User> userList;
	private List<Folio> folioList;
	private List<SimpleTag> tagList;
	private List<CampaignUser> campaignUserList;
	
	public Campaign getCampaign() {
		return campaign;
	}
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
	
	public List<User> getUserList() {
		return userList;
	}
	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
	
	public List<Folio> getFolioList() {
		return folioList;
	}
	public void setFolioList(List<Folio> folioList) {
		this.folioList = folioList;
	}
	
	public List<SimpleTag> getTagList() {
		return tagList;
	}
	public void setTagList(List<SimpleTag> tagList) {
		this.tagList = tagList;
	}
	
	public List<CampaignUser> getCampaignUserList() {
		return campaignUserList;
	}
	public void setCampaignUserList(List<CampaignUser> campaignUserList) {
		this.campaignUserList = campaignUserList;
	}
}
