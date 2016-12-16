package org.softwarewolf.gameserver.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.softwarewolf.gameserver.domain.Campaign;
import org.softwarewolf.gameserver.domain.CampaignUser;
import org.softwarewolf.gameserver.domain.Folio;
import org.softwarewolf.gameserver.domain.SimpleTag;
import org.softwarewolf.gameserver.domain.User;
import org.softwarewolf.gameserver.domain.dto.CampaignDto;
import org.softwarewolf.gameserver.domain.dto.SelectCampaignDto;
import org.softwarewolf.gameserver.domain.dto.UserListItem;
import org.softwarewolf.gameserver.repository.CampaignUserRepository;
import org.softwarewolf.gameserver.repository.CampaignRepository;
import org.softwarewolf.gameserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class CampaignService {
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
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
	
	@Autowired
	private MessageSource messageSource;
	
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
	
	public Campaign saveCampaign(CampaignDto campaignDto) throws Exception {
		Campaign campaign = campaignDto.getCampaign();
		// Do we need to set the campaign owner?
		if (campaign.getOwnerId() == null) {
			if (campaignDto.getOwnerId() == null) {
				campaignDto.setOwnerId(userService.getCurrentUserId());
			}
			String campaignOwnerId = campaignDto.getOwnerId();
			campaign.setOwnerId(campaignOwnerId);
		}
		// Set the gms
		List<UserListItem> gamemasters = campaignDto.getGamemasters();
		if (gamemasters != null && !gamemasters.isEmpty()) {
			List<String> gmIdList = new ArrayList<>();
			gamemasters.forEach(gm -> gmIdList.add(gm.getId()));  
			campaign.setGameMasterIdList(gmIdList);
		}
		
		// We need the campaign id for the folio, if we haven't saved the campaign ever
		// we don't have an id, so we need to do this...
		boolean saveFolio = false;
		boolean createOwnerAndUsers = false;
		Folio campaignFolio = saveCampaignFolio(campaignDto);
		if (campaignFolio == null) {
			saveFolio = true;
			createOwnerAndUsers = true;
		}
		campaign =  campaignRepository.save(campaign);
		if (saveFolio) {
			saveCampaignFolio(campaignDto);
		}
		
		// We may need to create some CampaignUsers
		if (createOwnerAndUsers) {
			User userOwner = userService.getUser(campaign.getOwnerId());
			CampaignUser owner = new CampaignUser(campaign.getId(), ControllerUtils.ROLE_OWNER, 
					userOwner.getId(), userOwner.getUsername());
			campaignUserRepository.save(owner);
		}
		return campaign;
	}
	
	private Folio saveCampaignFolio(CampaignDto campaignDto) throws Exception {
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
		if (campaignFolio.getOwners().isEmpty()) {
			campaignFolio.addOwner(campaignDto.ownerId);
		}
		try {
			// The folio that describes a campaign has a special tag
			SimpleTag campaignDescriptionTag = getCampaignDescriptionTag(campaignId);
			List<SimpleTag> tags = campaignFolio.getTags();
			if (!tags.contains(campaignDescriptionTag)) {
				campaignFolio.addTag(campaignDescriptionTag);
			}

			campaignFolio = folioService.save(campaignFolio);
			campaignDto.setCampaignFolio(campaignFolio);
			return campaignFolio;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new Exception("Could not save folio: " + e.getMessage());
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
	
	public void editCampaign(CampaignDto campaignDto) throws Exception {
		Campaign campaign = campaignDto.getCampaign();
		Folio campaignFolio = campaignDto.getCampaignFolio();
		validateCampaign(campaign, campaignFolio);
		campaign = saveCampaign(campaignDto);
	}
	
	public SimpleTag getCampaignDescriptionTag(String campaignId) {
		String desc = messageSource.getMessage("createCampaign.description", null, Locale.US);
		SimpleTag descTag = simpleTagService.findOneByNameAndCampaignId(desc, campaignId);
		if (descTag == null) {
			descTag = new SimpleTag(desc, campaignId);
			descTag = simpleTagService.save(descTag);
		}
		return descTag;
	}
	
	private void validateCampaign(Campaign campaign, Folio campaignFolio) {
		StringBuilder errors = new StringBuilder();
		if (campaign.getName() == null) {
			errors.append("The campaign must have a name.");
		} else {
			List<Campaign> existingCampaigns = campaignRepository.findAll();
			for (Campaign existingCampaign : existingCampaigns) {
				if (existingCampaign.getName().toUpperCase().equals(campaign.getName().toUpperCase()) && 
						!existingCampaign.getId().equals(campaign.getId())) {
					if (errors.length() > 0) {
						errors.append(" ");
					}
					errors.append("Campaign names can not be duplicates.");
				}
			}
			if (campaignFolio.getContent() == null) {
				if (errors.length() > 0) {
					errors.append(" ");
				}
				errors.append("A description is required.");
			}
			if (campaign.getOwnerId() == null) {
				if (errors.length() > 0) {
					errors.append(" ");
				}
				errors.append("A campaign must have an owner.");
			}
		}
		if (errors.length() > 0) {
			throw new RuntimeException(errors.toString());
		}
	}
	
	public String deleteCampaign(String campaignId) {
		Campaign selectedCampaign = findOne(campaignId);
		if (selectedCampaign == null) {
			throw new RuntimeException("Could not locate a campaign for id " + campaignId);
		}
		String campaignName = selectedCampaign.getName();
		simpleTagService.deleteByCampaignId(campaignId);
		campaignUserService.deleteByCampaignId(campaignId);
		folioService.deleteByCampaignId(campaignId);
		campaignRepository.delete(selectedCampaign);
		
		return campaignName;
	}
}
