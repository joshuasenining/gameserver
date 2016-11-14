package org.softwarewolf.gameserver.controller;

import org.softwarewolf.gameserver.controller.helper.FeFeedback;
import org.softwarewolf.gameserver.domain.EmailSetting;
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
	private GameMailService gameMail;
	
	@RequestMapping(value="/seedData", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public String seedDb() {
		dataSeeder.cleanRepos();
		dataSeeder.seedData();

		return "user/menu";
	}

	@RequestMapping(value="/getSettings", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public String changeAppSettings() {
		
		return "admin/settings";
	}

	@RequestMapping(value="/emailSettings", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public String getEmailSettings(final EmailSetting emailSettingsDto, final FeFeedback feFeedback) {
		gameMail.initEmailSettignsDto(emailSettingsDto);
		return "admin/emailSettings";
	}
	
	@RequestMapping(value="/emailSettings", method = RequestMethod.POST)
	@Secured({"ADMIN"})
	public String changeEmailSettings(EmailSetting emailSettingsDto, FeFeedback feFeedback) {
		gameMail.initEmailSettignsDto(emailSettingsDto);
		feFeedback.setInfo("Email Settings Changed");
		return "admin/emailSettings";
	}	
}