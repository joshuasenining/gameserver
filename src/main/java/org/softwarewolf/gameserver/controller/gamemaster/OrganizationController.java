package org.softwarewolf.gameserver.controller.gamemaster;

import javax.servlet.http.HttpSession;

import org.softwarewolf.gameserver.controller.helper.ControllerHelper;
import org.softwarewolf.gameserver.domain.Organization;
import org.softwarewolf.gameserver.domain.OrganizationType;
import org.softwarewolf.gameserver.domain.SimpleTag;
import org.softwarewolf.gameserver.domain.helper.FeFeedback;
import org.softwarewolf.gameserver.domain.helper.OrganizationCreator;
import org.softwarewolf.gameserver.domain.helper.OrganizationRankCreator;
import org.softwarewolf.gameserver.repository.UserRepository;
import org.softwarewolf.gameserver.service.CampaignService;
import org.softwarewolf.gameserver.service.OrganizationRankService;
import org.softwarewolf.gameserver.service.OrganizationService;
import org.softwarewolf.gameserver.service.OrganizationTypeService;
import org.softwarewolf.gameserver.service.LocationService;
import org.softwarewolf.gameserver.service.SimpleTagService;
import org.softwarewolf.gameserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/gamemaster")
public class OrganizationController {
			
	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	protected CampaignService campaignService;
	
	@Autowired
	protected LocationService locationService;
	
	@Autowired
	protected OrganizationService organizationService;
	
	@Autowired
	public OrganizationTypeService organizationTypeService;
	
	@Autowired
	protected OrganizationRankService organizationRankService;
	
	@Autowired
	protected UserService userService;
	
	@Autowired
	protected SimpleTagService simpleTagService;
	
	private static final String CAMPAIGN_ID = "campaignId";
	
	private String validateOrganization(final Organization organization) {
		StringBuilder errorMsg = new StringBuilder(); 
		if (organization.getName().isEmpty()) {
			addMessage(errorMsg, "You must have a organization name.");
		}
		if (organization.getGameDataTypeId() == null || organization.getGameDataTypeId().isEmpty()) {
			addMessage(errorMsg, "You must have a terrorganizationitory type.");
		}
		if (organization.getDescription().isEmpty()) {
			addMessage(errorMsg, "You must have a description.");
		}
		if (organization.getParentId().isEmpty()) {
			organization.setParentId("ROOT");
		}
		return errorMsg.toString();
	}
	
	private void addMessage(StringBuilder builder, String message) {
		if (builder.length() > 0) {
			builder.append("\n");
		}
		builder.append(message);
	}
	
	@RequestMapping(value = "/editOrganization", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String editOrganization(HttpSession session, final OrganizationCreator organizationCreator, 
			final FeFeedback feFeedback, @RequestParam(value="id", required= false) String organizationId) {
		// If we haven't selected a campaign, get to the menu!
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}
		// organization id 0 = add new organization;
		if ("0".equals(organizationId) || organizationId == null) {
			organizationId = null;
			feFeedback.setUserStatus("You are editing a new organization");
		} else {
			Organization org = organizationService.findOne(organizationId);
			feFeedback.setUserStatus("You are editing " + org.getName());
		}
		organizationService.initOrganizationCreator(organizationId, organizationCreator, campaignId, ControllerHelper.EDIT_ORGANIZATION);
		
		return ControllerHelper.EDIT_ORGANIZATION;
	}

	/**
	 * Ajax call to get just the data on a organization when a user clicks on an organization
	 * in the edit location drop-down
	 * @param session
	 * @param organizationCreator
	 * @param feFeedback
	 * @param organizationId
	 * @return
	 */
	@RequestMapping(value = "/getOrganization", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	@ResponseBody
	public String getOrganization(HttpSession session, final OrganizationCreator organizationCreator, 
			final FeFeedback feFeedback, @RequestParam(value="hiddenOrganizationId", required= true) String organizationId) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}
		
		String out = "{}";
		if (!("0".equals(organizationId))) {
			out = organizationRankService.getOrganizationAndRanks(organizationId);
		}
		return out;
	}
	
	@RequestMapping(value = "/editOrganization", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String postEditOrganization(HttpSession session, final OrganizationCreator organizationCreator, 
			final OrganizationRankCreator organizationRankCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}
		
		Organization organization = organizationCreator.getOrganization();

		String errorMsg = validateOrganization(organization);
		if (errorMsg.length() > 0) {
			feFeedback.setError(errorMsg.toString());
			organizationService.initOrganizationCreator(organization.getId(), organizationCreator, organization.getCampaignId(), organizationCreator.getForwardingUrl());
			return ControllerHelper.EDIT_ORGANIZATION;
		}
		
		try {
			Organization org = organizationService.saveOrganization(organization);
			if (organizationCreator.getCreateOrganizationTag()) {
				SimpleTag organizationTag = new SimpleTag(org.getName(), campaignId);
				simpleTagService.save(organizationTag);
			}
			if (organizationCreator.getCreateOrganizationTypeTag()) {
				String gameDataTypeId = org.getGameDataTypeId();
				if (gameDataTypeId != null) {
					OrganizationType organizationType = organizationTypeService.findOne(gameDataTypeId);
					if (organizationType != null && organizationType.getName() != null) {
						SimpleTag locationTypeTag = new SimpleTag(organizationType.getName(), campaignId);
						simpleTagService.save(locationTypeTag);						
					}
				}
			}
			String organizationId = null;
			organizationService.initOrganizationCreator(organizationId, organizationCreator, campaignId, organizationCreator.getForwardingUrl());
			organizationRankService.initOrganizationRankCreator(organizationId, null, organizationRankCreator, campaignId, organizationCreator.getForwardingUrl());
			feFeedback.setInfo("Success, you've edited " + organization.getName());
			feFeedback.setUserStatus("You are editing a new organization");
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return ControllerHelper.EDIT_ORGANIZATION;
		}
		return organizationCreator.getForwardingUrl();
	}	
}