package org.softwarewolf.gameserver.service;

import java.util.ArrayList;
import java.util.List;

import org.softwarewolf.gameserver.domain.Campaign;
import org.softwarewolf.gameserver.domain.CampaignUser;
import org.softwarewolf.gameserver.domain.User;
import org.softwarewolf.gameserver.domain.dto.CampaignDto;
import org.softwarewolf.gameserver.domain.dto.SelectCampaignDto;
import org.softwarewolf.gameserver.domain.dto.UserListItem;
import org.softwarewolf.gameserver.repository.CampaignUserRepository;
import org.softwarewolf.gameserver.repository.CampaignRepository;
import org.softwarewolf.gameserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class CampaignService {
	@Autowired
	UserService userService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	CampaignRepository campaignRepository;
	
	@Autowired
	CampaignUserRepository campaignUserRepository;
	
	public List<UserListItem> getGamemasters() {
		List<UserListItem> gamemasters = new ArrayList<>();
		List<User> userList = userRepository.findAll();
		for (User user : userList) {
			SimpleGrantedAuthority roleGamemaster = new SimpleGrantedAuthority("ROLE_GAMEMASTER");
			
			if (user.getAuthorities().contains(roleGamemaster)) {
				UserListItem item = new UserListItem(user.getId(), user.getUsername());
				gamemasters.add(item);
			}
		}
		return gamemasters;
	}
	
	public void initCampaignDto(CampaignDto campaignCreator, User user) {
		campaignCreator.setGamemasters(getGamemasters());
		campaignCreator.setOwnerId(user.getId());
		campaignCreator.setOwnerName(user.getUsername());
		campaignCreator.setCampaign(new Campaign(user.getId()));
	}
	
	public void saveCampaign(Campaign campaign) {
		campaignRepository.save(campaign);
	}
	
	public List<Campaign> getAllCampaigns() {
		List<Campaign> allCampaigns = campaignRepository.findAll();
		if (allCampaigns == null) {
			allCampaigns = new ArrayList<>();
		}
		return allCampaigns;
	}
	
	public void initSelectCampaignDto(SelectCampaignDto selectCampaignDto) {
		String userId = userService.getCurrentUserId();
		List<Campaign> allCampaigns = campaignRepository.findAll();
		List<CampaignUser> campaignUserList = campaignUserRepository.findAllByUserId(userId);

		List<String> campaignIdList = new ArrayList<>();
		for (CampaignUser player : campaignUserList) {
			campaignIdList.add(player.getCampaignId());
		}
		Object[] campaignArray = new Object[campaignIdList.size()];
		campaignArray = campaignIdList.toArray(campaignArray);
		List<Campaign> inCampaignList = campaignRepository.findAllByKeyValues("id", campaignArray);
		
		List<Campaign> inaccessableCampaigns = new ArrayList<Campaign>(allCampaigns);
		inaccessableCampaigns.removeAll(inCampaignList);
		
		selectCampaignDto.setAccessableCampaigns(inCampaignList);
		selectCampaignDto.setInaccessableCampaigns(inaccessableCampaigns);
	}
	
	public List<Campaign> getAllCampaignsByGM(String ownerId) {
		return campaignRepository.findByOwnerId(ownerId);		
	}
	
	public String getCampaignName(String campaignId) {
		Campaign campaign = campaignRepository.findOne(campaignId);
		if (campaign == null) {
			throw new IllegalArgumentException("No campaign exists for id " + campaignId);
		}
		return campaign.getName();
	}
	
	public Campaign findOne(String id) {
		return campaignRepository.findOne(id);
	}

}
