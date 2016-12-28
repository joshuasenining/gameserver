package org.softwarewolf.gameserver.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.softwarewolf.gameserver.domain.Campaign;
import org.softwarewolf.gameserver.domain.CampaignSelector;
import org.softwarewolf.gameserver.domain.CampaignUser;
import org.softwarewolf.gameserver.domain.Folio;
import org.softwarewolf.gameserver.domain.SimpleTag;
import org.softwarewolf.gameserver.domain.User;
import org.softwarewolf.gameserver.domain.dto.CampaignDto;
import org.softwarewolf.gameserver.domain.dto.SelectCampaignDto;
import org.softwarewolf.gameserver.repository.CampaignUserRepository;
import org.softwarewolf.gameserver.repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CampaignService {
	@Autowired
	private UserService userService;
	
	@Autowired
	private CampaignRepository campaignRepository;
	
	@Autowired
	private CampaignUserRepository campaignUserRepository;
	
	@Autowired
	private SimpleTagService simpleTagService;
	
	@Autowired
	private CampaignUserService campaignUserService;
	
	@Autowired
	private FolioService folioService;
	
	public void initCampaignDto(String campaignId, CampaignDto campaignDto) {
		Campaign campaign = null;
		if (campaignId != null) {
			campaign = campaignRepository.findOne(campaignId);
		} else {
			campaign = new Campaign();
		}
		campaignDto.setCampaign(campaign);
		List<User> allUserList = userService.findAll();
		List<CampaignUser> campaignUserList = null;
		if (campaign.getId() == null) {
			String ownerId = userService.getCurrentUserId();
			campaign.addOwner(ownerId);
			campaignUserList = new ArrayList<>();
			for (User user : allUserList) {
				String permission = (ownerId.equals(user.getId())) ? ControllerUtils.PERMISSION_OWNER : ControllerUtils.NO_ACCESS;
				CampaignUser cu = new CampaignUser(null, permission, user.getId(), user.getUsername());
				campaignUserList.add(cu);
			}
		} else {
			campaignUserList = campaignUserService.findAllByCampaignId(campaign.getId());
			List<String> campaignUserIds = campaignUserList.stream().map(u -> u.getUserId()).collect(Collectors.toList());
			for (User user : allUserList) {
				if (!campaignUserIds.contains(user.getId())) {
					CampaignUser cu = new CampaignUser(campaign.getId(), ControllerUtils.NO_ACCESS, user.getId(), user.getUsername());
					campaignUserList.add(cu);
				}
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		String userListString = null;
		try {
			userListString = mapper.writeValueAsString(campaignUserList);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		campaignDto.setUsers(userListString);
		campaignDto.setIsOwner(isCampaignOwner(campaign));
		campaignDto.setCampaignList(getCampaignList());
		String folioId = campaign.getCampaignFolioId();
		if (folioId != null) {
			Folio folio = folioService.findOne(campaign.getCampaignFolioId());
			campaignDto.setCampaignFolio(folio);
		} else {
			campaignDto.setCampaignFolio(new Folio());
		}
	}
	
	/**
	 * 2 cases: Insert and update
	 * @param campaignDto
	 * @return Campaign
	 * @throws Exception
	 */
	public Campaign saveCampaign(CampaignDto campaignDto) throws Exception {
		Campaign campaign = campaignDto.getCampaign();
		String campaignId = campaign.getId();
		// If we haven't saved campaign yet, we need to so we have an id.
		List<CampaignUser> campaignUserList = getCampaignUsersFromDto(campaignDto);
		if (campaignId == null) {
			campaign = campaignRepository.save(campaign);
			// ...and there won't be any campaign users so save them too;
			for (CampaignUser cu : campaignUserList) {
				cu.setCampaignId(campaign.getId());
			}
			campaignUserList = campaignUserRepository.save(campaignUserList);
			campaign = campaignRepository.save(campaign);
			campaignId = campaign.getId();
		} else {
			// We need to update the CampaignUsers
			campaignUserService.deleteByCampaignId(campaignId);
			campaignUserRepository.save(campaignUserList);
		}
		List<CampaignUser> ownerList = campaignUserService.findAllByCampaignIdAndPermission(campaignId, ControllerUtils.PERMISSION_OWNER);
		// We can't have 0 owners
		if (ownerList == null || ownerList.isEmpty()) {
			User userOwner = userService.getCurrentUser();
			CampaignUser campaignUser = new CampaignUser(campaignId, ControllerUtils.PERMISSION_OWNER, userOwner.getId(), userOwner.getUsername());
			campaignUserService.save(campaignUser);
			campaign.addOwner(userOwner.getId());
		}			

		// Save the campaign and the campaign folio
		campaign = campaignRepository.save(campaign);
		saveCampaignFolio(campaignDto);
		
		return campaign;
	}
	
	private List<CampaignUser> filterCampaignUserListByPermissions(List<CampaignUser> inList, List<String> filter) {
		return inList.stream().filter(u -> !(filter.contains(u.getPermission()))).collect(Collectors.toList());
	}
	
	public Folio saveCampaignFolio(CampaignDto campaignDto) throws Exception {
		String campaignId = campaignDto.getCampaign().getId();
		if (campaignId == null || campaignId.isEmpty()) {
			return null;
		}
		Folio campaignFolio = campaignDto.getCampaignFolio();
		if (campaignFolio.getCampaignId() == null) {
			campaignFolio.setCampaignId(campaignId);
		}
		if (campaignFolio.getTitle() == null) {
			campaignFolio.setTitle(campaignDto.getCampaign().getName());
		}
		Campaign campaign = campaignDto.getCampaign();
		campaignFolio.setOwners(campaign.getOwnerList());
		campaignFolio.setWriters(campaign.getGameMasterList());
		campaignFolio.setReaders(campaign.getPlayerList());
		
		try {
			// The folio that describes a campaign has a special tag
			SimpleTag campaignDescriptionTag = getCampaignDescriptionTag(campaignId);
			List<SimpleTag> tags = campaignFolio.getTags();
			if (!tags.contains(campaignDescriptionTag)) {
				campaignFolio.addTag(campaignDescriptionTag);
			}

			campaignFolio = folioService.save(campaignFolio);
			campaignDto.setCampaignFolio(campaignFolio);
			campaign.setCampaignFolioId(campaignFolio.getId());
			save(campaign);
			return campaignFolio;
		} catch (Exception e) {
			String message = ControllerUtils.getI18nMessage("editCampaign.error.couldNotSave");
			message += " " + e.getMessage();
			throw new Exception(message);
		}
	}
	
	public List<Campaign> getAllCampaigns() {
		List<Campaign> allCampaigns = campaignRepository.findAll();
		if (allCampaigns == null) {
			allCampaigns = new ArrayList<>();
		}
		return allCampaigns;
	}
	
	public void initSelectCampaignDto(SelectCampaignDto selectCampaignDto, String asType) {
		selectCampaignDto.setAsType(asType);
		String userId = userService.getCurrentUserId();
		List<Campaign> allCampaigns = campaignRepository.findAll();
		List<CampaignUser> inCampaignList = new ArrayList<>();
		if (ControllerUtils.GM_TYPE.equals(asType)) {
			List<CampaignUser> ownerList = campaignUserRepository.findAllByUserIdAndPermission(userId, ControllerUtils.PERMISSION_OWNER);
			if (!ownerList.isEmpty()) {
				inCampaignList.addAll(ownerList);
			}
			List<CampaignUser> gmList = campaignUserRepository.findAllByUserIdAndPermission(userId, ControllerUtils.PERMISSION_GAMEMASTER);
			if (!gmList.isEmpty()) {
				inCampaignList.addAll(gmList);
			}
		} else if (ControllerUtils.PLAYER_TYPE.equals(asType)) {
			List<CampaignUser> playerList = campaignUserRepository.findAllByUserIdAndPermission(userId, ControllerUtils.PERMISSION_PLAYER);
			if (!playerList.isEmpty()) {
				inCampaignList.addAll(playerList);
			}
		}

		if (ControllerUtils.ADMIN_TYPE.equals(asType)) {
			selectCampaignDto.setAccessableCampaigns(allCampaigns);
		} else {
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
	}
	
	public List<Campaign> getAllCampaignsByGM(String ownerId) {
		return campaignRepository.findByOwnerId(ownerId);		
	}
	
	public String getCampaignName(String campaignId) {
		Campaign campaign = campaignRepository.findOne(campaignId);
		if (campaign == null) {
			String message = ControllerUtils.getI18nMessage("editCampaign.error.noCampaignName");
			throw new IllegalArgumentException(message + " " + campaignId);
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
	
	public void validateCampaign(CampaignDto campaignDto) throws Exception {
		putUsersFromDtoIntoCampaign(campaignDto);
		Campaign campaign = campaignDto.getCampaign();
		Folio campaignFolio = campaignDto.getCampaignFolio();
		validateCampaign(campaign, campaignFolio);
	}
	
	/**
	 * Note: Upon initial creation of a campaign, none of the campaign users might have 
	 * the campaignId in them because the campaign hasn't been saved yet.
	 * @param campaignDto
	 */
	private void putUsersFromDtoIntoCampaign(CampaignDto campaignDto) {
		Campaign campaign = campaignDto.getCampaign();
		List<CampaignUser> campaignUserList = getCampaignUsersFromDto(campaignDto);
		campaign.setOwnerList(null);
		campaign.setGameMasterList(null);
		campaign.setPlayerList(null);
		for (CampaignUser campaignUser : campaignUserList) {
			if (ControllerUtils.PERMISSION_OWNER.equals(campaignUser.getPermission())) {
				campaign.addOwner(campaignUser.getUserId());
			} else if (ControllerUtils.PERMISSION_GAMEMASTER.equals(campaignUser.getPermission())) {
				campaign.addGameMaster(campaignUser.getUserId());
			} else if (ControllerUtils.PERMISSION_PLAYER.equals(campaignUser.getPermission())) {
				campaign.addPlayer(campaignUser.getUserId());
			}
		}
	}
	
	private List<CampaignUser> getCampaignUsersFromDto(CampaignDto campaignDto) {
		ObjectMapper mapper = new ObjectMapper();
		JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, CampaignUser.class);
		List<CampaignUser> campaignUserList = null;
		try {
			campaignUserList = mapper.readValue(campaignDto.users, type);
		} catch (Exception e) {
			String message = ControllerUtils.getI18nMessage("editCampaign.error.couldNotGetUsers");
			throw new RuntimeException(message);
		}
		return campaignUserList; 
	}
	
	
	private List<CampaignUser> getCampaignUsersFromCampaign(Campaign campaign) {
		List<String> campaignUserIds = new ArrayList<>();
		campaignUserIds.addAll(campaign.getOwnerList());
		campaignUserIds.addAll(campaign.getGameMasterList());
		campaignUserIds.addAll(campaign.getPlayerList());
		
		String[] userArray = campaignUserIds.toArray(new String[campaignUserIds.size()]);
		List<CampaignUser> campaignUserList = campaignUserService.findAllByKeyValues("userId", userArray);
	
		return campaignUserList; 
	}
	
	public SimpleTag getCampaignDescriptionTag(String campaignId) {
		String desc = ControllerUtils.getI18nMessage("editCampaign.description");
		SimpleTag descTag = simpleTagService.findOneByNameAndCampaignId(desc, campaignId);
		if (descTag == null) {
			descTag = new SimpleTag(desc, campaignId);
			descTag = simpleTagService.save(descTag);
		}
		return descTag;
	}
	
	private void validateCampaign(Campaign campaign, Folio campaignFolio) {
		StringBuilder errors = new StringBuilder();
		if (campaign.getName().isEmpty()) {
			String message = ControllerUtils.getI18nMessage("editCampaign.error.noCampaignName");
			errors.append(message);
		} else {
			List<Campaign> existingCampaigns = campaignRepository.findAll();
			for (Campaign existingCampaign : existingCampaigns) {
				if (existingCampaign.getName().toUpperCase().equals(campaign.getName().toUpperCase()) && 
						!existingCampaign.getId().equals(campaign.getId())) {
					String message = ControllerUtils.getI18nMessage("editCampaign.error.noDuplicateNames");
					errors.append(message);
					break;
				}
			}
			if (campaignFolio.getContent() == null) {
				if (errors.length() > 0) {
					errors.append(" ");
				}
				String message = ControllerUtils.getI18nMessage("editCampaign.error.noDescription");
				errors.append(message);
			}
			if (campaign.getOwnerList() == null || campaign.getOwnerList().isEmpty()) {
				if (errors.length() > 0) {
					errors.append(" ");
				}
				String message = ControllerUtils.getI18nMessage("editCampaign.error.noOwner");
				errors.append(message);
			}
		}
		if (errors.length() > 0) {
			throw new RuntimeException(errors.toString());
		}
	}
	
	public String deleteCampaign(String campaignId) {
		Campaign selectedCampaign = findOne(campaignId);
		if (selectedCampaign == null) {
			String message = ControllerUtils.getI18nMessage("editCampaign.error.noCampaignExistsForId");
			throw new RuntimeException(message + " " + campaignId);
		}
		String campaignName = selectedCampaign.getName();
		simpleTagService.deleteByCampaignId(campaignId);
		campaignUserService.deleteByCampaignId(campaignId);
		folioService.deleteByCampaignId(campaignId);
		campaignRepository.delete(selectedCampaign);
		
		return campaignName;
	}
	
	private List<CampaignSelector> getCampaignList() {
		List<Campaign> campaignList = campaignRepository.findAll();
		List<CampaignSelector> selectorList = campaignList.stream().map(c -> c.getCampaignSelector()).collect(Collectors.toList());

		return selectorList;
	}
	
	private Boolean isCampaignOwner(Campaign campaign) {
		String userId = userService.getCurrentUserId();
		return campaign.getOwnerList().contains(userId) ? Boolean.TRUE : Boolean.FALSE;
	}
}
