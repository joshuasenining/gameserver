package org.softwarewolf.gameserver.service;

import java.util.ArrayList;
import java.util.List;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.softwarewolf.gameserver.domain.Folio;
import org.softwarewolf.gameserver.domain.SimpleTag;
import org.softwarewolf.gameserver.exception.NotImplementedException;
import org.softwarewolf.gameserver.repository.SimpleTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SimpleTagService {

	@Autowired
	private SimpleTagRepository simpleTagRepository;
	
	public SimpleTag findOne(String id) {
		return simpleTagRepository.findOneById(id);
	}
	
	public List<SimpleTag> getTagList(String campaignId, List<SimpleTag> excludeTags) {
		List<SimpleTag> tagList = simpleTagRepository.findAllByCampaignId(campaignId);
		if (excludeTags != null && !excludeTags.isEmpty()) {
			tagList.removeAll(excludeTags);
		}
		return tagList;
	}
		
	public SimpleTag save(String simpleTagName, String campaignId) throws Exception {
		SimpleTag newTag = createAndVerifyTag(simpleTagName, campaignId);
		
		return save(newTag);
	}

	private SimpleTag createAndVerifyTag(String simpleTagName, String campaignId) {
		simpleTagName = simpleTagName.trim();
		if (simpleTagName.isEmpty()) {
			String message = ControllerUtils.getI18nMessage("editFolio.error.noTagName");
			throw new RuntimeException(message);
		}
		SimpleTag oldTag = simpleTagRepository.findOneByNameAndCampaignId(simpleTagName, campaignId);
		if (oldTag != null) {
			String message = ControllerUtils.getI18nMessage("editFolio.error.duplicateTagName");
			throw new RuntimeException(message);
		}

		return new SimpleTag(simpleTagName, campaignId); 
	}
	
	public SimpleTag save(SimpleTag simpleTag) {
		SimpleTag newTag = createAndVerifyTag(simpleTag.getName(), simpleTag.getCampaignId());
		
		return simpleTagRepository.save(newTag);
	}
	
	public void delete(SimpleTag simpleTag) {
		throw new NotImplementedException();
	}
	
	public void deleteAll() {
		simpleTagRepository.deleteAll();
	}
	
	/**
	 * TODO: I need to do this via MongoTemplate calls rather than in code.
	 * @param campaignId
	 * @return
	 */
	public List<SimpleTag> getUnassignedTags(Folio folio) {
		List<SimpleTag> unassignedTags = new ArrayList<>();
		List<SimpleTag> allTags = getTagList(folio.getCampaignId(), unassignedTags);
		for (SimpleTag currentTag : allTags) {
			if (!folio.getTags().contains(currentTag)) {
				unassignedTags.add(currentTag);
			}
		}
		return unassignedTags;
	}
	
	public SimpleTag findOneByNameAndCampaignId(String name, String campaignId) {
		return simpleTagRepository.findOneByNameAndCampaignId(name, campaignId);
	}
	
	public List<SimpleTag> deleteByCampaignId(String campaignId) {
		return simpleTagRepository.deleteByCampaignId(campaignId);
	}
	
	public List<SimpleTag> findAllByCampaignId(String campaignId) {
		return simpleTagRepository.findAllByCampaignId(campaignId);
	}
	
	public List<SimpleTag> save(List<SimpleTag> tagList) {
		return simpleTagRepository.saveAll(tagList);
	}
}
