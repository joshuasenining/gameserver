package org.softwarewolf.gameserver.domain.helper;

import java.util.List;

import org.softwarewolf.gameserver.domain.Campaign;
import org.softwarewolf.gameserver.domain.Folio;
import org.softwarewolf.gameserver.domain.Location;
import org.softwarewolf.gameserver.domain.LocationType;
import org.softwarewolf.gameserver.domain.Organization;
import org.softwarewolf.gameserver.domain.OrganizationRank;
import org.softwarewolf.gameserver.domain.OrganizationType;
import org.softwarewolf.gameserver.domain.User;

public class CampaignData {
	private Campaign campaign;
	private List<User> userList;
	private List<Folio> folioList;
	private List<Location> locationList;
	private List<LocationType> locationTypeList;
	private List<Organization> organizationList;
	private List<OrganizationType> organizationTypeList;
	private List<OrganizationRank> organizationRankList;
	
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
	
	public List<Location> getLocationList() {
		return locationList;
	}
	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}
	
	public List<LocationType> getLocationTypeList() {
		return locationTypeList;
	}
	public void setLocationTypeList(List<LocationType> locationTypeList) {
		this.locationTypeList = locationTypeList;
	}

	public List<Organization> getOrganizationList() {
		return organizationList;
	}
	public void setOrganizationList(List<Organization> organizationList) {
		this.organizationList = organizationList;
	}
	
	public List<OrganizationType> getOrganizationTypeList() {
		return organizationTypeList;
	}
	public void setOrganizationTypeList(List<OrganizationType> organizationTypeList) {
		this.organizationTypeList = organizationTypeList;
	}

	public List<OrganizationRank> getOrganizationRankList() {
		return organizationRankList;
	}
	public void setOrganizationRankList(List<OrganizationRank> organizationRankList) {
		this.organizationRankList = organizationRankList;
	}
}
