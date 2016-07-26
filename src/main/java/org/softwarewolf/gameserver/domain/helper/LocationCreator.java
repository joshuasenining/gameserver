package org.softwarewolf.gameserver.domain.helper;

import java.util.List;

import org.softwarewolf.gameserver.domain.Location;
import org.softwarewolf.gameserver.domain.LocationType;

public class LocationCreator {
	public Location location;
	public List<Location> locationsInCampaign;
	public List<LocationType> locationTypesInCampaign;
	public String locationTreeJson;
	public String addGameDataTypeId;
	public String removeGameDataTypeId;
	public String forwardingUrl;
	public Boolean createLocationTag;
	public Boolean createLocationTypeTag;
	
	public LocationCreator() {
		location = new Location();
	}

	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}

	public List<Location> getLocationsInCampaign() {
		return locationsInCampaign;
	}
	
	public void setLocationsInCampaign(List<Location> locationsInCampaign) {
		this.locationsInCampaign = locationsInCampaign;
	}
	
	public List<LocationType> getLocationTypesInCampaign() {
		return locationTypesInCampaign;
	}
	
	public void setLocationTypesInCampaign(List<LocationType> locationTypesInCampaign) {
		this.locationTypesInCampaign = locationTypesInCampaign;
	}
	
	public String getLocationTreeJson() {
		return locationTreeJson;
	}
	
	public void setLocationTreeJson(String locationTreeJson) {
		this.locationTreeJson = locationTreeJson;
	}
	
	public String getAddGameDataTypeId() {
		return addGameDataTypeId;
	}
	
	public void setAddGameDataTypeId(String addGameDataTypeId) {
		this.addGameDataTypeId = addGameDataTypeId;
	}
	
	public String getRemoveGameDataTypeId() {
		return removeGameDataTypeId;
	}
	
	public void setRemoveGameDataTypeId(String removeGameDataTypeId) {
		this.removeGameDataTypeId = removeGameDataTypeId;
	}

	public String getForwardingUrl() {
		return forwardingUrl;
	}
	
	public void setForwardingUrl(String forwardingUrl) {
		this.forwardingUrl = forwardingUrl;
	}
	
	public Boolean getCreateLocationTag() {
		return createLocationTag;
	}
	
	public void setCreateLocationTag(Boolean createLocationTag) {
		this.createLocationTag = createLocationTag;
	}
	
	public Boolean getCreateLocationTypeTag() {
		return createLocationTypeTag;
	}
	
	public void setCreateLocationTypeTag(Boolean createLocationTypeTag) {
		this.createLocationTypeTag = createLocationTypeTag;
	}
}
