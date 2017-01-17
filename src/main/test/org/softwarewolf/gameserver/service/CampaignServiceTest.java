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
	private static List<User> allUsers;
	private static CampaignUser firstCampaignUser;
	private static String firstCampaignUserId = UUID.randomUUID().toString();
	private static CampaignUser secondCampaignUser;
	private static String secondCampaignUserId = UUID.randomUUID().toString();
	private static CampaignUser thirdCampaignUser;
	private static String thirdCampaignUserId = UUID.randomUUID().toString();
	private static List<CampaignUser> allfirstCampaignUsers;
	private static Folio firstCampaignFolio;
	private static String firstCampaignFolioContent = "Some content";
	private static String firstCampaignFolioId = UUID.randomUUID().toString();
	private static Campaign firstCampaign;
	private static String firstCampaignName = "First Campaign";
	private static String firstCampaignId = UUID.randomUUID().toString();
	
	@BeforeClass
	public static void testPrep() {
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
		allUsers = new ArrayList<>(Arrays.asList(new User[] {firstUser, secondUser, thirdUser}));
		
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
		allfirstCampaignUsers = new ArrayList<>(Arrays.asList(new CampaignUser[] {firstCampaignUser, secondCampaignUser, thirdCampaignUser}));
		
		firstCampaignFolio = new Folio();
		firstCampaignFolio.setCampaignId(firstCampaignId);
		firstCampaignFolio.setTitle(firstCampaignName);
		firstCampaignFolio.setContent(firstCampaignFolioContent);
		firstCampaignFolio.setId(firstCampaignFolioId);
		firstCampaignFolio.addOwner(firstUserId);
		firstCampaignFolio.addReader(secondUserId);
		firstCampaignFolio.addReader(thirdUserId);
		
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
	}
	
	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testInitCampaignDto() {
		List<Campaign> allCampaigns = new ArrayList<>();
		allCampaigns.add(firstCampaign);
		doReturn(allCampaigns).when(campaignRepository).findAll();
		doReturn(firstCampaign).when(campaignRepository).findOne(firstCampaignId);
		doReturn(allUsers).when(userService).findAll();
		doReturn(firstUserId).when(userService).getCurrentUserId();
		doReturn(allfirstCampaignUsers).when(campaignUserService).findAllByCampaignId(firstCampaignId);
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
		assertEquals("CampaignDto.campaignList is incorrect", 1, campaignDto.getCampaignList().size());
	}
}
