package org.softwarewolf.gameserver.controller;

import org.softwarewolf.gameserver.controller.helper.ControllerHelper;
import org.softwarewolf.gameserver.service.GameMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/test")
public class TestController {
	@Autowired
	private GameMailService gameMail;
	
	@RequestMapping(value = "/testMail", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER"})
	public String testMail() {
		gameMail.testMail();
		return ControllerHelper.CAMPAIGN_HOME;
	}
}
