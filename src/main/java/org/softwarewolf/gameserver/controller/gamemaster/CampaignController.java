package org.softwarewolf.gameserver.controller.gamemaster;

import javax.servlet.http.HttpSession;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.softwarewolf.gameserver.controller.utils.FeFeedback;
import org.softwarewolf.gameserver.domain.Campaign;
import org.softwarewolf.gameserver.domain.User;
import org.softwarewolf.gameserver.domain.dto.CampaignDto;
import org.softwarewolf.gameserver.domain.dto.SelectCampaignDto;
import org.softwarewolf.gameserver.repository.UserRepository;
import org.softwarewolf.gameserver.service.CampaignService;
import org.softwarewolf.gameserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/gamemaster")
public class CampaignController {
	
	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	protected CampaignService campaignService;

	@Autowired
	protected UserService userService;
	
	/**
	 * This method is distinct from user/selectCampaign in that the list of campaigns is generated
	 * from the list of campaigns 'owned' by the currently logged in user
	 * @param selectCampaignDto
	 * @return
	 */
	@RequestMapping(value = "/selectCampaign", method = RequestMethod.GET)
	@Secured({"USER"})
	public String selectCampaign(final SelectCampaignDto selectCampaignDto) {
		campaignService.initSelectCampaignDto(selectCampaignDto, ControllerUtils.GM_TYPE);

		return ControllerUtils.SELECT_CAMPAIGN;
	}
	
	@RequestMapping(value = "/selectCampaign", method = RequestMethod.POST)
	@Secured({"USER"})
	public String selectCampaign(HttpSession session, final SelectCampaignDto selectCampaignDto) {
		String campaignId = selectCampaignDto.getSelectedCampaignId();
		Campaign selectedCampaign = campaignService.findOne(campaignId);
		session.setAttribute(ControllerUtils.CAMPAIGN_ID, campaignId);
		session.setAttribute("campaignName", selectedCampaign.getName());
		
		return ControllerUtils.USER_MENU;
	}
	
	@RequestMapping(value = "/createCampaign", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String getCampaignDto(CampaignDto campaignDto, FeFeedback feFeedback) {
		UserDetails userDetails =
				 (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userName = userDetails.getUsername();
		User user = userRepository.findOneByUsername(userName);
		
		campaignService.initCampaignDto(campaignDto, user.getId());
		
		return ControllerUtils.CREATE_CAMPAIGN;
	}
	
	@RequestMapping(value = "/createCampaign", method = RequestMethod.POST)
	@Secured({"GAMEMASTER", "ADMIN"})
	public String postCampaign(@ModelAttribute CampaignDto campaignDto, FeFeedback feFeedback) {
		String ownerId = campaignDto.getOwnerId();
		campaignService.createCampaign(campaignDto);
		campaignService.initCampaignDto(campaignDto, ownerId);
		feFeedback.setInfo("Campaign " + campaignDto.getCampaign().getName() + " created.");
		
		return ControllerUtils.CREATE_CAMPAIGN;
	}
}