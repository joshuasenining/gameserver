package org.softwarewolf.gameserver.service;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
//import org.softwarewolf.gameserver.controller.Bundle.ImportExportBundle;
import org.softwarewolf.gameserver.domain.Campaign;
import org.softwarewolf.gameserver.domain.CampaignUser;
import org.softwarewolf.gameserver.domain.Folio;
import org.softwarewolf.gameserver.domain.SimpleTag;
import org.softwarewolf.gameserver.domain.User;
import org.softwarewolf.gameserver.domain.dto.ImportExportCampaignDto;
import org.softwarewolf.gameserver.domain.dto.ImportExportSelectorDto;
import org.softwarewolf.gameserver.repository.SimpleGrantedAuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ImportExportService {
	@Autowired
	private UserService userService;
	
	@Autowired
	private CampaignService campaignService;
	
	@Autowired
	private CampaignUserService campaignUserService;
	
	@Autowired
	private FolioService folioService;
	
	@Autowired
	private SimpleTagService simpleTagService;
	
	@Autowired
	private ApplicationSettingsService applicationSettingsService;
	
	@Autowired
	private SimpleGrantedAuthorityRepository simpleGrantedAuthorityRepository;

	public void initImportExportBundle(ImportExportSelectorDto importExportSelectorDto, boolean isImport, String forwardingUrl) 
			throws JsonGenerationException, JsonMappingException, IOException {
		if (forwardingUrl == null) {
			throw new IllegalArgumentException("A fowardingUrl is required");
		}
		importExportSelectorDto.setForwardingUrl(forwardingUrl);
		
		if (isImport) {
			importExportSelectorDto.setCampaignListAsString(null);
		} else {
			List<Campaign> campaignList = campaignService.getAllCampaigns();
			Map<String, String> campaignMap = new HashMap<>();
			for (Campaign campaign : campaignList) {
				campaignMap.put(campaign.getId(), campaign.getName());
			}
			
			ObjectMapper mapper = new ObjectMapper();
			String campaignMapAsString = mapper.writeValueAsString(campaignMap);

			importExportSelectorDto.setCampaignListAsString(campaignMapAsString);
		}
	}
	
	public String exportCampaign(String campaignId) throws IOException {
		String backupDir = applicationSettingsService.getExportDir();
		if (backupDir == null) {
			String message = ControllerUtils.getI18nMessage("exportCampaign.error.noDir");
			throw new RuntimeException(message);
		}
		
		ImportExportCampaignDto campaignData = new ImportExportCampaignDto();
		
		// ** campaign **
		Campaign campaign = campaignService.findOne(campaignId);
		campaignData.setCampaign(campaign);
		
		// ** CampaignUser **
		List<CampaignUser> campaignUserList = campaignUserService.findAllByCampaignId(campaignId);
		campaignData.setCampaignUserList(campaignUserList);
		
		// ** User **
		// TODO: reconcile the SimpleGrantedAuthority list of users on import
		List<String> campaignUserIdList = campaignUserList.stream().map(c -> c.getUserId()).collect(Collectors.toList());
		List<User> userList = userService.findAllByKeyValues("id", campaignUserIdList.toArray());
		campaignData.setUserList(userList);
		
		// ** Folio **
		List<Folio> folioList = folioService.findAllByCampaignId(campaignId);
		campaignData.setFolioList(folioList);

		// ** Tag **
		List<SimpleTag> tagList = simpleTagService.findAllByCampaignId(campaignId);
		campaignData.setTagList(tagList);
		
		ObjectMapper mapper = new ObjectMapper();
		String campaignDataAsString = mapper.writeValueAsString(campaignData);
		
		// Write the file
		String campaignName = campaign.getName();
		campaignName = campaignName.replace(" ", "_");
		String fileName = backupDir + campaignName + ".mongo";
		File file = new File(fileName);

		// if file doesn't exists, then create it
		if (!file.exists()) {
			BufferedWriter bw = null;
			try {
				file.createNewFile();
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				bw = new BufferedWriter(fw);
				bw.write(campaignDataAsString);
				bw.close();
			} catch (IOException e) {
				throw e;
			} finally {
				if (bw != null) {
					bw.close();
				}
			}
		}
		
		return fileName;
	}

	public void importCampaignData(MultipartFile file) throws IOException {
	  	if (!file.isEmpty()) {
  			byte[] bytes = file.getBytes();
  			
  			InputStream inputStream = new ByteArrayInputStream(bytes);
  			ByteArrayOutputStream baos = new ByteArrayOutputStream();
  			byte[] buf = new byte[8192];
  			for (;;) {
  			    int nread = inputStream.read(buf, 0, buf.length);
  			    if (nread <= 0) {
  			        break;
  			    }
  			    baos.write(buf, 0, nread);
  			}
  			inputStream.close();
  			baos.close();
  			byte[] newBytes = baos.toByteArray();
  			String campaignAsString = new String(newBytes);
  			
  			ObjectMapper mapper = new ObjectMapper();
  			ImportExportCampaignDto campaignData = null;
  			try {
  				campaignData = mapper.readValue(campaignAsString, ImportExportCampaignDto.class);
  				Campaign campaign = campaignData.getCampaign();
  				Campaign existingCampaign = campaignService.findOneByName(campaign.getName());
  				if (existingCampaign != null) {
  					String message = ControllerUtils.getI18nMessage("importCampaign.error.campaignNameExists");
  					throw new RuntimeException(message);
  				}

  				/**
  				 * update the user's roles and ids
  				 */
  				List<CampaignUser> campaignUserList = campaignData.getCampaignUserList();
  				List<User> userList = campaignData.getUserList();		
  				resetUserRoles(userList, campaignUserList);
  				Map<String, String> importIdToExistingIdMap = getUserIdMap(userList);
  				// Save the users
  				userService.save(userList);
  				// convert owners, gms, and players
  				resetCampaign(campaign, importIdToExistingIdMap);
  				
  				campaign = campaignService.save(campaign);
  				String campaignId = campaign.getId();

  				resetCampaignUserList(campaignUserList, campaignId, importIdToExistingIdMap);
  				campaignUserService.save(campaignUserList);
  				
  				List<SimpleTag> tagList = campaignData.getTagList();
  				resetTagList(tagList, campaignId);
  				tagList = simpleTagService.save(tagList);
  				Map<String, SimpleTag> tagMap = tagList.stream().collect(Collectors.toMap(SimpleTag::getName, t -> t));
  				
  				List<Folio> folioList = campaignData.getFolioList();
  				resetFolios(folioList, campaignId, importIdToExistingIdMap, tagMap);
  				folioService.save(folioList);
  				
  			} catch (IOException e) {
  				throw new RuntimeException(e);
  			}
  			
  			System.out.println("Tada!");
	  	}
	}
	
	private void resetTagList(List<SimpleTag> tagList, String campaignId) {
		for (SimpleTag tag : tagList) {
			tag.setCampaignId(campaignId);
		}
	}
	
	/**
	 * Do NOT allow the import of a user with ROLE_ADMIN. Strip that out.
	 * @param userList
	 * @param campaignUserList
	 */
	private void resetUserRoles(List<User> userList, List<CampaignUser> campaignUserList) {
		List<SimpleGrantedAuthority> sgaList = simpleGrantedAuthorityRepository.findAll();
		Map<String, SimpleGrantedAuthority> sgaMap = 
				sgaList.stream().collect(Collectors.toMap(SimpleGrantedAuthority::getAuthority, s -> s));
		Map<String, List<String>> userAuthorityListMap = new HashMap<>();
		String gamemaster = sgaMap.get("ROLE_GAMEMASTER").getAuthority();
		String player = sgaMap.get("ROLE_USER").getAuthority();
		
		for (CampaignUser campaignUser : campaignUserList) {
			List<String> usersSgaList = userAuthorityListMap.get(campaignUser.getUserId());
			if (campaignUser.getPermission().equals(ControllerUtils.PERMISSION_OWNER) ||
					campaignUser.getPermission().equals(ControllerUtils.PERMISSION_GAMEMASTER)) {
				if (usersSgaList == null) {
					usersSgaList = new ArrayList<>(Arrays.asList(new String[] {gamemaster}));
				} else {
					usersSgaList.add(gamemaster);
				}
			} else if (campaignUser.getPermission().equals(ControllerUtils.PERMISSION_PLAYER)) {
				if (usersSgaList == null) {
					usersSgaList = new ArrayList<>(Arrays.asList(new String[] {player}));
				} else {
					usersSgaList.add(player);
				}
			}
			userAuthorityListMap.put(campaignUser.getUserId(), usersSgaList);
		}
		for (User user : userList) {
			List<String> currentUsersSgaList = userAuthorityListMap.get(user.getId());
			user.setAuthorities(currentUsersSgaList);
		}
	}
	/*
	public void restoreCampaignData(String fileName) {
		// Should use more error handling
		File campaignDataFile = null;
		campaignDataFile = new File(fileName);
		String campaignDataAsString = null;
		Scanner scanner = null;
		try {
			try {
				 scanner = new Scanner(campaignDataFile).useDelimiter("\\Z");
				 campaignDataAsString = scanner.next();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// Create the CampaignData object
			ObjectMapper mapper = new ObjectMapper();
			ImportExportCampaignDto campaignData = null;
			try {
				campaignData = mapper.readValue(campaignDataAsString, ImportExportCampaignDto.class);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			Map<String, String> importIdToExistingIdMap = getUserIdMap(campaignData.getUserList());
			
			Campaign campaign = campaignData.getCampaign();
			resetCampaign(campaign, importIdToExistingIdMap);
			String campaignId = campaign.getId();
			List<Folio> folioList = campaignData.getFolioList();
			resetFolios(folioList, campaignId, importIdToExistingIdMap);
		} finally {
			scanner.close();
		}
	}
*/
	/**
	 * Users should be restored first because their id's will modify the contents of the campaign.
	 * Note that the ROLES should not vary from implementation to implementation so those should 
	 * not need to be reconciled.
	 * @param userList
	 */
	public Map<String, String> getUserIdMap(List<User> userList) {
		Map<String, String> importIdToExistingIdMap = new HashMap<>();
		List<User> existingUsers = userService.findAll();
		Map<String, User> userMap = existingUsers.stream().collect(Collectors.toMap(User::getUsername, u -> u));
		for (User newUser : userList) {
			User oldUser = userMap.get(newUser.getUsername());
			if (oldUser != null) {
				importIdToExistingIdMap.put(newUser.getId(), oldUser.getId());
				newUser.setId(oldUser.getId());
			} else {
				importIdToExistingIdMap.put(newUser.getId(), newUser.getId());
			}
		}
		
		return importIdToExistingIdMap;
	}
	
	public void resetCampaign(Campaign campaign, Map<String, String> importIdToExistingIdMap) {
		Campaign existingCampaign = campaignService.findOneByName(campaign.getName());
		// In case the Ids don't match. This is risky because in reality these might not
		// be the same campaign!
		if (existingCampaign != null) {
			throw new RuntimeException("Can not import a campaign with an existing name");
		}
		
		List<String> importOwnerIds = campaign.getOwnerList();
		List<String> existingOwnerIds = convertUserIdList(importOwnerIds, importIdToExistingIdMap);
		campaign.setOwnerList(existingOwnerIds);
		
		List<String> importGmIdList = campaign.getGameMasterList();
		List<String> existingGmIdList = convertUserIdList(importGmIdList, importIdToExistingIdMap);
		campaign.setGameMasterList(existingGmIdList);
		
		List<String> importPlayerIdList = campaign.getPlayerList();
		List<String> existingPlayerIdList = convertUserIdList(importPlayerIdList, importIdToExistingIdMap);
		campaign.setGameMasterList(existingPlayerIdList);
		
		campaignService.save(campaign);
	}

	public void resetCampaignUserList(List<CampaignUser> campaignUserList, String campaignId, Map<String, String> importIdToExistingIdMap) {
		for (CampaignUser campaignUser : campaignUserList) {
			campaignUser.setCampaignId(campaignId);
			String userId = importIdToExistingIdMap.get(campaignUser.getUserId());
			campaignUser.setUserId(userId);
		}
	}
	
	public void resetFolios(List<Folio> folioList, String campaignId, Map<String, String> importIdToExistingIdMap, 
			Map<String, SimpleTag> tagMap) {
		for (Folio folio : folioList) {
			List<String> importOwnerIdList = folio.getOwners();
			List<String> existingOwnerIdList = convertUserIdList(importOwnerIdList, importIdToExistingIdMap);
			folio.setOwners(existingOwnerIdList);
			
			List<String> importWriterIdList = folio.getWriters();
			List<String> existingWriterList = convertUserIdList(importWriterIdList, importIdToExistingIdMap);
			folio.setWriters(existingWriterList);
			
			List<String> importReaderIdList = folio.getReaders();
			List<String> existingReadersList = convertUserIdList(importReaderIdList, importIdToExistingIdMap);
			folio.setReaders(existingReadersList);
			
			List<SimpleTag> importSimpleTagList = folio.getTags();
			List<SimpleTag> resetTagList = importSimpleTagList.stream().map(t -> tagMap.get(t.getName())).collect(Collectors.toList());
			folio.setTags(resetTagList);
		}
	}
	
	private List<String> convertUserIdList(List<String> importUserIds, Map<String, String> importIdToExistingIdMap) {
		List<String> existingUserIdList = new ArrayList<>();
		for (String importId : importUserIds) {
			String existingId = importIdToExistingIdMap.get(importId);
			if (existingId == null) {
				throw new RuntimeException("User mapping error");
			}
			existingUserIdList.add(existingId);
		}
		return existingUserIdList;
	}
	
	public void importCampaign(String filePath) {
		// TODO: write this
	}
}
