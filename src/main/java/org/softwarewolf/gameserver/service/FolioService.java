package org.softwarewolf.gameserver.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.softwarewolf.gameserver.domain.Folio;
import org.softwarewolf.gameserver.domain.SimpleTag;
import org.softwarewolf.gameserver.domain.dto.FolioDescriptor;
import org.softwarewolf.gameserver.domain.dto.FolioDto;
import org.softwarewolf.gameserver.domain.dto.SelectFolioCreator;
import org.softwarewolf.gameserver.domain.dto.ViewFolioCreator;
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
	private SimpleTagService simpleTagService;
	
	private static final long serialVersionUID = 1L;

	public Folio saveFolio(FolioDto folioDto) throws Exception {
		String selectedTagString = folioDto.getSelectedTags();
		ObjectMapper mapper = new ObjectMapper();
		JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, SimpleTag.class);
		List<SimpleTag> selectedTagList = mapper.readValue(selectedTagString, type);
//		if ("[]".equals(selectedTagList)) {
//			selectedTagList = null;
//		}

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
				folio.removeTag(oldTag.getName());
			}
		}
		
		if (folio.getTitle() == null || folio.getTitle().isEmpty()) {
			return folio;
		}
		return save(folio);
	}
	
	public Folio save(Folio folio) throws Exception {
		String folioId = folio.getId();
		if (folioId != null && folioId.isEmpty()) {
			folio.setId(null);
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
	
	public void initFolioCreator(FolioDto folioDto, String folioId, String campaignId) {
		Folio folio = folioRepository.findOne(folioId);
		initFolioCreator(folioDto, folio, campaignId);
	}
	
	public void initFolioCreator(FolioDto folioDto, Folio folio, String campaignId) {
		if (folio == null) {
			folio = new Folio();
			folio.setCampaignId(campaignId);
		}

		folioDto.setFolio(folio);
		List<SimpleTag> selectedTagList = folio.getTags();
		Collections.sort(selectedTagList, new SimpleTagCompare());
		String selectedTags = tagListToString(selectedTagList);
		folioDto.setSelectedTags(selectedTags);

		List<SimpleTag> unselectedTagList = simpleTagService.getUnassignedTags(folio);
		Collections.sort(unselectedTagList, new SimpleTagCompare());
		String unselectedTags = tagListToString(unselectedTagList);
		folioDto.setUnselectededTags(unselectedTags);
		
		folioDto.setFolioDescriptorList(getFolioDescriptorList(null));
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
	
	public Folio removeTagFromFolio(String folioId, String tagId) {
		Folio folio = folioRepository.findOne(folioId);
		if (folio != null) {
			folio.removeTag(tagId);
		}
		return folioRepository.save(folio);
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

class SimpleTagCompare implements Comparator<SimpleTag> {
	@Override
	public int compare(SimpleTag o1, SimpleTag o2) {
		return o1.getName().compareTo(o2.getName());
	}
}
