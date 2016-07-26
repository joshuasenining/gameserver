package org.softwarewolf.gameserver.controller.gamemaster;

import javax.servlet.http.HttpSession;

import org.softwarewolf.gameserver.controller.helper.ControllerHelper;
import org.softwarewolf.gameserver.domain.helper.FeFeedback;
import org.softwarewolf.gameserver.domain.helper.OrganizationCreator;
import org.softwarewolf.gameserver.domain.helper.OrganizationTypeCreator;
import org.softwarewolf.gameserver.domain.helper.LocationCreator;
import org.softwarewolf.gameserver.domain.helper.LocationTypeCreator;
import org.softwarewolf.gameserver.repository.UserRepository;
import org.softwarewolf.gameserver.service.CampaignService;
import org.softwarewolf.gameserver.service.OrganizationRankService;
import org.softwarewolf.gameserver.service.OrganizationService;
import org.softwarewolf.gameserver.service.OrganizationTypeService;
import org.softwarewolf.gameserver.service.FolioService;
import org.softwarewolf.gameserver.service.LocationService;
import org.softwarewolf.gameserver.service.LocationTypeService;
import org.softwarewolf.gameserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/gamemaster")
public class GamemasterController {
			
	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	protected CampaignService campaignService;
	
	@Autowired
	protected LocationService locationService;
	
	@Autowired
	protected LocationTypeService locationTypeService;
	
	@Autowired
	protected OrganizationService organizationService;
	
	@Autowired
	public OrganizationTypeService organizationTypeService;
	
	@Autowired
	protected OrganizationRankService organizationRankService;
	
	@Autowired
	protected UserService userService;
	
	@Autowired
	protected FolioService folioService;
	
	@RequestMapping(value = "/campaignHome", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String campaignHome(HttpSession session, LocationCreator locationCreator, 
			LocationTypeCreator locationTypeCreator, final OrganizationCreator organizationCreator,
			final OrganizationTypeCreator organizationTypeCreator, FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}
		String locationId = null;
		locationService.initLocationCreator(locationId, locationCreator, campaignId, ControllerHelper.CAMPAIGN_HOME);
		locationTypeService.initLocationTypeCreator(null, locationTypeCreator, campaignId, ControllerHelper.CAMPAIGN_HOME);

		String organizationId = null;
		organizationService.initOrganizationCreator(organizationId, organizationCreator, campaignId, ControllerHelper.CAMPAIGN_HOME);
		organizationTypeService.initOrganizationTypeCreator(null, organizationTypeCreator, campaignId,ControllerHelper. CAMPAIGN_HOME);

		return ControllerHelper.CAMPAIGN_HOME;
	}

}