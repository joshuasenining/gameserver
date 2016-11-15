package org.softwarewolf.gameserver.controller;

import org.softwarewolf.gameserver.controller.helper.FeFeedback;
import org.softwarewolf.gameserver.domain.EmailSettings;
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
	public String getEmailSettings(final EmailSettings emailSettings, final FeFeedback feFeedback) {
		gameMailService.initEmailSettings(emailSettings);
		return "admin/emailSettings";
	}
	
	@RequestMapping(value="/emailSettings", method = RequestMethod.POST)
	@Secured({"ADMIN"})
	public String changeEmailSettings(EmailSettings emailSettings, FeFeedback feFeedback) {
		gameMailService.updateEmailSettings(emailSettings, feFeedback);

		return "admin/emailSettings";
	}	
}