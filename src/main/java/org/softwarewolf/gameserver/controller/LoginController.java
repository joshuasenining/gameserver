package org.softwarewolf.gameserver.controller;

import org.softwarewolf.gameserver.controller.helper.ControllerHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		return ControllerHelper.LOGIN;
	}

	@RequestMapping(value = "/user/menu", method = RequestMethod.GET)
	public String getMenu() {
		return ControllerHelper.USER_MENU;
	}
	
	@RequestMapping(value = "/gameserver")
	public String test() {
		return ControllerHelper.USER_MENU;
	}
}
