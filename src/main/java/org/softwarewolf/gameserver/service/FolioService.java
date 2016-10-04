package org.softwarewolf.gameserver.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.softwarewolf.gameserver.domain.Folio;
import org.softwarewolf.gameserver.domain.helper.FolioCreator;
import org.softwarewolf.gameserver.domain.helper.FolioDescriptor;
import org.softwarewolf.gameserver.domain.SimpleTag;
import org.softwarewolf.gameserver.domain.helper.SelectFolioCreator;
import org.softwarewolf.gameserver.domain.helper.ViewFolioCreator;
import org.softwarewolf.gameserver.repository.FolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FolioService implements Serializable {
	private final String ADD = "add";
	private final String REMOVE = "remove";
	
	@Autowired
	private FolioRepository folioRepository;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private OrganizationRankService organizationRankService;
	@Autowired
	private OrganizationTypeService organizationTypeService;
	@Autowired
	private LocationService locationService;
	@Autowired
	private LocationTypeService locationTypeService;
	@Autowired
	private SimpleTagService simpleTagService;
	
	private static final long serialVersionUID = 1L;

	public Folio save(Folio folio) throws Exception {
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
	
	public void initFolioCreator(FolioCreator folioCreator, String folioId, String campaignId) {
		Folio folio = folioRepository.findOne(folioId);
		initFolioCreator(folioCreator, folio, campaignId);
	}
	
	public void initFolioCreator(FolioCreator folioCreator, Folio folio, String campaignId) {
		if (folio == null) {
			folio = new Folio();
			folio.setCampaignId(campaignId);
		}

		folioCreator.setFolio(folio);
		List<SimpleTag> selectedTags = folio.getTags();
		ObjectMapper mapper = new ObjectMapper();
		if (selectedTags != null && !selectedTags.isEmpty()) {
			String json;
			try {
				json = mapper.writeValueAsString(selectedTags);
				folioCreator.setSelectedTags(json);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				folioCreator.setSelectedTags("{}");
			}
		} else {
			folioCreator.setSelectedTags("{}");
		}

		List<SimpleTag> unassignedTags = simpleTagService.getUnassignedTags(campaignId, folio.getId());
		String json;
		try {
			json = mapper.writeValueAsString(unassignedTags);
			folioCreator.setUnassignedTags(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			folioCreator.setUnassignedTags("{}");
		}
		
		folioCreator.setFolioDescriptorList(getFolioDescriptorList(null));
	}
	
	public void deleteAll() {
		folioRepository.deleteAll();
	}
	
	public Folio removeTagFromFolio(String folioId, String tagId) {
		Folio folio = folioRepository.findOne(folioId);
		if (folio != null) {
			folio.removeTag(tagId);
		}
		return folioRepository.save(folio);
	}

	public Folio addTagToFolio(String campaignId, String folioId, String tagName) {
		SimpleTag tag = simpleTagService.findOneByNameAndCampaignId(tagName, campaignId);
		if (tag == null) {
			return null;
		}
		return addTagToFolio(folioId, tag);
	}
	
	public Folio addTagToFolio(String folioId, SimpleTag tag) {
		Folio folio = folioRepository.findOne(folioId);
		folio.addTag(tag);
		return folioRepository.save(folio);
	}

	public List<FolioDescriptor> getFolioDescriptorList(List<SimpleTag> includeTags) {
		List<FolioDescriptor> folioDescriptorList = new ArrayList<>();
		if (includeTags == null) {
			includeTags = new ArrayList<>();
		}
		List<Folio> folioList = folioRepository.findAll();
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
	
	public Folio findOne(String id) {
		return folioRepository.findOne(id);
	}
	
	/**
	 * In order to communicate with the back-end the SelectFolioCreator stores the list of selected 
	 * and unselected ObjectTags as a JSON String. They need to be converted back to be manipulated. 
	 * @param campaignId
	 * @param selectFolioCreator
	 */ 
	public void initSelectFolioCreator(String campaignId, SelectFolioCreator selectFolioCreator) {
		selectFolioCreator.setCampaignId(campaignId);
		List<SimpleTag> excludeTags = new ArrayList<>();
		List<SimpleTag> allTags = simpleTagService.getTagList(campaignId, excludeTags);
		
		String unselectedTags = selectFolioCreator.getUnselectedTags();
		if (unselectedTags == null) {
			unselectedTags = "";
		}
		String selectedTags = selectFolioCreator.getSelectedTags();
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
				selectFolioCreator.setUnselectedTags(unselectedTags);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String addTagName = selectFolioCreator.getAddTagName();
		String removeTagName = selectFolioCreator.getRemoveTagName();
		if (addTagName != null || removeTagName != null) {
			for(SimpleTag tag: allTags) {
				String currentTagName = tag.getName();
				if (addTagName != null && addTagName.equals(currentTagName)) {
					addTag = tag;
					selectFolioCreator.setAddTagName(null);
				} else if (removeTagName != null && removeTagName.equals(currentTagName)) {
					removeTag = tag;
					selectFolioCreator.setRemoveTagName(null);
				}
			}
		}

		if (addTag != null) {
			createTagList(ADD, selectFolioCreator, addTag);
		}
		if (removeTag != null) {
			createTagList(REMOVE, selectFolioCreator, removeTag);
		}
		
		
		List<FolioDescriptor> folioDescriptorList = getFolioDescriptorList(selectFolioCreator.getSelectedTagsAsTags());
		String listAsString = convertFolioDescriptorListToString(folioDescriptorList);
		selectFolioCreator.setFolioDescriptorList(listAsString);
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
	
	private void createTagList(String operation, SelectFolioCreator selectFolioCreator, SimpleTag tag) {
		if (!ADD.equals(operation) && !REMOVE.equals(operation)) {
			throw new IllegalArgumentException("Only 'add' and remove' are allowable operations.");
		}
		
		ObjectMapper mapper = new ObjectMapper();
		JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, SimpleTag.class);
		List<SimpleTag> unselectedTagList = null;
		List<SimpleTag> selectedTagList = null;

		if (selectFolioCreator.getUnselectedTags().isEmpty()) {
			unselectedTagList = new ArrayList<>();
		} else {
			try {
				unselectedTagList = mapper.readValue(selectFolioCreator.getUnselectedTags(), type);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (selectFolioCreator.getSelectedTags().isEmpty()) {
			selectedTagList = new ArrayList<>();
		} else {
			try {
				selectedTagList = mapper.readValue(selectFolioCreator.getSelectedTags(), type);
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
			selectFolioCreator.setUnselectedTags(mapper.writeValueAsString(unselectedTagList));
			selectFolioCreator.setSelectedTags(mapper.writeValueAsString(selectedTagList));
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
		selectFolioCreator.setFolioDescriptorList(folioDescriptors);
	}
	
	public List<Folio> findFoliosByTags(List<SimpleTag> selectedTags) {
		List<Object> tagNames = new ArrayList<>();
		for (SimpleTag tag : selectedTags) {
			tagNames.add(tag.getName());
		}
		List<Folio> folioList = folioRepository.findAllByKeyValues("tags.name", tagNames.toArray());
		return folioList;
	}
	
	public void initViewFolioCreator(ViewFolioCreator viewFolioCreator, String folioId) {
		Folio folio = folioRepository.findOne(folioId);
		if (folio != null) {
			viewFolioCreator.setContent(folio.getContent());
		}
		
	}
}
