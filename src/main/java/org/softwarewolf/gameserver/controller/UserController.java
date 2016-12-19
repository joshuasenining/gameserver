package org.softwarewolf.gameserver.controller;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.softwarewolf.gameserver.controller.utils.FeFeedback;
import org.softwarewolf.gameserver.domain.dto.ChangeEmailDto;
import org.softwarewolf.gameserver.domain.dto.ResetPasswordDto;
import org.softwarewolf.gameserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/shared")
public class UserController {
	
	@Autowired
	protected UserService userService;
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER","ADMIN"})
	public String resetPassword(ResetPasswordDto resetPasswordDto, FeFeedback feFeedback) {

		return ControllerUtils.RESET_PASSWORD;
	}
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	@Secured({"USER","GAMEMASTER","ADMIN"})
	public String postResetPassword(ResetPasswordDto resetPasswordDto, FeFeedback feFeedback) {
		try {
			userService.resetPassword(resetPasswordDto);
		} catch (Exception e) {
			feFeedback.setError(e.getMessage());
			return ControllerUtils.RESET_PASSWORD;
		}
		
		feFeedback.setInfo("You have reset your password");
		return ControllerUtils.RESET_PASSWORD;
	}

	@RequestMapping(value = "/adminResetPassword", method = RequestMethod.POST)
	@Secured({"USER","GAMEMASTER","ADMIN"})
	public String postAdminResetPassword(ResetPasswordDto resetPasswordDto, FeFeedback feFeedback) {
		try {
			userService.adminResetPassword(resetPasswordDto);
		} catch (Exception e) {
			feFeedback.setError(e.getMessage());
			return ControllerUtils.RESET_PASSWORD;
		}
		
		feFeedback.setInfo("A temporary password has been assigned to you. Please check your email.");
		return ControllerUtils.RESET_PASSWORD;
	}
	
	@RequestMapping(value = "/changeEmail", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER","ADMIN"})
	public String changeEmail(ChangeEmailDto changeEmailDto, FeFeedback feFeedback) {
		changeEmailDto.setEmail(userService.getCurrentUserEmail());
		return ControllerUtils.CHANGE_EMAIL;
	}
	
	@RequestMapping(value = "/changeEmail", method = RequestMethod.POST)
	@Secured({"USER","GAMEMASTER","ADMIN"})
	public String postChangeEmail(ChangeEmailDto changeEmailDto, FeFeedback feFeedback) {
		try {
			userService.changeEmail(changeEmailDto.getEmail());
		} catch (Exception e) {
			feFeedback.setError(e.getMessage());
			return ControllerUtils.CHANGE_EMAIL;
		}
		
		feFeedback.setInfo("You have changed your email address");
		return ControllerUtils.CHANGE_EMAIL;
	}
}