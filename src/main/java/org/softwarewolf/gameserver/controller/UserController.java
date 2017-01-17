package org.softwarewolf.gameserver.controller;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.softwarewolf.gameserver.controller.utils.FeFeedback;
import org.softwarewolf.gameserver.domain.dto.MyAccountDto;
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
		
		String message = ControllerUtils.getI18nMessage("myAccount.resetPassword.success");
		feFeedback.setInfo(message);
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
		
		String message = ControllerUtils.getI18nMessage("myAccount.adminResetPassword.success");
		feFeedback.setInfo(message);
		return ControllerUtils.RESET_PASSWORD;
	}
	
	@RequestMapping(value = "/myAccount", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER","ADMIN"})
	public String editMyAccount(MyAccountDto myAccountDto, FeFeedback feFeedback) {
		userService.initMyAccountDto(myAccountDto);
		return ControllerUtils.EDIT_MY_ACCOUNT;
	}
	
	@RequestMapping(value = "/myAccount", method = RequestMethod.POST)
	@Secured({"USER","GAMEMASTER","ADMIN"})
	public String postMyAccount(MyAccountDto myAccountDto, FeFeedback feFeedback) {
		try {
			userService.editMyAccount(myAccountDto);
		} catch (Exception e) {
			feFeedback.setError(e.getMessage());
			return ControllerUtils.EDIT_MY_ACCOUNT;
		}
		
		String message = ControllerUtils.getI18nMessage("myAccount.success");
		feFeedback.setInfo(message);
		return ControllerUtils.EDIT_MY_ACCOUNT;
	}
}