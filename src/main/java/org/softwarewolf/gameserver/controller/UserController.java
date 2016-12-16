package org.softwarewolf.gameserver.controller;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.softwarewolf.gameserver.controller.utils.FeFeedback;
import org.softwarewolf.gameserver.domain.dto.ResetPasswordDto;
import org.softwarewolf.gameserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/shared")
public class UserController {
	
	@Autowired
	protected UserRepository userRepository;
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
	public String resetPassword(ResetPasswordDto resetPasswordDto, FeFeedback feFeedback) {
 
		return ControllerUtils.RESET_PASSWORD;
	}
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	public String postResetPassword(ResetPasswordDto resetPasswordDto, FeFeedback feFeedback) {
 
		return ControllerUtils.RESET_PASSWORD;
	}
}