package org.softwarewolf.gameserver.domain.dto;

import java.util.List;

import org.softwarewolf.gameserver.domain.Folio;

public class FolioDto {
	private Folio folio;
	private String unselectededTags;
	private String selectedTags;
	private String addTag;
	private String removeTag;
	private List<FolioDescriptor> folioDescriptorList;
	private String viewTagList;
	private String operationType;
	/**
	 * Serialized form of List<Map<String, String>> where each map contains:
	 * id (a CampaignUser.userId)
	 * userName (a CampaignUser.userName)
	 * permission (a CampaignUser.permission - "ROLE_OWNER", "ROLE_USER", or "No Access") 
	 * this is derived from Folo.owners and Folio.users (lists of user.id's)
	 */
	private String owners;
	private String writers;
	private String readers;
	private String forwardingUrl;
	
	public FolioDto() {
		folio = new Folio();
	}
	
	public Folio getFolio() {
		 return folio;
	}
	public void setFolio(Folio folio) {
		this.folio = folio;
	}
	
	public String getUnselectedTags() {
		return unselectededTags;
	}
	public void setUnselectededTags(String unassignedTags) {
		this.unselectededTags = unassignedTags;
	}

	public String getSelectedTags() {
		return selectedTags;
	}
	public void setSelectedTags(String selectedTags) {
		this.selectedTags = selectedTags;
	}
	
	public String getAddTag() {
		return addTag;
	}
	public void setAddTag(String addTag) {
		this.addTag = addTag;
	}
	
	public String getRemoveTag() {
		return removeTag;
	}
	public void setRemoveTag(String removeTag) {
		this.removeTag = removeTag;
	}
	
	public List<FolioDescriptor> getFolioDescriptorList() {
		return folioDescriptorList;
	}
	public void setFolioDescriptorList(List<FolioDescriptor> folioDescriptorList) {
		this.folioDescriptorList = folioDescriptorList;
	}
	
	public String getViewTagList() {
		return viewTagList;
	}
	
	public void setViewTagList(String viewTagList) {
		this.viewTagList = viewTagList;
	}

	public String getOperationType() {
		return operationType;
	}
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}
	
	public String getOwners() {
		return owners;
	}
	public void setOwners(String owners) {
		this.owners = owners;
	}
	
	public String getWriters() {
		return writers;
	}
	public void setWriters(String writers) {
		this.writers = writers;
	}
	
	public String getReaders() {
		return readers;
	}
	public void setReaders(String readers) {
		this.readers = readers;
	}
	
	public String getForwardingUrl() {
		return forwardingUrl;
	}
	public void setForwardingUrl(String forwardingUrl) {
		this.forwardingUrl = forwardingUrl;
	}
}
