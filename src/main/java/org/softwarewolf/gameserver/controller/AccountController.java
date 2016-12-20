package org.softwarewolf.gameserver.controller;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.softwarewolf.gameserver.controller.utils.FeFeedback;
import org.softwarewolf.gameserver.domain.dto.UserDto;
import org.softwarewolf.gameserver.repository.UserRepository;
import org.softwarewolf.gameserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/public")
public class AccountController {
	@Autowired
	private UserService userService;
	
	@Autowired
	protected UserRepository userRepository;

	@RequestMapping(value = "/createAccount", method = RequestMethod.GET)
	public String preCreateAccount(UserDto userDto, FeFeedback feFeedback) {
		
		return ControllerUtils.CREATE_ACCOUNT;
	}
	
	@RequestMapping(value = "/createAccount", method = RequestMethod.POST)
	public String createAccount(UserDto userDto, FeFeedback feFeedback) {
		try {
			userService.registerAccount(userDto);
		} catch (RuntimeException e) {
			feFeedback.setError(e.getMessage());
			return ControllerUtils.CREATE_ACCOUNT;
		}
		String message = ControllerUtils.getI18nMessage("createAccount.registered");
		feFeedback.setInfo(message);
		return ControllerUtils.CREATE_ACCOUNT;
	}
}