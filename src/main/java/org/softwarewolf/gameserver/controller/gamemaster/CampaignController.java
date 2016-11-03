package org.softwarewolf.gameserver.controller.gamemaster;

import org.softwarewolf.gameserver.domain.Campaign;
import org.softwarewolf.gameserver.domain.User;
import org.softwarewolf.gameserver.domain.dto.CampaignDto;
import org.softwarewolf.gameserver.domain.dto.SelectCampaignHelper;
import org.softwarewolf.gameserver.repository.UserRepository;
import org.softwarewolf.gameserver.service.CampaignService;
import org.softwarewolf.gameserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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
	 * @param selectCampaignHelper
	 * @return
	 */
	@RequestMapping(value = "/selectCampaign", method = RequestMethod.GET)
	@Secured({"USER"})
	public String selectCampaign(final SelectCampaignHelper selectCampaignHelper) {
		UserDetails userDetails =
				 (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = userDetails.getUsername();
		String userId = userService.getUserIdFromUsername(username);
		campaignService.initSelectCampaignHelperByGM(selectCampaignHelper, userId);

		return "/user/selectCampaign";
	}
	
//	@RequestMapping(value = "/selectCampaign", method = RequestMethod.POST)
//	@Secured({"USER"})
//	public String selectCampaign(HttpSession session, final SelectCampaignHelper selectCampaignHelper) {
//		String campaignId = selectCampaignHelper.getSelectedCampaignId(); 
//		String campaignName = selectCampaignHelper.getSelectedCampaignName();
//		session.setAttribute(CAMPAIGN_ID, campaignId);
//		session.setAttribute("campaignName", campaignName);
//		
//		return ControllerHelper.USER_MENU;
//	}
	
	@RequestMapping(value = "/createCampaign", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String getCampaignDto(CampaignDto campaignDto) {
		UserDetails userDetails =
				 (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userName = userDetails.getUsername();
		User user = userRepository.findOneByUsername(userName);
		
		campaignService.initCampaignDto(campaignDto, user);
		
		return "/gamemaster/createCampaign";		
	}
	
	@RequestMapping(value = "/createCampaign", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String postCampaign(@ModelAttribute CampaignDto campaignDto, BindingResult bindingResult) {
		Campaign campaign = campaignDto.getCampaign();
		String ownerId = campaignDto.getOwnerId();
		campaign.setOwnerId(ownerId);
		campaignService.saveCampaign(campaign);
		
		return "/gamemaster/campaignCreated";
	}
}