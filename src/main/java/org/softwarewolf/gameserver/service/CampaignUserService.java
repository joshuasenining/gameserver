package org.softwarewolf.gameserver.service;

import org.softwarewolf.gameserver.repository.CampaignUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import org.softwarewolf.gameserver.domain.CampaignUser;

@Service
public class CampaignUserService {
	@Autowired 
	private CampaignUserRepository campaignUserRepository;
	
	public List<CampaignUser> findAllByCampaignId(String campaignId) {
		return campaignUserRepository.findByCampaignId(campaignId);
	}
	
	public CampaignUser findByCampaignIdAndUserId(String campaignId, String userId) {
		return campaignUserRepository.findByCampaignIdAndUserId(campaignId, userId);
	}
	
	public List<CampaignUser> findAllByCampaignIdAndPermission(String campaignId, String permission) {
		return campaignUserRepository.findAllByCampaignIdAndPermission(campaignId, permission);
	}
	
	public CampaignUser save(CampaignUser campaignUser) {
		return campaignUserRepository.save(campaignUser);
	}
	
	public void deleteByCampaignId(String campaignId) {
		campaignUserRepository.deleteByCampaignId(campaignId);
	}
	
	// TODO: Be more specific
	public void deleteAll() {
		campaignUserRepository.deleteAll();
	}
	
	public List<CampaignUser> findAllByKeyValues(String key, Object[] value) {
		return campaignUserRepository.findAllByKeyValues(key, value);
	}	
}
