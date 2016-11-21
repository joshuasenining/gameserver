package org.softwarewolf.gameserver.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.softwarewolf.gameserver.controller.helper.ControllerUtils;
import org.softwarewolf.gameserver.domain.Folio;
import org.softwarewolf.gameserver.domain.SimpleTag;
import org.softwarewolf.gameserver.domain.dto.FolioDescriptor;
import org.softwarewolf.gameserver.domain.dto.FolioDto;
import org.softwarewolf.gameserver.domain.dto.SelectFolioDto;
import org.softwarewolf.gameserver.repository.FolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FolioService implements Serializable {
	private final static String ADD = "add";
	private final static String REMOVE = "remove";
	public final static String EDIT = "edit";
	public final static String VIEW = "view";
	
	@Autowired
	private FolioRepository folioRepository;

	@Autowired
	private SimpleTagService simpleTagService;
	
	@Autowired
	private UserService userService;
	
	private static final long serialVersionUID = 1L;

	public Folio saveFolio(FolioDto folioDto) throws Exception {
		String selectedTagString = folioDto.getSelectedTags();
		ObjectMapper mapper = new ObjectMapper();
		JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, SimpleTag.class);
		List<SimpleTag> selectedTagList = mapper.readValue(selectedTagString, type);

		Folio folio = folioDto.getFolio();
		folio.setTags(selectedTagList);	
		
		String addTagId = folioDto.getAddTag();
		if (addTagId != null && !addTagId.isEmpty()) {
			SimpleTag newTag = simpleTagService.findOne(addTagId);
			if (newTag != null) {
				folio.addTag(newTag);
			}
		}
		String removeTagId = folioDto.getRemoveTag();
		if (removeTagId != null && !removeTagId.isEmpty()) {
			SimpleTag oldTag = simpleTagService.findOne(removeTagId);
			if (oldTag != null) {
				folio.removeTag(oldTag);
			}
		}
		
		if (folio.getTitle() == null || folio.getTitle().isEmpty()) {
			return folio;
		}
		if (folio.getOwnerId() == null || folio.getOwnerId().isEmpty()) {
			String ownerId = userService.getCurrentUserId();
			folio.setOwnerId(ownerId);
			folio.addAllowedUser(ownerId);
		}
		return save(folio);
	}
	
	public Folio save(Folio folio) throws Exception {
		String folioId = folio.getId();
		if (folioId != null && folioId.isEmpty()) {
			folio.setId(null);
		}
		if (folio.getOwnerId() == null) {
			String ownerId = userService.getCurrentUserId();
			folio.setOwnerId(ownerId);
			folio.addAllowedUser(ownerId);
		}
		String errorList = validateFolio(folio);
		if (errorList.length() > 0) {
			throw new Exception(errorList);
		}
		return folioRepository.save(folio);
	}
	
	public String validateFolio(Folio folio) {
		String errorList = "";
		if (folio == null) {
			errorList = "Null folio.";
			return errorList;
		}
		if (folio.getTitle() == null || folio.getTitle().isEmpty()) {
			errorList = "Title may not be empty.";
		}
		if (folio.getContent() == null || folio.getContent().isEmpty()) {
			if (errorList.length() > 0) {
				errorList += "\n";
			}
			errorList += "Content may not be empty.";
		}
		return errorList;
	}
	
	public List<Folio> findAll() {
		return folioRepository.findAll();
	}
	
	public void initFolioDto(FolioDto folioDto, String folioId, String campaignId, String operationType) {
		Folio folio = null;
		if (folioId == null) { 
			folio = new Folio();
		} else {
			folio = folioRepository.findOne(folioId);
		}
		initFolioDto(folioDto, folio, campaignId, operationType);
	}
	
	public void initFolioDto(FolioDto folioDto, Folio folio, String campaignId, String operationType) {
		if (folio == null) {
			folio = new Folio();
			folio.setCampaignId(campaignId);
		}

		folioDto.setFolio(folio);
		folioDto.setOperationType(operationType);
		List<SimpleTag> selectedTagList = folio.getTags();
		Collections.sort(selectedTagList, new SimpleTagCompare());
		String selectedTags = tagListToString(selectedTagList);
		folioDto.setSelectedTags(selectedTags);

		List<SimpleTag> unselectedTagList = simpleTagService.getUnassignedTags(folio);
		Collections.sort(unselectedTagList, new SimpleTagCompare());
		String unselectedTags = tagListToString(unselectedTagList);
		folioDto.setUnselectededTags(unselectedTags);
		
		folioDto.setAddTag(null);
		folioDto.setRemoveTag(null);
		
		folioDto.setFolioDescriptorList(getFolioDescriptorList(folio.getCampaignId(), null, operationType));
	}

	private String tagListToString(List<SimpleTag> simpleTagList) {
		String tagsString = null;
		if (simpleTagList == null || simpleTagList.isEmpty()) {
			return "[]";
		}
		try {
			ObjectMapper mapper = new ObjectMapper();
			tagsString = mapper.writeValueAsString(simpleTagList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tagsString;
	}
	
	public void deleteAll() {
		folioRepository.deleteAll();
	}

	public Folio addTagToFolio(String campaignId, String folioId, String tagId) throws Exception {
		if (tagId == null) {
			throw new Exception("tagId can not be null");
		}
		SimpleTag tag = simpleTagService.findOne(tagId);
		if (tag == null) {
			throw new Exception("Could not locate a tag for id "+tagId);
		}
		
 		if (folioId.equals("null")) {
			folioId = null;
		}
		Folio folio = null;
		if (folioId != null) {
			folio = findOne(folioId);
		}
		if (folio == null) {
			folio = new Folio();
			folio.setCampaignId(campaignId);
			folio.setTitle("Placeholder title");
		} 

		return addTagToFolio(folio, tag);		
	}
	
	public Folio addTagToFolio(String folioId, SimpleTag tag) throws Exception {
		Folio folio = folioRepository.findOne(folioId);
		if (folio != null) {
			return addTagToFolio(folio, tag);
		}
		throw new Exception("Invalid folio id");
	}
	
	public Folio addTagToFolio(Folio folio, SimpleTag tag) {
		folio.addTag(tag);
		if (folio.getId() != null) {
			folio = folioRepository.save(folio);
		}
		return folio;
	}

	public List<FolioDescriptor> getFolioDescriptorList(String campaignId, List<SimpleTag> includeTags, String operationType) {
		List<FolioDescriptor> folioDescriptorList = new ArrayList<>();
		if (includeTags == null) {
			includeTags = new ArrayList<>();
		}
		
		String userId = userService.getCurrentUserId();
		List<Folio> folioList = null;
		if (EDIT.equals(operationType)) {
			folioList = folioRepository.findAllByOwnerId(userId);
		} else {
			folioList = getAllViewableFolios(campaignId, userId);
		}
		if (folioList != null && folioList.size() > 0) {
			for (Folio folio : folioList) {
				List<SimpleTag> folioTags = folio.getTags();
//				if (folioTags != null && !Collections.disjoint(includeTags, folioTags)) {
				if (folioTags != null && folio.getTags().containsAll(includeTags)) {
					folioDescriptorList.add(folio.createDescriptor());
				} else if (includeTags == null) {
					folioDescriptorList.add(folio.createDescriptor());
				}
			}
		}
		return folioDescriptorList;
	}
	
	public List<Folio> getAllViewableFolios(String campaignId, String userId) {
		List<Folio> allowedFolios = folioRepository.findAllByAllowedUsers(userId);

		return allowedFolios;
	}
	
	public List<Folio> getAllEditableFolios(String campaignId, String userId) {
		List<Folio> allowedFolios = folioRepository.findAllByOwnerId(userId);

		return allowedFolios;
	}
	
	public Folio findOne(String id) {
		return folioRepository.findOne(id);
	}
	
	/**
	 * In order to communicate with the back-end the SelectFolioCreator stores the list of selected 
	 * and unselected ObjectTags as a JSON String. They need to be converted back to be manipulated. 
	 * @param campaignId
	 * @param selectFolioDto
	 */ 
	public void initSelectFolioDto(String campaignId, SelectFolioDto selectFolioDto, String operationType) {
		selectFolioDto.setCampaignId(campaignId);
		List<SimpleTag> excludeTags = new ArrayList<>();
		List<SimpleTag> allTags = simpleTagService.getTagList(campaignId, excludeTags);
		
		String unselectedTags = selectFolioDto.getUnselectedTags();
		if (unselectedTags == null) {
			unselectedTags = "";
		}
		String selectedTags = selectFolioDto.getSelectedTags();
		if (selectedTags == null) {
			selectedTags = "";
		}

		SimpleTag addTag = null;
		SimpleTag removeTag = null;
		boolean initUnselectedTags = (unselectedTags.isEmpty() && selectedTags.isEmpty());
		if (initUnselectedTags) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				unselectedTags = mapper.writeValueAsString(allTags);
				selectFolioDto.setUnselectedTags(unselectedTags);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String addTagName = selectFolioDto.getAddTagName();
		String removeTagName = selectFolioDto.getRemoveTagName();
		if (!addTagName.isEmpty() || !removeTagName.isEmpty()) {
			for(SimpleTag tag: allTags) {
				String currentTagName = tag.getName();
				if (addTagName != null && addTagName.equals(currentTagName)) {
					addTag = tag;
					selectFolioDto.setAddTagName(null);
				} else if (removeTagName != null && removeTagName.equals(currentTagName)) {
					removeTag = tag;
					selectFolioDto.setRemoveTagName(null);
				}
			}
		}

		if (addTag != null) {
			createTagList(ADD, selectFolioDto, addTag);
		}
		if (removeTag != null) {
			createTagList(REMOVE, selectFolioDto, removeTag);
		}
		
    	List<FolioDescriptor> folioDescriptorList = getFolioDescriptorList(campaignId, selectFolioDto.getSelectedTagsAsTags(), operationType);
		String listAsString = convertFolioDescriptorListToString(folioDescriptorList);
		selectFolioDto.setFolioDescriptorList(listAsString);
		
		// Need to pick the correct forwarding url, this is just generic
		if (EDIT.equals(operationType)) {
			selectFolioDto.setForwardingUrl(ControllerUtils.EDIT_FOLIO);
		} else if ("view".equals(operationType)) {
			selectFolioDto.setForwardingUrl(ControllerUtils.VIEW_FOLIO);
		}		
	}		
	
	private String convertFolioDescriptorListToString(List<FolioDescriptor> folioDescriptorList) {
		String listAsString = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			listAsString = mapper.writeValueAsString(folioDescriptorList);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listAsString;
	}
	
	private void createTagList(String operation, SelectFolioDto selectFolioDto, SimpleTag tag) {
		if (!ADD.equals(operation) && !REMOVE.equals(operation)) {
			throw new IllegalArgumentException("Only 'add' and remove' are allowable operations.");
		}
		
		ObjectMapper mapper = new ObjectMapper();
		JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, SimpleTag.class);
		List<SimpleTag> unselectedTagList = null;
		List<SimpleTag> selectedTagList = null;

		if (selectFolioDto.getUnselectedTags().isEmpty()) {
			unselectedTagList = new ArrayList<>();
		} else {
			try {
				unselectedTagList = mapper.readValue(selectFolioDto.getUnselectedTags(), type);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (selectFolioDto.getSelectedTags().isEmpty()) {
			selectedTagList = new ArrayList<>();
		} else {
			try {
				selectedTagList = mapper.readValue(selectFolioDto.getSelectedTags(), type);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		

		if (ADD.equals(operation)) {
			if (!selectedTagList.contains(tag)) {
				selectedTagList.add(tag);
			}
			unselectedTagList.remove(tag);
		} else {
			if (!unselectedTagList.contains(tag)) {
				unselectedTagList.add(tag);
			}
			selectedTagList.remove(tag);
		}
		
		try {
			selectFolioDto.setUnselectedTags(mapper.writeValueAsString(unselectedTagList));
			selectFolioDto.setSelectedTags(mapper.writeValueAsString(selectedTagList));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<FolioDescriptor> folioDescriptorList = new ArrayList<>();
		List<Folio> folioList = findFoliosByTags(selectedTagList);
		for (Folio folio : folioList) {
			folioDescriptorList.add(folio.createDescriptor());
		}
		String folioDescriptors = null;
		try {
			folioDescriptors = mapper.writeValueAsString(folioDescriptorList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		selectFolioDto.setFolioDescriptorList(folioDescriptors);
	}
	
	public List<Folio> findFoliosByTags(List<SimpleTag> selectedTags) {
		List<Object> tagNames = new ArrayList<>();
		for (SimpleTag tag : selectedTags) {
			tagNames.add(tag.getName());
		}
		List<Folio> folioList = folioRepository.findAllByKeyValues("tags.name", tagNames.toArray());
		return folioList;
	}
/*	
	public void initViewFolioDto(ViewFolioDto viewFolioDto, String folioId) {
		Folio folio = null;
		if (folioId != null) { 
			folio = folioRepository.findOne(folioId);
		}
		if (folio == null) {
			folio = new Folio();
		}
		viewFolioDto.setFolio(folio);
		
		List<SimpleTag> tagList = folio.getTags();
		String tags = "";
		if (tagList != null) {
			Iterator<SimpleTag> tagIter = tagList.iterator();
			while(tagIter.hasNext()) {
				SimpleTag tag = tagIter.next();
				tags += tag.getName();
				if (tagIter.hasNext()) {
					tags += ", ";
				}
			}
		}
		viewFolioDto.setTags(tags);
	}
*/	
	public List<Folio> deleteByCampaignId(String campaignId) {
		return folioRepository.deleteByCampaignId(campaignId);
	}
}

class SimpleTagCompare implements Comparator<SimpleTag> {
	@Override
	public int compare(SimpleTag o1, SimpleTag o2) {
		return o1.getName().compareTo(o2.getName());
	}
}
