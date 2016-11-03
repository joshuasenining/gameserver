package org.softwarewolf.gameserver.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	@Autowired
	private FolioService folioService;
	
	public SimpleTag findOne(String id) {
		return simpleTagRepository.findOne(id);
	}
	
	public List<SimpleTag> getTagList(String campaignId, List<SimpleTag> excludeTags) {
		List<SimpleTag> tagList = simpleTagRepository.findAllByCampaignId(campaignId);
		if (excludeTags != null && !excludeTags.isEmpty()) {
			tagList.removeAll(excludeTags);
		}
		return tagList;
	}
		
	public SimpleTag save(SimpleTag simpleTag) {
		String tagName = simpleTag.getName();
		List<SimpleTag> tagList = simpleTagRepository.findAllByKeyValue("name", tagName);
		if (tagList == null || tagList.isEmpty()) {
			simpleTag = simpleTagRepository.save(simpleTag);
		} else {
			simpleTag = tagList.get(0);
		}
		return simpleTag;
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
}
