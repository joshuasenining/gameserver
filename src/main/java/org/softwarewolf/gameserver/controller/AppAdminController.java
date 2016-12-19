package org.softwarewolf.gameserver.controller;

import javax.servlet.http.HttpSession;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.softwarewolf.gameserver.controller.utils.FeFeedback;
import org.softwarewolf.gameserver.domain.EmailSettings;
import org.softwarewolf.gameserver.domain.dto.SelectCampaignDto;
import org.softwarewolf.gameserver.service.CampaignService;
import org.softwarewolf.gameserver.service.DataSeeder;
import org.softwarewolf.gameserver.service.GameMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin")
public class AppAdminController {
	
	@Autowired
	private DataSeeder dataSeeder;
	
	@Autowired
	private GameMailService gameMailService;
	
	@Autowired
	private CampaignService campaignService;
	
	@RequestMapping(value="/seedData", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public String seedDb() {
		dataSeeder.cleanRepos();
		dataSeeder.seedData();
		
		return ControllerUtils.USER_MENU;
	}

	@RequestMapping(value="/getSettings", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public String changeAppSettings() {
		
		return ControllerUtils.SETTINGS;
	}

	@RequestMapping(value="/emailSettings", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public String getEmailSettings(final EmailSettings emailSettings, final FeFeedback feFeedback) {
		gameMailService.initEmailSettings(emailSettings);
		return ControllerUtils.EMAIL_SETTINGS;
	}
	
	@RequestMapping(value="/emailSettings", method = RequestMethod.POST)
	@Secured({"ADMIN"})
	public String changeEmailSettings(EmailSettings emailSettings, FeFeedback feFeedback) {
		gameMailService.updateEmailSettings(emailSettings, feFeedback);

		return ControllerUtils.EMAIL_SETTINGS;
	}	
	
	@RequestMapping(value="/deleteCampaign", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public String deleteCampaign(final SelectCampaignDto selectCampaignDto, final FeFeedback feFeedback) {
		campaignService.initSelectCampaignDto(selectCampaignDto, ControllerUtils.ADMIN_TYPE);

		return ControllerUtils.DELETE_CAMPAIGN;
	}
	
	@RequestMapping(value="/deleteCampaign", method = RequestMethod.POST)
	@Secured({"ADMIN"})
	public String deleteCampaign(HttpSession session, final SelectCampaignDto selectCampaignDto,
			final FeFeedback feFeedback) {
		String campaignId = selectCampaignDto.getSelectedCampaignId();
		
		String campaignName = null;
		try {
			campaignName = campaignService.deleteCampaign(campaignId);
			feFeedback.setInfo("You have deleted campaign '"+campaignName+"'");
		} catch (RuntimeException e) {
			feFeedback.setError(e.getMessage());
		}
		campaignService.initSelectCampaignDto(selectCampaignDto, ControllerUtils.ADMIN_TYPE);
		
		return ControllerUtils.DELETE_CAMPAIGN;
	}	
}