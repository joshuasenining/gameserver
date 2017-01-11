package org.softwarewolf.gameserver.controller;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		return ControllerUtils.LOGIN;
	}

	@RequestMapping(value = "/shared/menu", method = RequestMethod.GET)
	public String getMenu() {
		return ControllerUtils.USER_MENU;
	}
	
	@RequestMapping(value = "/gameserver")
	public String test() {
		return ControllerUtils.USER_MENU;
	}
}
