package org.softwarewolf.gameserver.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.softwarewolf.gameserver.controller.utils.FeFeedback;
import org.softwarewolf.gameserver.domain.User;
import org.softwarewolf.gameserver.domain.dto.RolesData;
import org.softwarewolf.gameserver.domain.dto.UserAdminDto;
import org.softwarewolf.gameserver.service.SimpleGrantedAuthorityService;
import org.softwarewolf.gameserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/admin")
public class UserAdminController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private SimpleGrantedAuthorityService sgaService;
	
	@ModelAttribute("user")
	public User getUser() {
		return new User();
	}

	@RequestMapping("/createRole")
	@Secured({"ADMIN"})
	public String getRoles(final RolesData rolesData, FeFeedback feFeedback) {
		userService.setRolesData(rolesData);
		return ControllerUtils.CREATE_ROLE;
	}

	@RequestMapping(value = "/createRole", method = RequestMethod.POST)
	@Secured({"ADMIN"})
	public String createRole(@ModelAttribute RolesData rolesData, FeFeedback feFeedback) {
		userService.createRoles(rolesData);
		String message = ControllerUtils.getI18nMessage("createRoles.success");
		feFeedback.setInfo(message);
		userService.setRolesData(rolesData);
		return ControllerUtils.CREATE_ROLE;
	}
	
	@RequestMapping("/deleteRole")
	@Secured({"ADMIN"})
	public String getRolesForDelete(final RolesData rolesData, FeFeedback feFeedback) {
		userService.setRolesData(rolesData);
		return ControllerUtils.DELETE_ROLE;
	}

	@RequestMapping(value = "/deleteRole", method = RequestMethod.POST)
	@Secured({"ADMIN"})
	public String postRoles(@ModelAttribute RolesData rolesData, FeFeedback feFeeback) {
		userService.deleteRoles(rolesData);
		String message = ControllerUtils.getI18nMessage("deleteRoles.success");
		feFeeback.setInfo(message);
		userService.setRolesData(rolesData);
		return ControllerUtils.DELETE_ROLE;
	}
	
	/**
	 * This method is called when a user is selected. It returns a list of roles 
	 * possessed by the user in question
	 * @param userID
	 * @return
	 */
	@RequestMapping("/rolesJson/{userId}")
	@Secured({"ADMIN"})
	public @ResponseBody String getRolesJson(@PathVariable("userId") String userId) {
		return userService.getRoleListsAsJson(userId);
	}

	@RequestMapping("/getUserData")
	@Secured({"ADMIN"})
	public String getUserDataWithId(@RequestParam(required = false) final String userId, 
			final UserAdminDto userAdminDto, FeFeedback feFeedback) {
		userService.initUserAdminDto(userAdminDto, userId);
		
		return ControllerUtils.EDIT_USER;
	}
	
	@RequestMapping(value = "/getUser/{userID}", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public @ResponseBody String getUser(HttpServletRequest request, 
			@PathVariable("userID") String userID, FeFeedback feFeedback) {
		User user = userService.getUser(userID);
		ObjectMapper objMapper = new ObjectMapper();
		String jsonStr = null;
		try {
			jsonStr = objMapper.writeValueAsString(user);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}

	@RequestMapping(value = "/editUserData", method = RequestMethod.POST)
	@Secured({"ADMIN"})
	public String postUser(final UserAdminDto userAdminDto, FeFeedback feFeedback) {
		try {
			userService.editUserData(userAdminDto);
		} catch (Exception e) {
			feFeedback.setError(e.getMessage());
			return ControllerUtils.EDIT_USER;
		}
		userService.initUserAdminDto(userAdminDto, userAdminDto.getSelectedUserId());
		String message = ControllerUtils.getI18nMessage("editUser.success");
		feFeedback.setInfo(message);
		
		return ControllerUtils.EDIT_USER;
	} 
}
