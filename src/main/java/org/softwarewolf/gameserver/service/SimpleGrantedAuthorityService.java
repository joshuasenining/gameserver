package org.softwarewolf.gameserver.service;

import java.util.ArrayList;
import java.util.List;

import org.softwarewolf.gameserver.domain.User;
import org.softwarewolf.gameserver.repository.SimpleGrantedAuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class SimpleGrantedAuthorityService {
	@Autowired 
	private SimpleGrantedAuthorityRepository repository;
	@Autowired
	private UserService userService;
	
	public List<String> getAvailableList(String userID) {
		List<String> availableRoles = new ArrayList<>();
		List<SimpleGrantedAuthority> sgaList = repository.findAll();
		User user = userService.getUser(userID);
		List<String> usersRoles = user.getRoles();
		for (SimpleGrantedAuthority sga : sgaList) {
			if (!usersRoles.contains(sga.getAuthority())) {
				availableRoles.add(sga.getAuthority());
			}
		}
		return availableRoles;
	}
	
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
