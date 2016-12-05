package org.softwarewolf.gameserver.service;

import java.util.ArrayList;
import java.util.List;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
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
			SimpleGrantedAuthority roleGamemaster = new SimpleGrantedAuthority(ControllerUtils.ROLE_GAMEMASTER);
			
			if (user.getAuthorities().contains(roleGamemaster)) {
				UserListItem item = new UserListItem(user.getId(), user.getUsername());
				gamemasters.add(item);
			}
		}
		return gamemasters;
	}
	
	public void initCampaignDto(CampaignDto campaignDto, String ownerId) {
		campaignDto.setGamemasters(null);
		campaignDto.setOwnerId(ownerId);
		campaignDto.setCampaign(new Campaign(ownerId));
	}
	
	public Campaign saveCampaign(Campaign campaign) {
		campaign =  campaignRepository.save(campaign);
		CampaignUser owner = new CampaignUser(campaign.getId(), ControllerUtils.ROLE_OWNER, 
				userService.getCurrentUserId(), userService.getCurrentUserName());
		campaignUserRepository.save(owner);
		CampaignUser gm = new CampaignUser(campaign.getId(), ControllerUtils.ROLE_GAMEMASTER, 
				userService.getCurrentUserId(), userService.getCurrentUserName());
		campaignUserRepository.save(gm);
		return campaign;
	}
	
	public List<Campaign> getAllCampaigns() {
		List<Campaign> allCampaigns = campaignRepository.findAll();
		if (allCampaigns == null) {
			allCampaigns = new ArrayList<>();
		}
		return allCampaigns;
	}
	
	public void initSelectCampaignDto(SelectCampaignDto selectCampaignDto, String asType) {
		String userId = userService.getCurrentUserId();
		List<Campaign> allCampaigns = campaignRepository.findAll();
		List<CampaignUser> inCampaignList = new ArrayList<>();
		if (ControllerUtils.GM_TYPE.equals(asType)) {
			List<CampaignUser> ownerList = campaignUserRepository.findAllByUserIdAndRole(userId, ControllerUtils.ROLE_OWNER);
			if (!ownerList.isEmpty()) {
				inCampaignList.addAll(ownerList);
			}
			List<CampaignUser> gmList = campaignUserRepository.findAllByUserIdAndRole(userId, ControllerUtils.ROLE_GAMEMASTER);
			if (!gmList.isEmpty()) {
				inCampaignList.addAll(gmList);
			}
		} else if (ControllerUtils.PLAYER_TYPE.equals(asType)) {
			List<CampaignUser> playerList = campaignUserRepository.findAllByUserIdAndRole(userId, ControllerUtils.ROLE_USER);
			if (!playerList.isEmpty()) {
				inCampaignList.addAll(playerList);
			}
		}

		List<String> campaignIdList = new ArrayList<>();
		for (CampaignUser player : inCampaignList) {
			campaignIdList.add(player.getCampaignId());
		}
		Object[] campaignArray = new Object[campaignIdList.size()];
		campaignArray = campaignIdList.toArray(campaignArray);
		List<Campaign> campaignList = campaignRepository.findAllByKeyValues("id", campaignArray);
		
		List<Campaign> inaccessableCampaigns = new ArrayList<Campaign>(allCampaigns);
		inaccessableCampaigns.removeAll(campaignList);
		
		selectCampaignDto.setAccessableCampaigns(campaignList);
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
	
	public Campaign findOneByName(String name) {
		return campaignRepository.findOneByName(name);
	}
	
	public void deleteByName(String name) {
		campaignRepository.deleteByName(name);
	}
	
	public Campaign save(Campaign campaign) {
		return campaignRepository.save(campaign);
	}
	
	public void createCampaign(CampaignDto campaignDto) {
		Campaign campaign = campaignDto.getCampaign();
		String ownerId = campaignDto.getOwnerId();
		campaign.setOwnerId(ownerId);
		campaign = saveCampaign(campaign);
		CampaignUser ownerCu = new CampaignUser(campaign.getId(), "ROLE_OWNER", ownerId, userService.getCurrentUserName());
		campaignUserRepository.save(ownerCu);
		CampaignUser gmCu = new CampaignUser(campaign.getId(), "ROLE_GAMEMASTER", ownerId, userService.getCurrentUserName());
		campaignUserRepository.save(gmCu);		
	}
}
