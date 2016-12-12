package org.softwarewolf.gameserver.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.softwarewolf.gameserver.domain.Campaign;
import org.softwarewolf.gameserver.domain.CampaignUser;
import org.softwarewolf.gameserver.domain.Folio;
import org.softwarewolf.gameserver.domain.SimpleTag;
import org.softwarewolf.gameserver.domain.User;
import org.softwarewolf.gameserver.domain.dto.CampaignDto;
import org.softwarewolf.gameserver.repository.DeleteableRoleRepository;
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
	private static final String ROLE_OWNER = "ROLE_OWNER";
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
	private static final String OAKDALE = "Oakdale";
	private static final String MAYOR = "Mayor";
	private static final String SHOPKEEPER = "Shopkeeper";
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
	private DeleteableRoleRepository roleRepo;
	
	@Autowired
	private SimpleGrantedAuthorityRepository sgaRepo;
	
	@Autowired 
	private CampaignService campaignService;
	
	@Autowired
	private SimpleTagService simpleTagService;
	
	@Autowired
	private FolioService folioService;
	
	@Autowired
	private CampaignUserService campaignUserService;
	
	public void cleanRepos() {
		Campaign sAndSCampaign = campaignService.findOneByName(SWORD_AND_SORCERY);
		if (sAndSCampaign != null) {
			campaignUserService.deleteByCampaignId(sAndSCampaign.getId());
			if (sAndSCampaign != null) {
				folioService.deleteByCampaignId(sAndSCampaign.getId());
				simpleTagService.deleteByCampaignId(sAndSCampaign.getId());
				campaignUserService.deleteByCampaignId(sAndSCampaign.getId());
				campaignService.deleteByName(SWORD_AND_SORCERY);
			}
		}
		Campaign modernCampaign  = campaignService.findOneByName(MODERN);
		if (modernCampaign != null) {
			campaignUserService.deleteByCampaignId(modernCampaign.getId());
			if (modernCampaign != null) {
				folioService.deleteByCampaignId(modernCampaign.getId());
				simpleTagService.deleteByCampaignId(modernCampaign.getId());
				campaignUserService.deleteByCampaignId(modernCampaign.getId());
				campaignService.deleteByName(MODERN);
			}
		}
		Campaign spaceCampaign = campaignService.findOneByName(SPACE_OPERA);
		if (spaceCampaign != null) {
			campaignUserService.deleteByCampaignId(spaceCampaign.getId());
			if (spaceCampaign != null) {
				folioService.deleteByCampaignId(spaceCampaign.getId());
				simpleTagService.deleteByCampaignId(spaceCampaign.getId());
				campaignUserService.deleteByCampaignId(spaceCampaign.getId());
				campaignService.deleteByName(SPACE_OPERA);
			}
		}
		campaignUserService.deleteAll();
		
		try {
			roleRepo.deleteByRole(ROLE_ADMIN);
		} catch (Exception e) {	}
		try {
			roleRepo.deleteByRole(ROLE_GAMEMASTER);
		} catch (Exception e) {	}
		try {
			roleRepo.deleteByRole(ROLE_USER);
		} catch (Exception e) {	}

		User adminUser = userRepo.findOneByUsername(ADMIN);
		if (adminUser != null) {
			userRepo.delete(adminUser);
		}		
		User gmUser = userRepo.findOneByUsername(GM);
		if (gmUser != null) {
			userRepo.delete(gmUser);
		}
		User userUser = userRepo.findOneByUsername(USER);
		if (userUser != null) {
			userRepo.delete(userUser);
		}
	}
	
	public void seedData() {
		Map<String, SimpleGrantedAuthority> roleMap = seedRoles();
		Map<String, User> userMap = seedUsers(roleMap);
		Map<String, Campaign> campaignMap = seedCampaign(userMap);

		Map<String, Map<String, SimpleTag>> tagMap = seedTags(campaignMap);
		seedFolios(campaignMap, tagMap);
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
			user.setEmail("noSpam@yahoo.com");
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
		
		User playerUser = userMap.get(USER);
		Campaign sAs = campaignMap.get(SWORD_AND_SORCERY);
		CampaignUser player = new CampaignUser(sAs.getId(), ROLE_USER, playerUser.getId(), USER);
		campaignUserService.save(player);
		
		return campaignMap;
	}
	
	private void saveCampaign(String name, String description, String ownerId, Map<String, Campaign> campaignMap) {
		Campaign campaign = campaignService.findOneByName(name);
		if (campaign == null) {
			CampaignDto campaignDto = new CampaignDto();
			campaign = new Campaign();
			campaign.setName(name);
			campaignDto.setOwnerId(ownerId);
			campaignDto.setCampaign(campaign);
			Folio campaignFolio = new Folio();
			campaignFolio.setContent(description);
			campaignDto.setCampaignFolio(campaignFolio);
			try {
				campaignService.saveCampaign(campaignDto);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			campaign = campaignDto.getCampaign();
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
		saveTag(sAndSTagMap, OAKDALE, sAndSCampaignId);
		saveTag(sAndSTagMap, MAYOR, sAndSCampaignId);
		saveTag(sAndSTagMap, SHOPKEEPER, sAndSCampaignId);
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
		User gm = userRepo.findOneByUsername(GM);
		User user = userRepo.findOneByUsername(USER);
		
		String sAndSCampaignId = campaignMap.get(SWORD_AND_SORCERY).getId();
		Folio goldenRoadFolio = new Folio();
		goldenRoadFolio.setCampaignId(sAndSCampaignId);
		goldenRoadFolio.addOwner(gm.getId());
		goldenRoadFolio.setTitle("Golden Road Trading League Intro");
		goldenRoadFolio.setContent("<H1>The Golden Road Trading League</H1><p>This is a big merchant guild</p>");		
		goldenRoadFolio.addTag(tagMap.get(sAndSCampaignId).get(MERCHANTS_GUILD));
		goldenRoadFolio.addTag(tagMap.get(sAndSCampaignId).get(GOLDEN_ROAD));
		goldenRoadFolio.addTag(tagMap.get(sAndSCampaignId).get(KINGDOM_OF_MIDLAND));
		goldenRoadFolio.addUser(user.getId());
		try {
			folioService.save(goldenRoadFolio);
		} catch (Exception e) {
			// ignore;
		}
		
		Folio kindomOfMidlandFolio = new Folio();
		kindomOfMidlandFolio.setCampaignId(sAndSCampaignId);
		kindomOfMidlandFolio.addOwner(gm.getId());
		kindomOfMidlandFolio.setTitle("The Kingdom of Midland Geography");
		kindomOfMidlandFolio.setContent("<H1>The Kingdom of Midland</H1><p>This is a big kingdom that stretches from east to west.</p>");		
		kindomOfMidlandFolio.addTag(tagMap.get(sAndSCampaignId).get(LOCATION));
		kindomOfMidlandFolio.addTag(tagMap.get(sAndSCampaignId).get(KINGDOM_OF_MIDLAND));
		kindomOfMidlandFolio.addUser(user.getId());
		try {
			folioService.save(kindomOfMidlandFolio);
		} catch (Exception e) {
			// ignore;
		}		
		
		Folio kindomOfMidlandOrgFolio = new Folio();
		kindomOfMidlandOrgFolio.setCampaignId(sAndSCampaignId);
		kindomOfMidlandOrgFolio.addOwner(gm.getId());
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
		magicCountyFolio.addOwner(gm.getId());
		magicCountyFolio.setTitle("Magic County");
		magicCountyFolio.setContent("<H1>A magic county in the Kingdom of Midland</H1><p>This is a county in the Kingdom of Midland.</p>");		
		magicCountyFolio.addTag(tagMap.get(sAndSCampaignId).get(ORGANIZATION));
		magicCountyFolio.addTag(tagMap.get(sAndSCampaignId).get(KINGDOM_OF_MIDLAND));
		magicCountyFolio.addTag(tagMap.get(sAndSCampaignId).get(MAGIC_COUNTY));
		try {
			folioService.save(magicCountyFolio);
		} catch (Exception e) {
			// ignore;
		}	

		Folio oakdaleFolio = new Folio();
		oakdaleFolio.setCampaignId(sAndSCampaignId);
		oakdaleFolio.addOwner(gm.getId());
		oakdaleFolio.setTitle("The Town of Oakdale");
		oakdaleFolio.setContent("<H1>The Town of Oakdale</H1><p>This is a town in the Kindom of Midland.</p>");		
		oakdaleFolio.addTag(tagMap.get(sAndSCampaignId).get(OAKDALE));
		oakdaleFolio.addTag(tagMap.get(sAndSCampaignId).get(KINGDOM_OF_MIDLAND));
		oakdaleFolio.addTag(tagMap.get(sAndSCampaignId).get(MAGIC_COUNTY));
		oakdaleFolio.addTag(tagMap.get(sAndSCampaignId).get(TOWN));
		try {
			folioService.save(oakdaleFolio);
		} catch (Exception e) {
			// ignore;
		}	
	
		Folio johnDarteFolio = new Folio();
		johnDarteFolio.setCampaignId(sAndSCampaignId);
		johnDarteFolio.addOwner(gm.getId());
		johnDarteFolio.setTitle("John Darte");
		johnDarteFolio.setContent("<H1>John Darte</H1><p>John Darte is the Mayor of Oakdale.</p>");		
		johnDarteFolio.addTag(tagMap.get(sAndSCampaignId).get(MAYOR));
		johnDarteFolio.addTag(tagMap.get(sAndSCampaignId).get(KINGDOM_OF_MIDLAND));
		johnDarteFolio.addTag(tagMap.get(sAndSCampaignId).get(MAGIC_COUNTY));
		johnDarteFolio.addTag(tagMap.get(sAndSCampaignId).get(OAKDALE));
		try {
			folioService.save(johnDarteFolio);
		} catch (Exception e) {
			// ignore;
		}	
		
		Folio jimBeamFolio = new Folio();
		jimBeamFolio.setCampaignId(sAndSCampaignId);
		jimBeamFolio.addOwner(gm.getId());
		jimBeamFolio.setTitle("Jim Beam");
		jimBeamFolio.setContent("<H1>Jim Beam, Merchant of Oakdale</H1><p>A shopkeeper in Oakdale.</p>");		
		jimBeamFolio.addTag(tagMap.get(sAndSCampaignId).get(MERCHANTS_GUILD));
		jimBeamFolio.addTag(tagMap.get(sAndSCampaignId).get(SHOPKEEPER));
		jimBeamFolio.addTag(tagMap.get(sAndSCampaignId).get(KINGDOM_OF_MIDLAND));
		jimBeamFolio.addTag(tagMap.get(sAndSCampaignId).get(MAGIC_COUNTY));
		jimBeamFolio.addTag(tagMap.get(sAndSCampaignId).get(OAKDALE));
		try {
			folioService.save(jimBeamFolio);
		} catch (Exception e) {
			// ignore;
		}	

		Folio bloodMoonFolio = new Folio();
		bloodMoonFolio.setCampaignId(sAndSCampaignId);
		bloodMoonFolio.addOwner(gm.getId());
		bloodMoonFolio.setTitle("Blood Moon Coven");
		bloodMoonFolio.setContent("<H1>Blood Moon Coven</H1><p>A coven of evil witches and warlocks.</p>");		
		bloodMoonFolio.addTag(tagMap.get(sAndSCampaignId).get(KINGDOM_OF_MIDLAND));
		bloodMoonFolio.addTag(tagMap.get(sAndSCampaignId).get(BLOOD_MOON));
		bloodMoonFolio.addTag(tagMap.get(sAndSCampaignId).get(OAKDALE));
		try {
			folioService.save(bloodMoonFolio);
		} catch (Exception e) {
			// ignore;
		}	
	}
}
