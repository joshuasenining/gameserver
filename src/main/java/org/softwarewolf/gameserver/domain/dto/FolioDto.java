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
	private Boolean isOwner;
	
	/**
	 * Serialized form of List<CampaignUser> where we add an itemPermission to 
	 * each CampaignUser to denote that user's permission on the folio:
	 * CampaignUser.itemPermission - "PERMISSION_OWNER", "PERMISSION_READ_WRITE", "PERMISSION_READ", or "No Access") 
	 * this is derived from Folo.owners, Folio.writers, and Folio.readers (lists of user.id's)
	 */
	private String users;
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
	
	public String getUsers() {
		return users;
	}
	public void setUsers(String users) {
		this.users = users;
	}
	public String getForwardingUrl() {
		return forwardingUrl;
	}
	public void setForwardingUrl(String forwardingUrl) {
		this.forwardingUrl = forwardingUrl;
	}
	
	public Boolean getIsOwner() {
		return isOwner;
	}
	public void setIsOwner(Boolean isOwner) {
		this.isOwner = isOwner;
	}
}
