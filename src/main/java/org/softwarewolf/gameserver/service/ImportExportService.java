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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;

import org.softwarewolf.gameserver.controller.helper.ImportExportHelper;
//import org.softwarewolf.gameserver.controller.helper.ImportExportHelper;
import org.softwarewolf.gameserver.domain.Campaign;
import org.softwarewolf.gameserver.domain.CampaignPlayer;
import org.softwarewolf.gameserver.domain.Folio;
import org.softwarewolf.gameserver.domain.User;
import org.softwarewolf.gameserver.domain.dto.ImportExportCampaignDto;
import org.softwarewolf.gameserver.repository.CampaignPlayerRepository;
import org.softwarewolf.gameserver.repository.CampaignRepository;
import org.softwarewolf.gameserver.repository.FolioRepository;

import org.softwarewolf.gameserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ImportExportService {
	@Autowired
	UserService userService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	CampaignRepository campaignRepository;
	
	@Autowired
	CampaignPlayerRepository campaignPlayerRepository;
	
	@Autowired
	FolioRepository folioRepository;

	public void initImportExportHelper(ImportExportHelper importExportHelper, boolean isImport, String forwardingUrl) 
			throws JsonGenerationException, JsonMappingException, IOException {
		importExportHelper.setSelectedCampaignId(null);
		importExportHelper.setSelectedCampaignName(null);
		importExportHelper.setImportFilename(null);
		
		if (forwardingUrl == null) {
			throw new IllegalArgumentException("A fowardingUrl is required");
		}
		importExportHelper.setForwardingUrl(forwardingUrl);
		
		if (isImport) {
			importExportHelper.setCampaignListAsString(null);
		} else {
			List<Campaign> campaignList = campaignRepository.findAll();
			Map<String, String> campaignMap = new HashMap<>();
			for (Campaign campaign : campaignList) {
				campaignMap.put(campaign.getId(), campaign.getName());
			}
			
			ObjectMapper mapper = new ObjectMapper();
			String campaignMapAsString = mapper.writeValueAsString(campaignMap);

			importExportHelper.setCampaignListAsString(campaignMapAsString);
		}
	}
	
	// TODO: Add SimpleTag list
	public String exportCampaign(String campaignId) throws IOException {
		ResourceBundle resources = ResourceBundle.getBundle("gameserver");
		String backupDir = resources.getString("exportLocation");
		
		ObjectMapper mapper = new ObjectMapper();
		ImportExportCampaignDto campaignData = new ImportExportCampaignDto();
		Campaign campaign = campaignRepository.findOne(campaignId);
		String campaignName = campaign.getName();
		campaignName = campaignName.replace(" ", "_");
		String fileName = backupDir + campaignName + ".mongo";
		
		campaignData.setCampaign(campaign);
		
		// ** Users **
		String campaignOwnerId = campaign.getOwnerId();
		List<String> gmIdList = campaign.getGameMasterIdList();
		List<CampaignPlayer> playerList = campaignPlayerRepository.findAllByKeyValue("campaignId", campaignId);
		// Populate the list of all campaign user ids
		List<String> campaignUserIdList = new ArrayList<>();
		campaignUserIdList.add(campaignOwnerId);
		if (gmIdList != null) {
			for (String gmId : gmIdList) {
				if (!campaignUserIdList.contains(gmId)) {
					campaignUserIdList.add(gmId);
				}
			}
		}
		if (playerList != null) {
			for (CampaignPlayer player : playerList) {
				if (!campaignUserIdList.contains(player.getPlayerId())) {
					campaignUserIdList.add(player.getPlayerId());
				}
			}
		}
		// Now create the user list from that
		// TODO: reconcile the SimpleGrantedAuthority list of users on import
		List<User> userList = userRepository.findAllByKeyValues("id", campaignUserIdList.toArray());
		campaignData.setUserList(userList);
		
		// Get all the folios in the campaign
		List<Folio> folioList = folioRepository.findAllByKeyValue("campaignId", campaignId);
		campaignData.setFolioList(folioList);

		String campaignDataAsString = mapper.writeValueAsString(campaignData);
		
		// Write the file
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

	// TODO: Add SimpleTag list
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
  				List<Folio> folioList = campaignData.getFolioList();
  				List<User> userList = campaignData.getUserList();
  				
  				Map<String, User> oldUserIdToNewUserMap = restoreCampaignUsers(userList);
  				restoreCampaign(campaign, oldUserIdToNewUserMap);
  				String campaignId = campaign.getId();
  				restoreFolios(folioList, campaignId);
  				
  			} catch (IOException e) {
  				
  			}
  			
  			System.out.println("Tada!");
	  	}
	}
	
	public void restoreCampaignData(String fileName) {
		// Should use more error handling
		File campaignDataFile = null;
		campaignDataFile = new File(fileName);
		String campaignDataAsString = null;
		try {
			campaignDataAsString = new Scanner(campaignDataFile).useDelimiter("\\Z").next();
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
		
		
		Map<String, User> oldUserIdToNewUserMap = restoreCampaignUsers(campaignData.getUserList());
		Campaign campaign = campaignData.getCampaign();
		restoreCampaign(campaign, oldUserIdToNewUserMap);
		String campaignId = campaign.getId();
		List<Folio> folioList = campaignData.getFolioList();
		restoreFolios(folioList, campaignId);
		
	}

	/**
	 * Users should be restored first because their id's will modify the contents of the campaign.
	 * Note that the ROLES should not vary from implementation to implementation so those should 
	 * not need to be reconciled.
	 * @param userList
	 */
	public Map<String, User> restoreCampaignUsers(List<User> userList) {
		Map<String, User> oldUserIdToNewUserMap = new HashMap<>();
		
		for (User oldUser : userList) {
			oldUserIdToNewUserMap.put(oldUser.getId(), null);
		}
		

		return oldUserIdToNewUserMap;
	}
	
	public void restoreCampaign(Campaign campaign, Map<String, User> oldUserIdToNewUserMap) {
		Campaign existingCampaign = campaignRepository.findOneByName(campaign.getName());
		// In case the Ids don't match. This is risky because in reality these might not
		// be the same campaign!
		if (existingCampaign != null) {
			campaign.setId(existingCampaign.getId());
		}
		User owner = oldUserIdToNewUserMap.get(campaign.getOwnerId());
		if (owner == null) {
			// throw exception! Can't have a null owner!
		}
		campaign.setOwnerId(owner.getId());
		List<String> newUserIdList = new ArrayList<>();
		for (String oldUserId : campaign.getGameMasterIdList()) {
			User newUser = oldUserIdToNewUserMap.get(oldUserId);
			if (newUser != null) {
				newUserIdList.add(newUser.getId());
			}
		}
		campaignRepository.save(campaign);
	}
	
	public void restoreFolios(List<Folio> folioList, String campaignId) {
		for (Folio backupFolio : folioList) {
			Folio currentFolio = folioRepository.findOneByCampaignIdAndTitle(campaignId, backupFolio.getTitle());
			if (currentFolio != null) {
				backupFolio.setId(currentFolio.getId());
			}
			folioRepository.save(backupFolio);
		}
	}
	
	public void importCampaign(String filePath) {
		// TODO: write this
	}
}
