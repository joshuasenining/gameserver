package org.softwarewolf.gameserver.controller;

import org.softwarewolf.gameserver.domain.User;
import org.softwarewolf.gameserver.repository.CampaignRepository;
import org.softwarewolf.gameserver.repository.SimpleGrantedAuthorityRepository;
import org.softwarewolf.gameserver.repository.LocationRepository;
import org.softwarewolf.gameserver.repository.LocationTypeRepository;
import org.softwarewolf.gameserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private SimpleGrantedAuthorityRepository sgaRepo;
	
	@Autowired 
	private CampaignRepository campaignRepo;
	
	@Autowired 
	private LocationTypeRepository locationTypeRepo;
	
	@Autowired 
	private LocationRepository locationRepo;
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		try {
		User user = userRepo.findOneByUsername("gm");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return "/login";
	}

	@RequestMapping(value = "/user/menu", method = RequestMethod.GET)
	public String getMenu() {
		return "/user/menu";
	}
	
	
}
