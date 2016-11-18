package org.softwarewolf.gameserver.controller;

import javax.servlet.http.HttpSession;

import org.softwarewolf.gameserver.controller.helper.ControllerUtils;
import org.softwarewolf.gameserver.domain.Campaign;
import org.softwarewolf.gameserver.domain.dto.SelectCampaignDto;
import org.softwarewolf.gameserver.repository.UserRepository;
import org.softwarewolf.gameserver.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	protected CampaignService campaignService;
	
	@RequestMapping(value = "/selectCampaign", method = RequestMethod.GET)
	@Secured({"USER"})
	public String getCampaign(SelectCampaignDto selectCampaignDto) {
		campaignService.initSelectCampaignDto(selectCampaignDto, ControllerUtils.PLAYER_TYPE);
		return ControllerUtils.SELECT_CAMPAIGN;
	}
	
	@RequestMapping(value = "/selectCampaign", method = RequestMethod.POST)
	@Secured({"USER"})
	public String postCampaign(HttpSession session, final SelectCampaignDto selectCampaignDto) {
		String campaignId = selectCampaignDto.getSelectedCampaignId(); 
		Campaign selectedCampaign = campaignService.findOne(campaignId);
		session.setAttribute("campaignId", campaignId);
		session.setAttribute("campaignName", selectedCampaign.getName());
		
		return ControllerUtils.USER_MENU;
	}

}