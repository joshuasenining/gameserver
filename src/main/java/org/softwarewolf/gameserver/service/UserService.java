package org.softwarewolf.gameserver.service;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.softwarewolf.gameserver.domain.DeleteableRole;
import org.softwarewolf.gameserver.domain.User;
import org.softwarewolf.gameserver.domain.dto.MyAccountDto;
import org.softwarewolf.gameserver.domain.dto.ResetPasswordDto;
import org.softwarewolf.gameserver.domain.dto.RoleLists;
import org.softwarewolf.gameserver.domain.dto.RolesData;
import org.softwarewolf.gameserver.domain.dto.UserAdminDto;
import org.softwarewolf.gameserver.domain.dto.UserDto;
import org.softwarewolf.gameserver.domain.dto.UserListItem;
import org.softwarewolf.gameserver.repository.DeleteableRoleRepository;
import org.softwarewolf.gameserver.repository.SimpleGrantedAuthorityRepository;
import org.softwarewolf.gameserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserService {
	@Autowired 
	private SimpleGrantedAuthorityRepository authRepository;
	
	@Autowired
	private DeleteableRoleRepository deletableRoleRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private SimpleGrantedAuthorityRepository simpleGrantedAuthorityRepository;
	
	@Autowired
	private SimpleGrantedAuthorityService authService;
	
	@Autowired
	private GameMailService gameMailService;

	@Autowired
	private MessageSource messageSource;
	
	private static final String EMAIL_PATTERN =
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	public RolesData getRolesData() {
		RolesData rolesData = new RolesData();
		rolesData.setAllRoles(getAllRoles());
		return rolesData;
	}
	
	public void setRolesData(RolesData rolesData) {
		Map<String, String> allRoles = getAllRoles(); 
		rolesData.setAllRoles(allRoles);
	}

	public List<String> getRoles() {
		List<SimpleGrantedAuthority> authorities = authRepository.findAll();
		List<String> roles = new ArrayList<>();
		for (SimpleGrantedAuthority auth : authorities) {
			String authority = auth.getAuthority().split("_")[1].toLowerCase();
			roles.add(authority);
		}
		return roles;
	}

	public void createRoles(RolesData rolesData) {
		String simpleRole;
		String role;
		simpleRole = rolesData.getCreateRole();
		role = "ROLE_" + simpleRole.toUpperCase();
		SimpleGrantedAuthority auth = new SimpleGrantedAuthority(role);
		authRepository.save(auth);
	}


	public void deleteRoles(RolesData rolesData) {
		for (String role : rolesData.getSelectedRoles()) {
			DeleteableRole deleteableRole = deletableRoleRepository.findOneByRole(role);
			if (deleteableRole != null) {
				deletableRoleRepository.delete(deleteableRole);
			}
		}
	}	
	
	public RoleLists getRoleLists(String userId) {
		RoleLists roleLists = new RoleLists();
		Map<String, String> allRoles = getAllRoles();

		if (userId != null) {
			User user = userRepository.findOneById(userId);
			List<String> userRoles = user.getRoles();
			Map<String, String> selectedRoles = new HashMap<>();
			for (String userRole : userRoles) {
				String roleString = roleToString(userRole);
				selectedRoles.put(userRole, roleString);
			}
			roleLists.setSelectedRoles(selectedRoles);
		} else {
			roleLists.setSelectedRoles(new HashMap<>());
		}
		roleLists.setAllRoles(allRoles);
		
		return roleLists;
	}
	
	public List<UserListItem> getUserList() {
		List<UserListItem> userList = new ArrayList<>();
		List<User> uList = userRepository.findAll();
		for (User user : uList) {
			UserListItem userListItem = new UserListItem(user.getId(), user.getUsername());
			userList.add(userListItem);
		}
		return userList;
	}
	
	public User getUser(String userID) {
		return userRepository.findOneById(userID);
	}

	public String roleToString(String role) {
		int underscore = role.indexOf('_') + 1;
		return role.substring(underscore).toLowerCase();
	}
	
	public String stringToRole(String role) {
		return "ROLE_" + (role.toUpperCase());
	}
	
	public Map<String, String> makeIdUsernameMap(String userId) {
		User user = userRepository.findOneById(userId);
		Map<String, String> userMap = new HashMap<String, String>();
		userMap.put(user.getId(), user.getUsername());
		
		return userMap;
	}
	
	public List<User> getUsersByRole(String role) {
		List<SimpleGrantedAuthority> sgaList = new ArrayList<>();
		SimpleGrantedAuthority sga = simpleGrantedAuthorityRepository.findByRole(role);
		sgaList.add(sga);
		return userRepository.findByAuthorities(sgaList);
	}
	
	public Map<String, String> getAllRoles() {
		Map<String, String> allRolesMap = new HashMap<>();
		List<SimpleGrantedAuthority> sgaList = simpleGrantedAuthorityRepository.findAll();
		for (SimpleGrantedAuthority sga : sgaList) {
			String roleString = roleToString(sga.getAuthority());
			allRolesMap.put(roleString, sga.getAuthority());
		}
		return allRolesMap;
	}
	
	public String getUserIdFromUsername(String username) {
		String userId = null;
		User user = userRepository.findOneByUsername(username);
		if (user != null) {
			userId = user.getId();
		}
		return userId;
	}
	
	public String getCurrentUserId() {
		User userDetails =
				 (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = userDetails.getUsername();
		return getUserIdFromUsername(username);
	}

	public String getCurrentUserName() {
		UserDetails userDetails =
				 (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userDetails.getUsername();
	}
	
	public String getCurrentUserEmail() {
		UserDetails userDetails =
				 (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = userRepository.findOneByUsername(userDetails.getUsername());
		return user.getEmail();
	}
	
	public User getCurrentUser() {
		UserDetails userDetails =
				 (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = userRepository.findOneByUsername(userDetails.getUsername());
		return user;
	}
	
	public Locale getCurrentUserLocale() {
		Locale locale;
		UserDetails userDetails = null;
		try {
			userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		} catch (Exception e) {
			// not logged in, no worries
		}
		if (userDetails == null) {
			locale = LocaleContextHolder.getLocale();
		} else {
			User user = userRepository.findOneByUsername(userDetails.getUsername());
			locale = user.getLocale();
		}
		return locale;
	}
	
	public void resetPassword(ResetPasswordDto resetPasswordDto) {
		User user = getCurrentUser();
		String currentPassword = resetPasswordDto.getCurrentPassword();
		if (currentPassword == null) {
			throw new RuntimeException("You must pass a valid password.");
		}
		PasswordEncoder encoder = authService.getPasswordEncoder();
		if (!encoder.matches(currentPassword, user.getPassword())) {
			throw new RuntimeException("Current password does not match.");
		}
	    String newPassword = resetPasswordDto.getNewPassword();
	    String verifyPassword = resetPasswordDto.getVerifyPassword();
	    if (newPassword == null || newPassword.length() < 2) {
	    	throw new RuntimeException("Invalid new password");
	    }
	    if (!newPassword.equals(verifyPassword)) {
	    	throw new RuntimeException("New password does not equal verify password");
	    }
	    
	    String encodedNewPassword = encoder.encode(newPassword);
	    user.setPassword(encodedNewPassword);
	    userRepository.save(user);
	}
	
	public void adminResetPassword(ResetPasswordDto resetPasswordDto) {
		User user = getCurrentUser();

		SecureRandom random = new SecureRandom();
		String tempPassword = new BigInteger(50, random).toString(32);
		  
		PasswordEncoder encoder = authService.getPasswordEncoder();
	    String encodedNewPassword = encoder.encode(tempPassword);
	    user.setPassword(encodedNewPassword);
	    user = userRepository.save(user);
	    
	    gameMailService.adminResetPassword(user, tempPassword);
	}
	
	public void initMyAccountDto(MyAccountDto myAccountDto) {
		User user = getCurrentUser();
		myAccountDto.setFirstName(user.getFirstName());
		myAccountDto.setLastName(user.getLastName());
		myAccountDto.setEmail(user.getEmail());
	}
	
	public void editMyAccount(MyAccountDto myAccountDto) {
		validateMyAccountInfo(myAccountDto);
		User user = getCurrentUser();
		user.setFirstName(myAccountDto.getFirstName());
		user.setLastName(myAccountDto.getLastName());
		user.setEmail(myAccountDto.getEmail());
		userRepository.save(user);
	}

	private void validateMyAccountInfo(MyAccountDto myAccountDto) {
		String nameRegex = "^[\\p{L} .'-]+$";
		Pattern namePattern = Pattern.compile(nameRegex);
		String firstName = myAccountDto.getFirstName();
		String invalidFirstName = ControllerUtils.getI18nMessage("myAccount.error.invalidFirstName");
		StringBuilder errors = new StringBuilder();
		if (firstName != null) {
			Matcher firstNameMatcher = namePattern.matcher(firstName);
			if (!firstNameMatcher.matches()) {
				errors.append(invalidFirstName).append(" ");
			}
		} else {
			errors.append(invalidFirstName).append(" ");
		}

		String lastName = myAccountDto.getLastName();
		String invalidLastName = ControllerUtils.getI18nMessage("myAccount.error.invalidLastName");
		if (lastName != null) {
			Matcher lastNameMatcher = namePattern.matcher(lastName);
			if (!lastNameMatcher.matches()) {
				errors.append(invalidLastName).append(" ");			
			}
		} else {
			errors.append(invalidLastName).append(" ");			
		}
		
		String email = myAccountDto.getEmail();
		String invalidEmail = ControllerUtils.getI18nMessage("myAccount.error.invalidEmail");
		if (email != null) {
			Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
			Matcher emailMatcher = emailPattern.matcher(email);
			if(!emailMatcher.matches()) {
				errors.append(invalidEmail);
			}
		} else {
			errors.append(invalidEmail);
		}
		
		if (errors.length() > 0) {
			throw new RuntimeException(errors.toString());
		}
	}
	
	public void registerAccount(UserDto userDto) {
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(userDto.getEmail());
		
		String errMsg = "";
		String userName = userDto.getUsername();
		if (userName == null || userName.isEmpty()) {
			String message = ControllerUtils.getI18nMessage("createAccount.error.usernameRequired");
			errMsg += message;
		}
		User user = userRepository.findOneByUsername(userDto.getUsername());
		if (user != null) {
			String message = ControllerUtils.getI18nMessage("createAccount.error.usernameInUse");
			errMsg += message;
		}
		if (!userDto.getPassword().equals(userDto.getVerifyPassword())) {
			String message = ControllerUtils.getI18nMessage("createAccount.error.passwordVerifyWrong");
			errMsg += message;
		}
		if (!matcher.matches()) {
			String message = ControllerUtils.getI18nMessage("createAccount.error.invalidEmail");
			errMsg += message;
		}
		if (userRepository.findOneByEmail(userDto.getEmail()) != null) {
			String message = ControllerUtils.getI18nMessage("createAccount.error.duplicateEmail");
			errMsg += message;
		}
		
		if (!errMsg.isEmpty()) {
			throw new RuntimeException(errMsg);
		}
		
		user = new User();
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		user.setCredentialsNonExpired(true);
		user.setEmail(userDto.getEmail());
		user.setEnabled(false);
		String password = userDto.getPassword();
		if (password != null && !password.isEmpty()) {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			password = encoder.encode(userDto.getPassword());
		}
		user.setPassword(password);		
		user.setUsername(userDto.getUsername());
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		userRepository.save(user);
	}
	
	public void editUserData(UserAdminDto userAdminDto) {
		User user = userAdminDto.getSelectedUser();
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		User prevVersion = null;
		if (user.getId() != null) {
			prevVersion = userRepository.findOneById(user.getId());
		}
		String newPassword = userAdminDto.getPassword();
		String verifyPassword = userAdminDto.getVerifyPassword();
		if (prevVersion != null) {
			if (newPassword == null && verifyPassword == null) {
				String prevEncodedPwd = prevVersion.getPassword();
				user.setPassword(prevEncodedPwd);
			} else if (newPassword.equals(verifyPassword)){
				String encodedPwd = encoder.encode(newPassword);
				user.setPassword(encodedPwd);
			} else {
				throw new RuntimeException(ControllerUtils.getI18nMessage("editUser.error.verifyPasswordDoeNotMatch"));
			}
		} else {
			user.setId(null);
			if (userAdminDto.getPassword() != null && userAdminDto.getPassword().equals(userAdminDto.getVerifyPassword())) {
				String encodedPwd = encoder.encode(userAdminDto.getPassword());
				user.setPassword(encodedPwd);
			} else {
				throw new RuntimeException(ControllerUtils.getI18nMessage("editUser.error.verifyPasswordDoeNotMatch"));
			}
		}
		userRepository.save(user);
	}
	
	public void initUserAdminDto(UserAdminDto userAdminDto, String userId) {
		List<UserListItem> userList = getUserList();
		userAdminDto.setUserList(userList);
		User user = null;
		if (userId == null) {
			user = new User();
		} else {
			user = userRepository.findOneById(userId);
			if (user == null) {
				user = new User();
			}
		}
   		userAdminDto.setSelectedUser(user);
		userAdminDto.setAllRoles(getAllRoles());		
	}
	
	public String getRoleListsAsJson(String userId) {
		if ("newUser".equals(userId)) {
			userId = null;
		}
		RoleLists roleLists = getRoleLists(userId);
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
	
	public List<User> findAll() {
		return userRepository.findAll();
	}
	
	public boolean hasRole(User user, String role) {
		return user.getRoles().contains(role);
	}
	
	public boolean currentUserHasRole(String role) {
		User user = this.getCurrentUser();
		return user.getRoles().contains(role);
	}
	
	public boolean currentUserHasRole(List<String> roles) {
		User user = this.getCurrentUser();
		for (String role : roles) {
			if (user.getRoles().contains(role)) {
				return true;
			}
		}
		return false;
	}
	
	public List<User> findAllByKeyValues(String key, Object[] value) {
		return userRepository.findAllByKeyValues(key, value);
	}
	
	public User save(User user) {
		return userRepository.save(user);
	}
	
	public List<User> save(List<User> userList) {
		return userRepository.saveAll(userList);
	}
}
