package org.softwarewolf.gameserver.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.softwarewolf.gameserver.controller.utils.GetPermissionsFrom;
import org.softwarewolf.gameserver.domain.CampaignUser;
import org.softwarewolf.gameserver.domain.Folio;
import org.softwarewolf.gameserver.domain.SimpleTag;
import org.softwarewolf.gameserver.domain.dto.FolioDescriptor;
import org.softwarewolf.gameserver.domain.dto.FolioDto;
import org.softwarewolf.gameserver.domain.dto.SelectFolioDto;
import org.softwarewolf.gameserver.repository.FolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
	
	@Autowired
	private CampaignUserService campaignUserService;
	
	private static final long serialVersionUID = 1L;

	public Folio saveFolio(FolioDto folioDto) throws Exception {
		String selectedTagString = folioDto.getSelectedTags();
		ObjectMapper mapper = new ObjectMapper();
		JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, SimpleTag.class);
		List<SimpleTag> selectedTagList = null;
		if (selectedTagString.isEmpty()) { 
			selectedTagList = new ArrayList<>();
		} else { 
			selectedTagList = mapper.readValue(selectedTagString, type); 
		}

		Folio folio = folioDto.getFolio();
		folio.setTags(selectedTagList);

		// Put the values for the use permissions from the dto into the folio
		usersFromDtoIntoFolio(folioDto);
		
		addRemoveTagsToFolio(folioDto);
		
		if (folio.getTitle() == null || folio.getTitle().isEmpty()) {
			return folio;
		}
		
		return save(folio);
	}
	
	public void addRemoveTagsToFolio(FolioDto folioDto) {
		Folio folio = folioDto.getFolio();
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
		folioDto.setAddTag(null);
		folioDto.setRemoveTag(null);
	}
	
	public Folio save(Folio folio) throws Exception {
		String folioId = folio.getId();
		if (folioId != null && folioId.isEmpty()) {
			folio.setId(null);
		}
		if (folio.getOwners() == null || folio.getOwners().isEmpty()) {
			String ownerId = userService.getCurrentUserId();
			folio.addOwner(ownerId);
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
	
	public FolioDto initFolioDto(String folioId, FolioDto folioDto, String campaignId, String operationType) {
		Folio folio = initFolio(folioId, campaignId);
		if (folioDto == null) {
			folioDto = new FolioDto();
		}
		folioDto.setFolio(folio);
		
		return initFolioDto(folioDto, campaignId, operationType, GetPermissionsFrom.FOLIO);
	}
	
	public FolioDto initFolioDto(FolioDto folioDto, String campaignId, String operationType, GetPermissionsFrom getPermissionsFrom) {
		Folio folio = folioDto.getFolio();
		if (folio == null || folio.getId() == null) {
			folio = initFolio(null, campaignId);
			folioDto.setFolio(folio);
		} 

		List<SimpleTag> selectedTagList = folio.getTags();
		if (selectedTagList.size() > 0) {
			Collections.sort(selectedTagList, new SimpleTagCompare());
			String selectedTags = tagListToString(selectedTagList);
			folioDto.setSelectedTags(selectedTags);
		}
		List<SimpleTag> unselectedTagList = simpleTagService.getUnassignedTags(folio);
		if (unselectedTagList.size() > 0) {
			Collections.sort(unselectedTagList, new SimpleTagCompare());
			String unselectedTags = tagListToString(unselectedTagList);
			folioDto.setUnselectededTags(unselectedTags);
		}
		folioDto.setOperationType(operationType);
		folioDto.setAddTag(null);
		folioDto.setRemoveTag(null);
		
		if (GetPermissionsFrom.FOLIO.equals(getPermissionsFrom) ||
				GetPermissionsFrom.INIT.equals(getPermissionsFrom)) {
			this.usersFromFolioIntoDto(folioDto);
		} else if (GetPermissionsFrom.FOLIO_DTO.equals(getPermissionsFrom)){
			usersFromDtoIntoFolio(folioDto);
		}
		
		folioDto.setFolioDescriptorList(getFolioDescriptorList(folio.getCampaignId(), null, operationType));
		return folioDto;
	}

	private Folio initFolio(String folioId, String campaignId) {
		Folio folio = null;
		if (folioId != null) {
			folio = folioRepository.findOne(folioId);
		}
		if (folio == null) {
			folio = new Folio();
			folio.setCampaignId(campaignId);
			folio.addOwner(userService.getCurrentUserId());
		}
		return folio;
	}

	// This method takes the values for user permissions from the FolioDto and
	// puts them into the Folio
	private void usersFromDtoIntoFolio(FolioDto folioDto) {
		String usersString = folioDto.getUsers();
		ObjectMapper mapper = new ObjectMapper();
		List<CampaignUser> campaignUserList = null;
		try {
			campaignUserList = mapper.readValue(usersString, new TypeReference<List<CampaignUser>>() {});
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		Folio folio = folioDto.getFolio();
		folio.setOwners(null);
		folio.setUsers(null);
		// put values from FolioDto into folio
		if (campaignUserList != null) {
			List<String> ownerIdList = new ArrayList<>();
			List<String> userIdList = new ArrayList<>();
			for (CampaignUser cu : campaignUserList) {
				String id = cu.getUserId();
				String permission = cu.getPermission();
				if (ControllerUtils.ROLE_OWNER.equals(permission)) {
					// Folio.owners is the list of users with owner authority 
					folio.addOwner(id);
					ownerIdList.add(id);
				} else if (ControllerUtils.ROLE_USER.equals(permission)) {
					// Folio.users is the list of users with read authority
					folio.addUser(id);
					userIdList.add(id);
				}
			}
			folio.setOwners(ownerIdList);
			folio.setUsers(userIdList);
		} else {
			// No values, current user = owner
			String ownerId = userService.getCurrentUserId();
			folio.addOwner(ownerId);
		}
	}
	
	private void usersFromFolioIntoDto(FolioDto folioDto) {
		Folio folio = folioDto.getFolio();
		List<CampaignUser> folioUserList = getFolioUsers(folio);
		
		ObjectMapper mapper = new ObjectMapper();
		String users = null;
		try {
			users = mapper.writeValueAsString(folioUserList);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		folioDto.setUsers(users);
	}
	
	private List<CampaignUser> getFolioUsers(Folio folio) {
		List<CampaignUser> allCampaignUsers = campaignUserService.findAllByCampaignId(folio.getCampaignId());
		List<CampaignUser> folioUserList = new ArrayList<>();
		
		for (CampaignUser campaignUser : allCampaignUsers) {
			if (folio.getOwners().contains(campaignUser.getUserId())) {
				campaignUser.setPermission(ControllerUtils.ROLE_OWNER);
				folioUserList.add(campaignUser);
			} else if (folio.getUsers().contains(campaignUser.getUserId())) {
				campaignUser.setPermission(ControllerUtils.ROLE_USER);
				folioUserList.add(campaignUser);
			} else {
				campaignUser.setPermission(ControllerUtils.NO_ACCESS);
				folioUserList.add(campaignUser);
			}
		}

	    return folioUserList;
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
			folioList = folioRepository.findAllByOwners(userId);
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
		List<Folio> allowedFolios = folioRepository.findAllByUsers(userId);
		List<Folio> ownerFolios = folioRepository.findAllByOwners(userId);
		allowedFolios.addAll(ownerFolios);

		return allowedFolios;
	}
	
	public List<Folio> getAllEditableFolios(String campaignId, String userId) {
		List<Folio> allowedFolios = folioRepository.findAllByOwners(userId);

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
