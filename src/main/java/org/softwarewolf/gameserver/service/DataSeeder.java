package org.softwarewolf.gameserver.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.softwarewolf.gameserver.domain.Campaign;
import org.softwarewolf.gameserver.domain.Organization;
import org.softwarewolf.gameserver.domain.OrganizationRank;
import org.softwarewolf.gameserver.domain.OrganizationType;
import org.softwarewolf.gameserver.domain.Folio;
import org.softwarewolf.gameserver.domain.Location;
import org.softwarewolf.gameserver.domain.LocationType;
import org.softwarewolf.gameserver.domain.SimpleTag;
import org.softwarewolf.gameserver.domain.User;
import org.softwarewolf.gameserver.repository.CampaignRepository;
import org.softwarewolf.gameserver.repository.OrganizationRankRepository;
import org.softwarewolf.gameserver.repository.OrganizationRepository;
import org.softwarewolf.gameserver.repository.OrganizationTypeRepository;
import org.softwarewolf.gameserver.repository.SimpleGrantedAuthorityRepository;
import org.softwarewolf.gameserver.repository.LocationRepository;
import org.softwarewolf.gameserver.repository.LocationTypeRepository;
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
	private static final String MAGIC_KINGDOM = "Magic Kingdom";
	private static final String MAGIC_COUNTY = "Magic County";
	private static final String MAGIC_CITY = "Magic City";
	private static final String MAGIC_TOWN = "Magic Town";
	private static final String MODERN_KINGDOM = "Modern Kingdom";
	private static final String MODERN_COUNTY = "Modern County";
	private static final String MODERN_CITY = "Modern City";
	private static final String MODERN_SPACE_STATION = "Modern Space Station";
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
	
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private SimpleGrantedAuthorityRepository sgaRepo;
	
	@Autowired 
	private CampaignRepository campaignRepo;
	
	@Autowired 
	private LocationTypeRepository locationTypeRepo;
	
	@Autowired 
	private LocationRepository locationRepo;
	
	@Autowired
	private LocationService locationService;
	
	@Autowired 
	private OrganizationTypeRepository organizationTypeRepo;
	
	@Autowired 
	private OrganizationRepository organizationRepo;
	
	@Autowired 
	private OrganizationRankRepository organizationRankRepo;
	
	@Autowired
	private SimpleTagService simpleTagService;
	
	@Autowired
	private FolioService folioService;
	
	public void cleanRepos() {
		sgaRepo.deleteAll();
		userRepo.deleteAll();
		campaignRepo.deleteAll();
		locationRepo.deleteAll();
		locationTypeRepo.deleteAll();
		organizationRepo.deleteAll();
		organizationTypeRepo.deleteAll();
		organizationRankRepo.deleteAll();
		folioService.deleteAll();
		simpleTagService.deleteAll();
	}
	
	public void seedData() {
		Map<String, SimpleGrantedAuthority> roleMap = seedRoles();
		Map<String, User> userMap = seedUsers(roleMap);
		Map<String, Campaign> campaignMap = seedCampaign(userMap);
		Map<String, LocationType> locationTypeMap = seedLocationType(campaignMap);
		Map<String, Location> locationMap = seedLocations(campaignMap, locationTypeMap);
		Map<String, OrganizationType> organizationTypeMap = seedOrganizationType(campaignMap);
		Map<String, Organization> organizationMap = seedOrganizations(campaignMap, organizationTypeMap);
		seedOrganizationRanks(campaignMap, organizationMap);
		seedFolios(organizationMap, locationMap, campaignMap);
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
	
	private Map<String, LocationType> seedLocationType(Map<String, Campaign> campaignMap) {
		Map<String, LocationType> locationTypeMap = new HashMap<>();

		Boolean createTag = Boolean.TRUE;
		
		saveLocationType(KINGDOM, "The lands belonging to a kingdom.", campaignMap.get(SWORD_AND_SORCERY).getId(), 
				campaignMap.get(SWORD_AND_SORCERY).getName(), locationTypeMap, createTag);
		saveLocationType(COUNTY, "A subdivision of a kingdom.", campaignMap.get(SWORD_AND_SORCERY).getId(), 
				campaignMap.get(SWORD_AND_SORCERY).getName(), locationTypeMap, createTag);
		saveLocationType(CITY, "A fairly densely populated area.", campaignMap.get(SWORD_AND_SORCERY).getId(), 
				campaignMap.get(SWORD_AND_SORCERY).getName(), locationTypeMap, createTag);
		saveLocationType(TOWN, "Similar to a city. A somewhat populated area.", campaignMap.get(SWORD_AND_SORCERY).getId(), 
				campaignMap.get(SWORD_AND_SORCERY).getName(), locationTypeMap, createTag);
		saveLocationType(VILLAGE, "Similar to a town. A lightly populated area.", campaignMap.get(SWORD_AND_SORCERY).getId(), 
				campaignMap.get(SWORD_AND_SORCERY).getName(), locationTypeMap, createTag);

		saveLocationType(KINGDOM, "The lands beloning to a kingdom.", campaignMap.get(MODERN).getId(), 
				campaignMap.get(MODERN).getName(), locationTypeMap, createTag);
		saveLocationType(COUNTY, "A subdivision of a kingdom.", campaignMap.get(MODERN).getId(), 
				campaignMap.get(MODERN).getName(), locationTypeMap, createTag);
		saveLocationType(CITY, "A fairly densely populated area.", campaignMap.get(MODERN).getId(), 
				campaignMap.get(MODERN).getName(), locationTypeMap, createTag);
		saveLocationType(TOWN, "Similar to a city. A somewhat populated area.", campaignMap.get(MODERN).getId(), 
				campaignMap.get(MODERN).getName(), locationTypeMap, createTag);
		saveLocationType(MODERN_SPACE_STATION, "A life sustaining habitat in space.", campaignMap.get(MODERN).getId(), 
				campaignMap.get(MODERN).getName(), locationTypeMap, createTag);

		saveLocationType(SPACE_STATION, "A life sustaining habitat in space.", campaignMap.get(SPACE_OPERA).getId(), 
				campaignMap.get(SPACE_OPERA).getName(), locationTypeMap, createTag);
		
		return locationTypeMap;
	}
	
	private void saveLocationType(String locTypeName, String description, String campaignId, String campaignName,
			Map<String, LocationType> locationTypeMap, Boolean createLocationTypeTag) {
		LocationType locationType = locationTypeRepo.findOneByNameAndCampaignId(locTypeName, campaignId);
		if (locationType == null) {
			locationType = new LocationType();
			locationType.setName(locTypeName);
			locationType.setDescription(description);
			locationType.setCampaignId(campaignId);
			locationType = locationTypeRepo.save(locationType);
			locationTypeMap.put(campaignName+locTypeName, locationType);
			if (createLocationTypeTag) {
				SimpleTag simpleTag = new SimpleTag(locationType.getName(), campaignId);
				simpleTagService.save(simpleTag);
			}
		}
	}
	
	private Map<String, Location> seedLocations(Map<String, Campaign> campaignMap, Map<String, LocationType> locationTypeMap) {
		locationRepo.deleteAll();
		
		Boolean createTag = Boolean.TRUE;
		Map<String, Location> locationMap = new HashMap<>();
		String sAndSCampaignId = campaignMap.get(SWORD_AND_SORCERY).getId();
		String sAndSCampaignName = campaignMap.get(SWORD_AND_SORCERY).getName();
		Location magicKingdom = findAndSave(MAGIC_KINGDOM, sAndSCampaignId, locationTypeMap.get(sAndSCampaignName+KINGDOM).getId(), 
				locationTypeMap.get(sAndSCampaignName+KINGDOM).getName(), campaignMap, "A magic kingdom", null, createTag);
		locationMap.put(sAndSCampaignName+magicKingdom.getName(), magicKingdom);
		
		Location magicCounty = findAndSave(MAGIC_COUNTY, sAndSCampaignId, locationTypeMap.get(sAndSCampaignName+COUNTY).getId(),
				locationTypeMap.get(sAndSCampaignName+COUNTY).getName(), campaignMap, "A magic county", magicKingdom, createTag);
		locationMap.put(sAndSCampaignName+magicCounty.getName(), magicCounty);
		
		Location magicCity = findAndSave(MAGIC_CITY, sAndSCampaignId, locationTypeMap.get(sAndSCampaignName+CITY).getId(), 
				locationTypeMap.get(sAndSCampaignName+CITY).getName(), campaignMap, "A magic city", magicCounty, createTag);
		locationMap.put(sAndSCampaignName+magicCity.getName(), magicCity);
		
		Location magicTown = findAndSave(MAGIC_TOWN, sAndSCampaignId, locationTypeMap.get(sAndSCampaignName+TOWN).getId(), 
				locationTypeMap.get(sAndSCampaignName+TOWN).getName(), campaignMap, "A magic city", magicCounty, createTag);
		locationMap.put(sAndSCampaignName+magicTown.getName(), magicTown);
		
		Location rivalKingdom = findAndSave(RIVAL_KINGDOM, sAndSCampaignId, locationTypeMap.get(sAndSCampaignName+KINGDOM).getId(), 
				locationTypeMap.get(sAndSCampaignName+KINGDOM).getName(), campaignMap, "A rival kingdom", null, createTag);
		locationMap.put(sAndSCampaignName+rivalKingdom.getName(), rivalKingdom);
		
		/* MODERN */
		String modernCampaignId = campaignMap.get(MODERN).getId();
		String modernCampaignName = campaignMap.get(MODERN).getName();
		Location modernKingdom = findAndSave(MODERN_KINGDOM, modernCampaignId, locationTypeMap.get(modernCampaignName+KINGDOM).getId(), 
				locationTypeMap.get(modernCampaignName+KINGDOM).getName(), campaignMap, "A modern kingdom", null, createTag);
		locationMap.put(modernCampaignName+modernKingdom.getName(), rivalKingdom);

		Location modernCounty = findAndSave(MODERN_COUNTY, modernCampaignId, locationTypeMap.get(modernCampaignName+COUNTY).getId(), 
				locationTypeMap.get(modernCampaignName+COUNTY).getName(), campaignMap, "A modern county", modernKingdom, createTag);
		locationMap.put(modernCampaignName+modernCounty.getName(), rivalKingdom);

		Location modernCity = findAndSave(MODERN_CITY, modernCampaignId, locationTypeMap.get(modernCampaignName+CITY).getId(), 
				locationTypeMap.get(modernCampaignName+CITY).getName(), campaignMap, "A modern city", modernCounty, createTag);
		locationMap.put(modernCampaignName+modernCity.getName(), rivalKingdom);

		Location modernSpaceStation = findAndSave(MODERN_SPACE_STATION, modernCampaignId, locationTypeMap.get(modernCampaignName+MODERN_SPACE_STATION).getId(), 
				locationTypeMap.get(modernCampaignName+MODERN_SPACE_STATION).getName(), campaignMap, "A modern space station", null, createTag);
		locationMap.put(modernCampaignName+modernSpaceStation.getName(), rivalKingdom);

		/* SPACE OPERA */
		String spaceOperaCampaignId = campaignMap.get(SPACE_OPERA).getId();
		String spaceOperaCampaignName = campaignMap.get(SPACE_OPERA).getName();
		Location spaceStation = findAndSave(SPACE_STATION, spaceOperaCampaignId, locationTypeMap.get(spaceOperaCampaignName+SPACE_STATION).getId(),
				locationTypeMap.get(spaceOperaCampaignName+SPACE_STATION).getName(), campaignMap, "A space station", null, createTag);
		locationMap.put(spaceOperaCampaignName+spaceStation.getName(), rivalKingdom);
		
		return locationMap;
	}
	
	private Location findAndSave(String name, String campaignId, String locationTypeId, String locationTypeName, 
			Map<String, Campaign> campaignMap, String description, Location parent, Boolean createTag) {
		Location location = locationRepo.findOneByNameAndCampaignId(name, campaignId);
		if (location == null) {
			location = new Location(name, campaignId);
			location.setDescription(description);
			location.setGameDataTypeId(locationTypeId);
			location.setGameDataTypeName(locationTypeName);
			// Need to get an id on location so all the parent/child links can be set
			String parentId = null;
			if (parent != null) {
				parentId = parent.getId();
			}
			location.setParentId(parentId);
			location = locationRepo.save(location);
			if (parent != null) {
				parent.addChildId(location.getId());
				parent = locationRepo.save(parent);
			}
			if (createTag) {
				SimpleTag simpleTag = new SimpleTag(location.getName(), campaignId);
				simpleTagService.save(simpleTag);
			}
		}
		return location;
	}
	
	private Map<String, OrganizationType> seedOrganizationType(Map<String, Campaign> campaignMap) {
		Map<String, OrganizationType> organizationTypeMap = new HashMap<>();
		Boolean createTag = Boolean.TRUE;
		
		saveOrganizationType(KINGDOM, "A typical kingdom.", campaignMap.get(SWORD_AND_SORCERY).getId(), 
				campaignMap.get(SWORD_AND_SORCERY).getName(), organizationTypeMap, createTag);
		saveOrganizationType(COUNTY, "A subdivision of a kingdom.", campaignMap.get(SWORD_AND_SORCERY).getId(), 
				campaignMap.get(SWORD_AND_SORCERY).getName(), organizationTypeMap, createTag);
		saveOrganizationType(CITY, "A collection of people and dwellings.", campaignMap.get(SWORD_AND_SORCERY).getId(), 
				campaignMap.get(SWORD_AND_SORCERY).getName(), organizationTypeMap, createTag);
		saveOrganizationType(TOWN, "Like a city but smaller.", campaignMap.get(SWORD_AND_SORCERY).getId(), 
				campaignMap.get(SWORD_AND_SORCERY).getName(), organizationTypeMap, createTag);
		saveOrganizationType(MERCHANTS_GUILD, "Mercantile guild.", campaignMap.get(SWORD_AND_SORCERY).getId(), 
				campaignMap.get(SWORD_AND_SORCERY).getName(), organizationTypeMap, createTag);
		saveOrganizationType(COVEN, "A witches coven.", campaignMap.get(SWORD_AND_SORCERY).getId(), 
				campaignMap.get(SWORD_AND_SORCERY).getName(), organizationTypeMap, createTag);

		saveOrganizationType(KINGDOM, "A typical kingdom.", campaignMap.get(MODERN).getId(), 
				campaignMap.get(MODERN).getName(), organizationTypeMap, createTag);
		saveOrganizationType(COUNTY, "A subdivision of a kingdom.", campaignMap.get(MODERN).getId(), 
				campaignMap.get(MODERN).getName(), organizationTypeMap, createTag);
		saveOrganizationType(CITY, "A collection of people and dwellings.", campaignMap.get(MODERN).getId(), 
				campaignMap.get(MODERN).getName(), organizationTypeMap, createTag);
		saveOrganizationType(TOWN, "Like a city but smaller.", campaignMap.get(MODERN).getId(), 
				campaignMap.get(MODERN).getName(), organizationTypeMap, createTag);
		saveOrganizationType(MODERN_SPACE_STATION, "A typical space station.", campaignMap.get(MODERN).getId(), 
				campaignMap.get(MODERN).getName(), organizationTypeMap, createTag);
		
		saveOrganizationType(SPACE_STATION, "A typical space station.", campaignMap.get(SPACE_OPERA).getId(), 
				campaignMap.get(SPACE_OPERA).getName(), organizationTypeMap, createTag);
		
		return organizationTypeMap;
	}
	
	private void saveOrganizationType(String orgName, String description, String campaignId, 
			String campaignName, Map<String, OrganizationType> organizationTypeMap, Boolean createTag) {
		OrganizationType organizationType = organizationTypeRepo.findOneByNameAndCampaignId(orgName, campaignId);
		if (organizationType == null) {
			organizationType = new OrganizationType();
			organizationType.setName(orgName);
			organizationType.setDescription(description);
			organizationType.setCampaignId(campaignId);
			organizationType = organizationTypeRepo.save(organizationType);
			organizationTypeMap.put(campaignName+orgName, organizationType);
			if (createTag) {
				SimpleTag simpleTag = new SimpleTag(organizationType.getName(), campaignId);
				simpleTagService.save(simpleTag);
			}
		}
	}
	
	private Map<String, Organization> seedOrganizations(Map<String, Campaign> campaignMap, Map<String, OrganizationType> organizationTypeMap) {
		organizationRepo.deleteAll();
		
		Map<String, Organization> orgMap = new HashMap<>();
		Boolean createTag = Boolean.TRUE;
		
		String sAndSCampaignId = campaignMap.get(SWORD_AND_SORCERY).getId();
		String sAndSCampaignName = campaignMap.get(SWORD_AND_SORCERY).getName();
		Organization kingdom = findAndSaveOrganization(KINGDOM_OF_MIDLAND, sAndSCampaignId, sAndSCampaignName,
				organizationTypeMap.get(sAndSCampaignName+KINGDOM).getId(), 
				organizationTypeMap.get(sAndSCampaignName+KINGDOM).getName(), campaignMap, "A kingdom", null, orgMap, createTag);

		Organization county = findAndSaveOrganization("Kirkwall " + COUNTY, sAndSCampaignId, sAndSCampaignName, 
				organizationTypeMap.get(sAndSCampaignName+COUNTY).getId(),
				organizationTypeMap.get(sAndSCampaignName+COUNTY).getName(), campaignMap, "A county", kingdom, orgMap, createTag);

		findAndSaveOrganization("Morningstar", sAndSCampaignId, sAndSCampaignName,
				organizationTypeMap.get(sAndSCampaignName+CITY).getId(), 
				organizationTypeMap.get(sAndSCampaignName+CITY).getName(), campaignMap, "A city", county, orgMap, createTag);

		findAndSaveOrganization("Markham", sAndSCampaignId, sAndSCampaignName,
				organizationTypeMap.get(sAndSCampaignName+TOWN).getId(), 
				organizationTypeMap.get(sAndSCampaignName+TOWN).getName(), campaignMap, "A city", county, orgMap, createTag);

		findAndSaveOrganization(GOLDEN_ROAD, sAndSCampaignId, sAndSCampaignName,
				organizationTypeMap.get(sAndSCampaignName+MERCHANTS_GUILD).getId(), 
				organizationTypeMap.get(sAndSCampaignName+MERCHANTS_GUILD).getName(), campaignMap, "A merchants guild", null, orgMap, createTag);

		findAndSaveOrganization(BLOOD_MOON, sAndSCampaignId, sAndSCampaignName,
				organizationTypeMap.get(sAndSCampaignName+COVEN).getId(), 
				organizationTypeMap.get(sAndSCampaignName+COVEN).getName(), campaignMap, "A witches coven", null, orgMap, createTag);

		/* MODERN */
		String modernCampaignId = campaignMap.get(MODERN).getId();
		String modernCampaignName = campaignMap.get(MODERN).getName();
		Organization modernKingdom = findAndSaveOrganization("Kalibah", modernCampaignId, modernCampaignName,
				organizationTypeMap.get(modernCampaignName+KINGDOM).getId(), 
				organizationTypeMap.get(modernCampaignName+KINGDOM).getName(), campaignMap, "A modern kingdom", null, orgMap, createTag);

		Organization modernCounty = findAndSaveOrganization("Kent", modernCampaignId, modernCampaignName, 
				organizationTypeMap.get(modernCampaignName+COUNTY).getId(), 
				organizationTypeMap.get(modernCampaignName+COUNTY).getName(), campaignMap, "A modern county", modernKingdom, orgMap, createTag);

		findAndSaveOrganization("York", modernCampaignId, 
				organizationTypeMap.get(modernCampaignName+CITY).getId(), modernCampaignName, 
				organizationTypeMap.get(modernCampaignName+CITY).getName(), campaignMap, "A modern city", modernCounty, orgMap, createTag);

		findAndSaveOrganization("Space Station Group", modernCampaignId, modernCampaignName, 
				organizationTypeMap.get(modernCampaignName+MODERN_SPACE_STATION).getId(), 
				organizationTypeMap.get(modernCampaignName+MODERN_SPACE_STATION).getName(), campaignMap, "A modern space station", null, orgMap, createTag);

		/* SPACE OPERA */
		String spaceOperaCampaignId = campaignMap.get(SPACE_OPERA).getId();
		String spaceOperaCampaignName = campaignMap.get(SPACE_OPERA).getName();
		findAndSaveOrganization(SPACE_STATION + " Omega", spaceOperaCampaignId, spaceOperaCampaignName,
				organizationTypeMap.get(spaceOperaCampaignName+SPACE_STATION).getId(),
				organizationTypeMap.get(spaceOperaCampaignName+SPACE_STATION).getName(), campaignMap, "A space station", null, orgMap, createTag);
		
		return orgMap;
	}
	
	private void seedOrganizationRanks(Map<String, Campaign> campaignMap, Map<String, Organization> organizationMap) {
		String sAndSCampaignId = campaignMap.get(SWORD_AND_SORCERY).getId();
		String sAndSCampaignName = campaignMap.get(SWORD_AND_SORCERY).getName();
		Organization org = organizationMap.get(sAndSCampaignName+KINGDOM_OF_MIDLAND);
		findAndSaveOrganizationRank("King", sAndSCampaignId, org.getId(),  
				campaignMap, "The ruler of the kingdom", null);
		
		String modernCampaignId = campaignMap.get(MODERN).getId();
		String modernCampaignName = campaignMap.get(MODERN).getName();
		org = organizationMap.get(modernCampaignName+"Kent");
		findAndSaveOrganizationRank("Sheriff of Kent", modernCampaignId, org.getId(), 
				campaignMap, "The local sheriff", null);

		String spaceOperaCampaignId = campaignMap.get(SPACE_OPERA).getId();
		String spaceOperaCampaignName = campaignMap.get(SPACE_OPERA).getName();
		org = organizationMap.get(spaceOperaCampaignName+SPACE_STATION + " Omega");
		findAndSaveOrganizationRank("Space Station Commander", spaceOperaCampaignId, org.getId(), 
				campaignMap, "The commander of the space station", null);
	}
	
	private Organization findAndSaveOrganization(String name, String campaignId, String campaignName, String organizationTypeId, 
			String organizationTypeName, Map<String, Campaign> campaignMap, String description, Organization parent, 
			Map<String, Organization> orgMap, Boolean createTag) {
		Organization organization = organizationRepo.findOneByNameAndCampaignId(name, campaignId);
		if (organization == null) {
			organization = new Organization(name, campaignId);
			organization.setDescription(description);
			organization.setGameDataTypeId(organizationTypeId);
			organization.setGameDataTypeName(organizationTypeName);
			// Need to get an id on location so all the parent/child links can be set
			String parentId = null;
			if (parent != null) {
				parentId = parent.getId();
			}
			organization.setParentId(parentId);
			organization = organizationRepo.save(organization);
			orgMap.put(campaignName+organization.getName(), organization);
			if (parent != null) {
				parent.addChildId(organization.getId());
				parent = organizationRepo.save(parent);
			}
			if (createTag) {
				SimpleTag simpleTag = new SimpleTag(organization.getName(), campaignId);
				simpleTagService.save(simpleTag);
			}
		}
		return organization;
	}	

	private OrganizationRank findAndSaveOrganizationRank(String name, String campaignId, String organizationId, 
			Map<String, Campaign> campaignMap, String description, Organization parent) {
		OrganizationRank organizationRank = organizationRankRepo.findOneByNameAndOrganizationId(name, campaignId);
		if (organizationRank == null) {
			organizationRank = new OrganizationRank(name, campaignId, organizationId);
			organizationRank.setDescription(description);
			// Need to get an id on location so all the parent/child links can be set
			String parentId = null;
			if (parent != null) {
				parentId = parent.getId();
			}
			organizationRank.setParentId(parentId);
			organizationRank = organizationRankRepo.save(organizationRank);
			if (parent != null) {
				parent.addChildId(organizationRank.getId());
				parent = organizationRepo.save(parent);
			}
		}
		return organizationRank;
	}	

	private void seedFolios(Map<String, Organization> organizationMap, Map<String, Location> locationMap, 
			Map<String, Campaign> campaignMap) {
		String sAndSCampaignName = campaignMap.get(SWORD_AND_SORCERY).getName();
		Folio goldenRoadPage = new Folio();
		Organization goldenRoad = organizationMap.get(sAndSCampaignName+GOLDEN_ROAD);		
		goldenRoadPage.setCampaignId(goldenRoad.getCampaignId());

		goldenRoadPage.setTitle("Golden Road Trading League Intro");
		goldenRoadPage.setContent("<H1>The Golden Road Trading League</H1><p>This is a big merchant guild</p>");
		
		SimpleTag merchantsGuildTag = simpleTagService.findOneByNameAndCampaignId(MERCHANTS_GUILD, goldenRoad.getCampaignId());
		goldenRoadPage.addTag(merchantsGuildTag);
		SimpleTag goldenRoadTag = simpleTagService.findOneByNameAndCampaignId(GOLDEN_ROAD, goldenRoad.getCampaignId());
		goldenRoadPage.addTag(goldenRoadTag);
		SimpleTag midlandTag = simpleTagService.findOneByNameAndCampaignId(KINGDOM_OF_MIDLAND, goldenRoad.getCampaignId());
		goldenRoadPage.addTag(midlandTag);		
		try {
			folioService.save(goldenRoadPage);
		} catch (Exception e) {
			// ignore;
		}
	}
}
