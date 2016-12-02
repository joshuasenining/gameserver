package org.softwarewolf.gameserver.controller.gamemaster;

import javax.servlet.http.HttpSession;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.softwarewolf.gameserver.controller.utils.FeFeedback;
import org.softwarewolf.gameserver.repository.UserRepository;
import org.softwarewolf.gameserver.service.CampaignService;
import org.softwarewolf.gameserver.service.FolioService;
import org.softwarewolf.gameserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/gamemaster")
public class GamemasterController {
			
	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	protected CampaignService campaignService;
	
	@Autowired
	protected UserService userService;
	
	@Autowired
	protected FolioService folioService;
	
	@RequestMapping(value = "/campaignHome", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String campaignHome(HttpSession session, FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerUtils.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerUtils.USER_MENU;
		}

		return ControllerUtils.CAMPAIGN_HOME;
	}
}