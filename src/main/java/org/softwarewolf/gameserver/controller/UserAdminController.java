package org.softwarewolf.gameserver.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.softwarewolf.gameserver.domain.User;
import org.softwarewolf.gameserver.domain.dto.RoleLists;
import org.softwarewolf.gameserver.domain.dto.RolesData;
import org.softwarewolf.gameserver.domain.dto.UserAdminDto;
import org.softwarewolf.gameserver.domain.dto.UserListItem;
import org.softwarewolf.gameserver.repository.UserRepository;
import org.softwarewolf.gameserver.service.SimpleGrantedAuthorityService;
import org.softwarewolf.gameserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
	protected UserService userService;
	@Autowired
	protected SimpleGrantedAuthorityService sgaService;
	@Autowired
	protected UserRepository userRepository;	
	
	@ModelAttribute("user")
	public User getUser() {
		return new User();
	}
	
	@RequestMapping("/app")
	public String changeAppSettings(ModelMap model) {
		model.addAttribute("msg", "hello world");
 
		return "/admin/settings";
	}
	
	@RequestMapping("/users")
	@Secured({"ADMIN"})
	public String updateUsers() {
		
		return "useradmin.jsp";
	}

	@RequestMapping("/createRole")
	@Secured({"ADMIN"})
	public String getRoles(final RolesData rolesData) {
		userService.setRolesData(rolesData);
		return "/admin/createRole";
	}
	
	@RequestMapping("/deleteRole")
	@Secured({"ADMIN"})
	public String getRolesForDelete(final RolesData rolesData) {
		userService.setRolesData(rolesData);
		return "/admin/deleteRole";
	}

	/**
	 * This method is called when a user is selected. It returns a list of roles 
	 * possessed by the user in question
	 * @param userID
	 * @return
	 */
	@RequestMapping("/rolesJson/{userID}")
	@Secured({"ADMIN"})
	public @ResponseBody String getRolesJson(@PathVariable("userID") String userID) {
		if ("newUser".equals(userID)) {
			userID = null;
		}
		RoleLists roleLists = userService.getRoleLists(userID);
		ObjectMapper objMapper = new ObjectMapper();
		String jsonStr = null;
		try {
			jsonStr = objMapper.writeValueAsString(roleLists);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonStr;
	}

	@RequestMapping(value = "/createRole", method = RequestMethod.POST)
	@Secured({"ADMIN"})
	public String createRole(@ModelAttribute RolesData rolesData) {
		userService.createRoles(rolesData);
		return "/admin/createdRole";
	}

	@RequestMapping(value = "/deleteRole", method = RequestMethod.POST)
	@Secured({"ADMIN"})
	public String postRoles(@ModelAttribute RolesData rolesData) {
		userService.deleteRoles(rolesData);
		return "/admin/deletedRole";
	}

	@RequestMapping("/getUserData")
	@Secured({"ADMIN"})
	public String getUserDataWithId(@RequestParam(required = false) final String userId, final UserAdminDto userAdminDto) {
		List<UserListItem> userList = userService.getUserList();
		userAdminDto.setUserList(userList);
		User user = null;
		if (userId == null) {
			user = new User();
		} else {
			user = userRepository.findOne(userId);
			if (user == null) {
				user = new User();
			}
		}
		userAdminDto.setSelectedUser(user);
		userAdminDto.setAllRoles(userService.getAllRoles());
		return "/admin/updateUser";
	}
	
	@RequestMapping(value = "/getUser/{userID}", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public @ResponseBody String getUser(HttpServletRequest request, @PathVariable("userID") String userID) {
		User user = userService.getUser(userID);
		ObjectMapper objMapper = new ObjectMapper();
		String jsonStr = null;
		try {
			jsonStr = objMapper.writeValueAsString(user);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonStr;
	}

	@RequestMapping(value = "/updateUserData", method = RequestMethod.POST)
	@Secured({"ADMIN"})
	public String postUser(final UserAdminDto userAdminDto) {
		User user = userAdminDto.getSelectedUser();
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		User prevVersion = userRepository.findOne(user.getId());
		String newPassword = userAdminDto.getPassword();
		String verifyPassword = userAdminDto.getVerifyPassword();
		if (prevVersion != null) {
			if (newPassword.isEmpty() && verifyPassword.isEmpty()) {
				String prevEncodedPwd = prevVersion.getPassword();
				user.setPassword(prevEncodedPwd);
			} else if (newPassword.equals(verifyPassword)){
				String encodedPwd = encoder.encode(newPassword);
				user.setPassword(encodedPwd);
			} else {
				userAdminDto.setErrorMessage("Password does not match verify password. Please retry");
				return "/admin/updateUser";
			}
			userRepository.save(user);
		}

		
		return "/admin/updatedUser";
	} 
}
