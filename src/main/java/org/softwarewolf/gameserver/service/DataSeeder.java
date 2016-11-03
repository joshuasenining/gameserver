package org.softwarewolf.gameserver.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.softwarewolf.gameserver.domain.Campaign;
import org.softwarewolf.gameserver.domain.Folio;
import org.softwarewolf.gameserver.domain.SimpleTag;
import org.softwarewolf.gameserver.domain.User;
import org.softwarewolf.gameserver.repository.CampaignRepository;
import org.softwarewolf.gameserver.repository.SimpleGrantedAuthorityRepository;
import org.softwarewolf.gameserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DataSeeder {
	private static final String ROLE_USER = "ROLE_USER";
	private static final String ROLE_ADMIN = "ROLE_ADMIN";
	private static final String ROLE_GAMEMASTER = "ROLE_GAMEMASTER";
	private static final String ADMIN = "admin";
	private static final String USER = "user";
	private static final String GM = "gm";
	private static final String SWORD_AND_SORCERY = "Sword and Sorcery";
	private static final String SPACE_OPERA = "Space Opera";
	private static final String MODERN = "Modern";
	private static final String KINGDOM = "Kingdom";
	private static final String RIVAL_KINGDOM = "Rival Kingdom";
	private static final String MAGIC_COUNTY = "Magic County";
	private static final String MAGIC_CITY = "Magic City";
	private static final String MAGIC_TOWN = "Magic Town";
	private static final String COUNTRY = "Country";
	private static final String STATE = "State";
	private static final String COUNTY = "County";
	private static final String CITY = "City";
	private static final String TOWN = "Town";
	private static final String VILLAGE = "Village";
	private static final String MERCHANTS_GUILD = "Merchants Guild";
	private static final String COVEN = "A witches coven";
	private static final String BLOOD_MOON = "The Blood Moon Coven";
	private static final String SPACE_STATION = "Space Station";
	private static final String KINGDOM_OF_MIDLAND = KINGDOM + " of Midland";
	private static final String GOLDEN_ROAD = "Golden Road Trading League";
	private static final String LOCATION = "Location";
	private static final String ORGANIZATION = "Organization";
	
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private SimpleGrantedAuthorityRepository sgaRepo;
	
	@Autowired 
	private CampaignRepository campaignRepo;
	
	@Autowired
	private SimpleTagService simpleTagService;
	
	@Autowired
	private FolioService folioService;
	
	public void cleanRepos() {
		sgaRepo.deleteAll();
		userRepo.deleteAll();
		campaignRepo.deleteAll();
		folioService.deleteAll();
		simpleTagService.deleteAll();
	}
	
	public void seedData() {
		Map<String, SimpleGrantedAuthority> roleMap = seedRoles();
		Map<String, User> userMap = seedUsers(roleMap);
		Map<String, Campaign> campaignMap = seedCampaign(userMap);

		Map<String, Map<String, SimpleTag>> tagMap = seedTags(campaignMap);
		seedFolios(campaignMap, tagMap);
/*		
		Map<String, LocationType> locationTypeMap = seedLocationType(campaignMap);
		Map<String, Location> locationMap = seedLocations(campaignMap, locationTypeMap);
		Map<String, OrganizationType> organizationTypeMap = seedOrganizationType(campaignMap);
		Map<String, Organization> organizationMap = seedOrganizations(campaignMap, organizationTypeMap);
		seedOrganizationRanks(campaignMap, organizationMap);
		seedFolios(organizationMap, locationMap, campaignMap);
		*/
	}
	
	private Map<String, SimpleGrantedAuthority> seedRoles() {
		SimpleGrantedAuthority roleAdmin = sgaRepo.findByRole(ROLE_ADMIN);
		SimpleGrantedAuthority roleUser = sgaRepo.findByRole(ROLE_USER);
		SimpleGrantedAuthority roleGamemaster = sgaRepo.findByRole(ROLE_GAMEMASTER);
	
		if (roleAdmin == null) {
			roleAdmin = new SimpleGrantedAuthority(ROLE_ADMIN);
			roleAdmin = sgaRepo.save(roleAdmin);
		}
		if (roleUser == null) {
			roleUser = new SimpleGrantedAuthority(ROLE_USER);
			roleUser = sgaRepo.save(roleUser);
		}
		if (roleGamemaster == null) {
			roleGamemaster = new SimpleGrantedAuthority(ROLE_GAMEMASTER);
			roleGamemaster = sgaRepo.save(roleGamemaster);
		}
		Map<String, SimpleGrantedAuthority> roles = new HashMap<>();
		roles.put(ROLE_ADMIN, roleAdmin);
		roles.put(ROLE_USER, roleUser);
		roles.put(ROLE_GAMEMASTER, roleGamemaster);
		
		return roles;
	}
	
	private Map<String, User> seedUsers(Map<String, SimpleGrantedAuthority> roleMap) {
		Map<String, User> userMap = new HashMap<>();

		List<SimpleGrantedAuthority> roleList = new ArrayList<>();
		roleList.add(roleMap.get(ROLE_USER));
		saveUser(USER, roleList, userMap);
		
		roleList.add(roleMap.get(ROLE_GAMEMASTER));
		saveUser(GM, roleList, userMap);
		
		roleList.add(roleMap.get(ROLE_ADMIN));
		saveUser(ADMIN, roleList, userMap);


		return userMap;
	}
	
	private void saveUser(String name, List<SimpleGrantedAuthority> roleList, Map<String, User> userMap) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		User user = userRepo.findOneByUsername(name);
		if (user == null) {
			user = new User();
			user.setUsername(name);
			user.setFirstName(name);
			String userPwd = encoder.encode(name);
			user.setPassword(userPwd);
			user.setEmail("dm_tim@yahoo.com");
			user.setAccountNonExpired(true);
			user.setAccountNonLocked(true);
			user.setCredentialsNonExpired(true);
			user.setEnabled(true);
			for (SimpleGrantedAuthority auth : roleList) {
				user.addSimpleGrantedAuthority(auth);
			}
			user = userRepo.save(user);
			userMap.put(name, user);
		} else {
			System.out.println("Found the user in the db: " + user.toString());
		}
	}
	
	private Map<String, Campaign> seedCampaign(Map<String, User> userMap) {
		Map<String, Campaign> campaignMap = new HashMap<>();
		String gmId = (userMap.get(GM).getId());

		saveCampaign(SWORD_AND_SORCERY, "Generic sword and sorcery campaign", gmId, campaignMap);
		saveCampaign(SPACE_OPERA, "Generic space opera campaign", gmId, campaignMap);
		saveCampaign(MODERN, "Generic modern campaign", gmId, campaignMap);
		
		return campaignMap;
	}
	
	private void saveCampaign(String name, String description, String ownerId, Map<String, Campaign> campaignMap) {
		Campaign campaign = campaignRepo.findOneByName(name);
		if (campaign == null) {
			campaign = new Campaign();
			campaign.setName(name);
			campaign.setDescription(description);
			campaign.setOwnerId(ownerId);
			campaign = campaignRepo.save(campaign);
			campaignMap.put(name, campaign);
		}
	}
	
	private Map<String, Map<String, SimpleTag>> seedTags(Map<String, Campaign> campaignMap) {
		Map<String, Map<String, SimpleTag>> allTags = new HashMap<>();
		
		String sAndSCampaignId = campaignMap.get(SWORD_AND_SORCERY).getId();
		Map<String, SimpleTag> sAndSTagMap = new HashMap<>();
		saveTag(sAndSTagMap, KINGDOM, sAndSCampaignId);
		saveTag(sAndSTagMap, KINGDOM_OF_MIDLAND, sAndSCampaignId);
		saveTag(sAndSTagMap, RIVAL_KINGDOM, sAndSCampaignId);
		saveTag(sAndSTagMap, MAGIC_COUNTY, sAndSCampaignId);
		saveTag(sAndSTagMap, MAGIC_CITY, sAndSCampaignId);
		saveTag(sAndSTagMap, MAGIC_TOWN, sAndSCampaignId);
		saveTag(sAndSTagMap, TOWN, sAndSCampaignId);
		saveTag(sAndSTagMap, VILLAGE, sAndSCampaignId);
		saveTag(sAndSTagMap, MERCHANTS_GUILD, sAndSCampaignId);
		saveTag(sAndSTagMap, GOLDEN_ROAD, sAndSCampaignId);
		saveTag(sAndSTagMap, COVEN, sAndSCampaignId);
		saveTag(sAndSTagMap, BLOOD_MOON, sAndSCampaignId);
		saveTag(sAndSTagMap, LOCATION, sAndSCampaignId);
		saveTag(sAndSTagMap, ORGANIZATION, sAndSCampaignId);
		allTags.put(sAndSCampaignId, sAndSTagMap);
		
		String modernCampaignId = campaignMap.get(MODERN).getId();
		Map<String, SimpleTag> modernTagMap = new HashMap<>();
		saveTag(modernTagMap, COUNTRY, modernCampaignId);
		saveTag(modernTagMap, COUNTY, modernCampaignId);
		saveTag(modernTagMap, CITY, modernCampaignId);
		saveTag(modernTagMap, TOWN, modernCampaignId);
		saveTag(modernTagMap, SPACE_STATION, modernCampaignId);
		allTags.put(modernCampaignId, modernTagMap);

		String spaceOperaCampaignId = campaignMap.get(SPACE_OPERA).getId();
		Map<String, SimpleTag> spaceOperaTagMap = new HashMap<>();
		saveTag(spaceOperaTagMap, COUNTRY, spaceOperaCampaignId);
		saveTag(spaceOperaTagMap, CITY, spaceOperaCampaignId);
		saveTag(spaceOperaTagMap, STATE, spaceOperaCampaignId);
		saveTag(spaceOperaTagMap, SPACE_STATION, spaceOperaCampaignId);
		allTags.put(spaceOperaCampaignId, spaceOperaTagMap);

		return allTags;
	}
	
	private void saveTag(Map<String, SimpleTag> tagMap, String tagName, String campaignId) {
		SimpleTag newTag = new SimpleTag(tagName, campaignId);
		newTag = simpleTagService.save(newTag);
		tagMap.put(tagName, newTag);
	}
		
	private void seedFolios(Map<String, Campaign> campaignMap, Map<String, Map<String, SimpleTag>> tagMap) {
		String sAndSCampaignId = campaignMap.get(SWORD_AND_SORCERY).getId();
		Folio goldenRoadFolio = new Folio();
		goldenRoadFolio.setCampaignId(sAndSCampaignId);
		goldenRoadFolio.setTitle("Golden Road Trading League Intro");
		goldenRoadFolio.setContent("<H1>The Golden Road Trading League</H1><p>This is a big merchant guild</p>");		
		goldenRoadFolio.addTag(tagMap.get(sAndSCampaignId).get(MERCHANTS_GUILD));
		goldenRoadFolio.addTag(tagMap.get(sAndSCampaignId).get(GOLDEN_ROAD));
		goldenRoadFolio.addTag(tagMap.get(sAndSCampaignId).get(KINGDOM_OF_MIDLAND));
		try {
			folioService.save(goldenRoadFolio);
		} catch (Exception e) {
			// ignore;
		}
		
		Folio kindomOfMidlandFolio = new Folio();
		kindomOfMidlandFolio.setCampaignId(sAndSCampaignId);
		kindomOfMidlandFolio.setTitle("The Kingdom of Midland Geography");
		kindomOfMidlandFolio.setContent("<H1>The Kingdom of Midland</H1><p>This is a big kingdom that stretches from east to west.</p>");		
		kindomOfMidlandFolio.addTag(tagMap.get(sAndSCampaignId).get(LOCATION));
		kindomOfMidlandFolio.addTag(tagMap.get(sAndSCampaignId).get(KINGDOM_OF_MIDLAND));
		try {
			folioService.save(kindomOfMidlandFolio);
		} catch (Exception e) {
			// ignore;
		}		
		
		Folio kindomOfMidlandOrgFolio = new Folio();
		kindomOfMidlandOrgFolio.setCampaignId(sAndSCampaignId);
		kindomOfMidlandOrgFolio.setTitle("The Kingdom of Midland");
		kindomOfMidlandOrgFolio.setContent("<H1>The Kingdom of Midland</H1><p>This is a feudal society.</p>");		
		kindomOfMidlandOrgFolio.addTag(tagMap.get(sAndSCampaignId).get(ORGANIZATION));
		kindomOfMidlandOrgFolio.addTag(tagMap.get(sAndSCampaignId).get(KINGDOM_OF_MIDLAND));
		try {
			folioService.save(kindomOfMidlandOrgFolio);
		} catch (Exception e) {
			// ignore;
		}	

		Folio magicCountyFolio = new Folio();
		magicCountyFolio.setCampaignId(sAndSCampaignId);
		magicCountyFolio.setTitle("The Kingdom of Midland");
		magicCountyFolio.setContent("<H1>The Kingdom of Midland</H1><p>This is a feudal society.</p>");		
		magicCountyFolio.addTag(tagMap.get(sAndSCampaignId).get(ORGANIZATION));
		magicCountyFolio.addTag(tagMap.get(sAndSCampaignId).get(KINGDOM_OF_MIDLAND));
		magicCountyFolio.addTag(tagMap.get(sAndSCampaignId).get(MAGIC_COUNTY));
		try {
			folioService.save(magicCountyFolio);
		} catch (Exception e) {
			// ignore;
		}	
	}
}
