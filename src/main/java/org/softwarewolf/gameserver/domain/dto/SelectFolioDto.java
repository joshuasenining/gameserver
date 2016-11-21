package org.softwarewolf.gameserver.domain.dto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.softwarewolf.gameserver.domain.SimpleTag;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SelectFolioDto {
	private String campaignId; 
	private String unselectedTags;
	private String selectedTags;
	private String addTagName;
	private String removeTagName;
	private String folioDescriptorList;
	private String operationType;
	private String forwardingUrl;
	
	public SelectFolioDto() {
		unselectedTags = "";
		selectedTags = "";
		addTagName = "";
		removeTagName = "";
		folioDescriptorList = "";
	}
	
	public String getUnselectedTags() {
		return unselectedTags;
	}
	public void setUnselectedTags(String unselectedTags) {
		this.unselectedTags = unselectedTags;
	}
	
	public String getSelectedTags() {
		return selectedTags;
	}
	public void setSelectedTags(String selectedTags) {
		this.selectedTags = selectedTags;
	}
	public List<SimpleTag> getSelectedTagsAsTags() {
		List<SimpleTag> simpleTagList = new ArrayList<>();
		if (selectedTags != null && !selectedTags.isEmpty()) {
			ObjectMapper mapper = new ObjectMapper();
			JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, SimpleTag.class);

			try {
				simpleTagList = mapper.readValue(getSelectedTags(), type);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		return simpleTagList;
	}
	
	public String getAddTagName() {
		return addTagName;
	}
	public void setAddTagName(String addTagName) {
		this.addTagName = addTagName;
	}
	
	public String getRemoveTagName() {
		return removeTagName;
	}
	public void setRemoveTagName(String removeTagName) {
		this.removeTagName = removeTagName;
	}

	public String getFolioDescriptorList() {
		return folioDescriptorList;
	}
	public void setFolioDescriptorList(String folioDescriptorList) {
		this.folioDescriptorList = folioDescriptorList;
	}

	public String getForwardingUrl() {
		return forwardingUrl;
	}
	public void setForwardingUrl(String forwardingUrl) {
		this.forwardingUrl = forwardingUrl;
	}
	
	public String getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
	
	public String getOperationType() {
		return operationType;
	}
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}	
}

