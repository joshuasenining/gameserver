package org.softwarewolf.gameserver.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.softwarewolf.gameserver.config.AppConfig;
import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.softwarewolf.gameserver.domain.Campaign;
import org.softwarewolf.gameserver.domain.CampaignUser;
import org.softwarewolf.gameserver.domain.Folio;
import org.softwarewolf.gameserver.domain.User;
import org.softwarewolf.gameserver.domain.dto.CampaignDto;
import org.softwarewolf.gameserver.repository.CampaignRepository;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {AppConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(basePackages = {"org.softwarewolf.gameserver"})
public class CampaignServiceTest {
	@InjectMocks
	private CampaignService campaignService;

	@Mock
	private CampaignRepository campaignRepository;
	
	@Mock
	private UserService userService;
	
	@Mock 
	private CampaignUserService campaignUserService;
	
	@Mock
	private FolioService folioService;
	
	private static User firstUser;
	private static String firstUserEmail = "firstUser@gmail.com";
	private static String firstUserFirstName = "First";
	private static String firstUserLastName = "User";
	private static String firstUserName = "firstuser";
	private static String firstUserId = UUID.randomUUID().toString();
	private static User secondUser;
	private static String secondUserEmail = "secondUser@gmail.com";
	private static String secondUserFirstName = "Second";
	private static String secondUserLastName = "User";
	private static String secondUserName = "seconduser";
	private static String secondUserId = UUID.randomUUID().toString();
	private static User thirdUser;
	private static String thirdUserEmail = "thirdUser@gmail.com";
	private static String thirdUserFirstName = "Third";
	private static String thirdUserLastName = "User";
	private static String thirdUserName = "thriduser";
	private static String thirdUserId = UUID.randomUUID().toString();
	private static User fourthUser;
	private static String fourthUserEmail = "fourthUser@gmail.com";
	private static String fourthUserFirstName = "Fourth";
	private static String fourthUserLastName = "User";
	private static String fourthUserName = "fourthuser";
	private static String fourthUserId = UUID.randomUUID().toString();
	private static List<User> allUsers;
	private static CampaignUser firstCampaignUser;
	private static String firstCampaignUserId = UUID.randomUUID().toString();
	private static CampaignUser secondCampaignUser;
	private static String secondCampaignUserId = UUID.randomUUID().toString();
	private static CampaignUser thirdCampaignUser;
	private static String thirdCampaignUserId = UUID.randomUUID().toString();
	private static CampaignUser fourthCampaignUser;
	private static String fourthCampaignUserId = UUID.randomUUID().toString();
	private static CampaignUser fifthCampaignUser;
	private static String fifthCampaignUserId = UUID.randomUUID().toString();
	private static CampaignUser sixthCampaignUser;
	private static String sixthCampaignUserId = UUID.randomUUID().toString();
	private static CampaignUser seventhCampaignUser;
	private static String seventhCampaignUserId = UUID.randomUUID().toString();
	private static List<CampaignUser> allFirstCampaignUsers;
	private static List<CampaignUser> allSecondCampaignUsers;
	private static Folio firstCampaignFolio;
	private static String firstCampaignFolioContent = "Some content";
	private static String firstCampaignFolioId = UUID.randomUUID().toString();
	private static Folio secondCampaignFolio;
	private static String secondCampaignFolioContent = "Some other content";
	private static String secondCampaignFolioId = UUID.randomUUID().toString();
	private static Campaign firstCampaign;
	private static String firstCampaignName = "First Campaign";
	private static String firstCampaignId = UUID.randomUUID().toString();
	private static Campaign secondCampaign;
	private static String secondCampaignName = "Second Campaign";
	private static String secondCampaignId = UUID.randomUUID().toString();
	
	@BeforeClass
	public static void testPrep() {
		// App users
		firstUser = new User();
		firstUser.setUsername(firstUserName);
		firstUser.setEmail(firstUserEmail);
		firstUser.setFirstName(firstUserFirstName);
		firstUser.setLastName(firstUserLastName);
		firstUser.setId(firstUserId);
		
		secondUser = new User();
		secondUser.setUsername(secondUserName);
		secondUser.setEmail(secondUserEmail);
		secondUser.setFirstName(secondUserFirstName);
		secondUser.setLastName(secondUserLastName);
		secondUser.setId(secondUserId);
		
		thirdUser = new User();
		thirdUser.setUsername(thirdUserName);
		thirdUser.setEmail(thirdUserEmail);
		thirdUser.setFirstName(thirdUserFirstName);
		thirdUser.setLastName(thirdUserLastName);
		thirdUser.setId(thirdUserId);
		
		fourthUser = new User();
		fourthUser.setUsername(fourthUserName);
		fourthUser.setEmail(fourthUserEmail);
		fourthUser.setFirstName(fourthUserFirstName);
		fourthUser.setLastName(fourthUserLastName);
		fourthUser.setId(fourthUserId);	
		allUsers = new ArrayList<>(Arrays.asList(new User[] {firstUser, secondUser, thirdUser, fourthUser}));
		
		// First Campaign users
		firstCampaignUser = new CampaignUser();
		firstCampaignUser.setCampaignId(firstCampaignId);
		firstCampaignUser.setId(firstCampaignUserId);
		firstCampaignUser.setUserId(firstUserId);
		firstCampaignUser.setUserName(firstUserName);
		firstCampaignUser.setPermission(ControllerUtils.ROLE_OWNER);
		
		secondCampaignUser = new CampaignUser();
		secondCampaignUser.setCampaignId(firstCampaignId);
		secondCampaignUser.setId(secondCampaignUserId);
		secondCampaignUser.setUserId(secondUserId);
		secondCampaignUser.setUserName(secondUserName);
		secondCampaignUser.setPermission(ControllerUtils.ROLE_USER);
		
		thirdCampaignUser = new CampaignUser();
		thirdCampaignUser.setCampaignId(firstCampaignId);
		thirdCampaignUser.setId(thirdCampaignUserId);
		thirdCampaignUser.setUserId(thirdUserId);
		thirdCampaignUser.setUserName(thirdUserName);
		thirdCampaignUser.setPermission(ControllerUtils.ROLE_USER);
		allFirstCampaignUsers = new ArrayList<>(Arrays.asList(new CampaignUser[] {firstCampaignUser, secondCampaignUser, thirdCampaignUser}));
		
		// Second Campaign users
		fourthCampaignUser = new CampaignUser();
		fourthCampaignUser.setCampaignId(secondCampaignId);
		fourthCampaignUser.setId(fourthCampaignUserId);
		fourthCampaignUser.setUserId(fourthUserId);
		fourthCampaignUser.setUserName(fourthUserName);
		fourthCampaignUser.setPermission(ControllerUtils.ROLE_OWNER);

		fifthCampaignUser = new CampaignUser();
		fifthCampaignUser.setCampaignId(secondCampaignId);
		fifthCampaignUser.setId(fifthCampaignUserId);
		fifthCampaignUser.setUserId(firstUserId);
		fifthCampaignUser.setUserName(firstUserName);
		fifthCampaignUser.setPermission(ControllerUtils.ROLE_GAMEMASTER);

		sixthCampaignUser = new CampaignUser();
		sixthCampaignUser.setCampaignId(secondCampaignId);
		sixthCampaignUser.setId(sixthCampaignUserId);
		sixthCampaignUser.setUserId(secondUserId);
		sixthCampaignUser.setUserName(secondUserName);
		sixthCampaignUser.setPermission(ControllerUtils.ROLE_USER);

		seventhCampaignUser = new CampaignUser();
		seventhCampaignUser.setCampaignId(secondCampaignId);
		seventhCampaignUser.setId(seventhCampaignUserId);
		seventhCampaignUser.setUserId(thirdUserId);
		seventhCampaignUser.setUserName(thirdUserName);
		seventhCampaignUser.setPermission(ControllerUtils.ROLE_USER);
		allSecondCampaignUsers = new ArrayList<>(Arrays.asList(new CampaignUser[] {fourthCampaignUser, fifthCampaignUser, sixthCampaignUser, seventhCampaignUser}));

		firstCampaignFolio = new Folio();
		firstCampaignFolio.setCampaignId(firstCampaignId);
		firstCampaignFolio.setTitle(firstCampaignName);
		firstCampaignFolio.setContent(firstCampaignFolioContent);
		firstCampaignFolio.setId(firstCampaignFolioId);
		firstCampaignFolio.addOwner(firstUserId);
		firstCampaignFolio.addReader(secondUserId);
		firstCampaignFolio.addReader(thirdUserId);
		
		secondCampaignFolio = new Folio();
		secondCampaignFolio.setCampaignId(secondCampaignId);
		secondCampaignFolio.setTitle(secondCampaignName);
		secondCampaignFolio.setContent(secondCampaignFolioContent);
		secondCampaignFolio.setId(secondCampaignFolioId);
		secondCampaignFolio.addOwner(fourthUserId);
		secondCampaignFolio.addOwner(firstUserId);
		secondCampaignFolio.addReader(secondUserId);
		secondCampaignFolio.addReader(thirdUserId);
		
		firstCampaign = new Campaign();
		firstCampaign.setId(firstCampaignId);
		firstCampaign.setCampaignFolioId(UUID.randomUUID().toString());
		firstCampaign.setName(firstCampaignName);
		List<String> firstCampaignOwnerList = new ArrayList<>();
		firstCampaignOwnerList.add(firstUser.getId());
		firstCampaign.setOwnerList(firstCampaignOwnerList);
		List<String> firstCampaignPlayerList = new ArrayList<>();
		firstCampaignPlayerList.addAll(new ArrayList<>(Arrays.asList(new String[] {secondUser.getId(), thirdUser.getId()})));
		firstCampaign.setPlayerList(firstCampaignPlayerList);
		firstCampaign.setCampaignFolioId(firstCampaignFolioId);
		
		secondCampaign = new Campaign();
		secondCampaign.setId(secondCampaignId);
		secondCampaign.setCampaignFolioId(UUID.randomUUID().toString());
		secondCampaign.setName(firstCampaignName);
		List<String> secondCampaignOwnerList = new ArrayList<>();
		secondCampaignOwnerList.add(fourthUser.getId());
		secondCampaign.setOwnerList(secondCampaignOwnerList);
		List<String> secondCampaignGmList = new ArrayList<>();
		secondCampaignGmList.add(firstUserId);
		secondCampaign.setGameMasterList(secondCampaignGmList);
		List<String> secondCampaignPlayerList = new ArrayList<>();
		secondCampaignPlayerList.addAll(new ArrayList<>(Arrays.asList(new String[] {secondUser.getId(), thirdUser.getId()})));
		secondCampaign.setPlayerList(secondCampaignPlayerList);
		secondCampaign.setCampaignFolioId(secondCampaignFolioId);
	}
	
	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}
	
	/**
	 * Conditions:
	 * Two campaigns in list
	 * One owner (firstUser)
	 * Two players (secondUser, thirdUser)
	 */
	@Test
	public void testInitFirstCampaignDto() {
		List<Campaign> allCampaigns = new ArrayList<>(Arrays.asList(new Campaign[] {firstCampaign, secondCampaign}));
		doReturn(allCampaigns).when(campaignRepository).findAll();
		doReturn(firstCampaign).when(campaignRepository).findOne(firstCampaignId);
		doReturn(allUsers).when(userService).findAll();
		doReturn(firstUserId).when(userService).getCurrentUserId();
		doReturn(allFirstCampaignUsers).when(campaignUserService).findAllByCampaignId(firstCampaignId);
		doReturn(firstCampaignFolio).when(folioService).findOne(firstCampaignFolioId);
		CampaignDto campaignDto = new CampaignDto();
		campaignService.initCampaignDto(firstCampaignId, campaignDto);
		Campaign campaign = campaignDto.getCampaign();
		assertEquals("Campaign id was altered", firstCampaignId, campaign.getId());
		assertEquals("Campaign folioId was altered", firstCampaignFolioId, campaign.getCampaignFolioId());
		assertEquals("Campaign owner list was altered", 1, campaign.getOwnerList().size());
		assertTrue("Campaign owner was altered", campaign.getOwnerList().contains(firstUserId));
		assertEquals("Campaign gamemaster list was altered", 0, campaign.getGameMasterList().size());
		assertEquals("Campaign user list was altered", 2, campaign.getPlayerList().size());
		List<String> playerIdList = new ArrayList<>(Arrays.asList(new String[] {secondUserId, thirdUserId}));
		assertTrue("Campaign user list was altered", campaign.getPlayerList().containsAll(playerIdList));
		assertTrue("CampaignDto.isOwner is incorrect", campaignDto.getIsOwner().booleanValue());
		assertEquals("CampaignDto.campaignList is incorrect", 2, campaignDto.getCampaignList().size());
	}

	/**
	 * Conditions:
	 * Two campaigns in list
	 * One owner (fourthUser)
	 * One GM (firstUser)
	 * Two players (secondUser, thirdUser)
	 */
	@Test
	public void testInitSecondCampaignDto() {
		List<Campaign> allCampaigns = new ArrayList<>(Arrays.asList(new Campaign[] {firstCampaign, secondCampaign}));
		doReturn(allCampaigns).when(campaignRepository).findAll();
		doReturn(secondCampaign).when(campaignRepository).findOne(secondCampaignId);
		doReturn(allUsers).when(userService).findAll();
		doReturn(fourthUserId).when(userService).getCurrentUserId();
		doReturn(allSecondCampaignUsers).when(campaignUserService).findAllByCampaignId(secondCampaignId);
		doReturn(secondCampaignFolio).when(folioService).findOne(secondCampaignFolioId);
		CampaignDto campaignDto = new CampaignDto();
		campaignService.initCampaignDto(secondCampaignId, campaignDto);
		Campaign campaign = campaignDto.getCampaign();
		assertEquals("Campaign id was altered", secondCampaignId, campaign.getId());
		assertEquals("Campaign folioId was altered", secondCampaignFolioId, campaign.getCampaignFolioId());
		assertEquals("Campaign owner list was altered", 1, campaign.getOwnerList().size());
		assertTrue("Campaign owner was altered", campaign.getOwnerList().contains(fourthUserId));
		assertEquals("Campaign gamemaster list was altered", 1, campaign.getGameMasterList().size());
		assertEquals("Campaign user list was altered", 2, campaign.getPlayerList().size());
		List<String> playerIdList = new ArrayList<>(Arrays.asList(new String[] {secondUserId, thirdUserId}));
		assertTrue("Campaign user list was altered", campaign.getPlayerList().containsAll(playerIdList));
		assertTrue("CampaignDto.isOwner is incorrect", campaignDto.getIsOwner().booleanValue());
		assertEquals("CampaignDto.campaignList is incorrect", 2, campaignDto.getCampaignList().size());
	}
}
