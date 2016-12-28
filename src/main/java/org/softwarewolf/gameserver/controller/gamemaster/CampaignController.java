package org.softwarewolf.gameserver.controller.gamemaster;

import javax.servlet.http.HttpSession;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.softwarewolf.gameserver.controller.utils.FeFeedback;
import org.softwarewolf.gameserver.controller.utils.GetPermissionsFrom;
import org.softwarewolf.gameserver.domain.Campaign;
import org.softwarewolf.gameserver.domain.Folio;
import org.softwarewolf.gameserver.domain.dto.CampaignDto;
import org.softwarewolf.gameserver.domain.dto.FolioDto;
import org.softwarewolf.gameserver.domain.dto.SelectCampaignDto;
import org.softwarewolf.gameserver.repository.UserRepository;
import org.softwarewolf.gameserver.service.CampaignService;
import org.softwarewolf.gameserver.service.FolioService;
import org.softwarewolf.gameserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CampaignController {
	
	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	protected CampaignService campaignService;

	@Autowired
	protected UserService userService;
	
	@Autowired
	protected FolioService folioService;
	
	/**
	 * This method is distinct from user/selectCampaign in that the list of campaigns is generated
	 * from the list of campaigns 'owned' by the currently logged in user
	 * @param selectCampaignDto
	 * @return
	 */
	@RequestMapping(value = "/shared/selectCampaign/{asType}", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER"})
	public String selectCampaign(final SelectCampaignDto selectCampaignDto, @PathVariable String asType,
			FeFeedback feFeedback) {
		campaignService.initSelectCampaignDto(selectCampaignDto, asType);

		return ControllerUtils.SELECT_CAMPAIGN;
	}
	
	@RequestMapping(value = "/shared/selectCampaign/{asType}", method = RequestMethod.POST)
	@Secured({"USER","GAMEMASTER"})
	public String selectCampaign(HttpSession session, final SelectCampaignDto selectCampaignDto,
			FeFeedback feFeedback, @PathVariable String asType) {
		String campaignId = selectCampaignDto.getSelectedCampaignId();
		campaignService.initSelectCampaignDto(selectCampaignDto, asType);
		if (campaignId == null) {
			String message = ControllerUtils.getI18nMessage("selectCampaign.error.pickOne");
			feFeedback.setError(message);
			return ControllerUtils.SELECT_CAMPAIGN;
		} else {
			Campaign selectedCampaign = campaignService.findOne(campaignId);
			if (selectedCampaign == null) {
				String message = ControllerUtils.getI18nMessage("selectCampaign.error.canNotLocate");
				feFeedback.setError(message);
				return ControllerUtils.SELECT_CAMPAIGN;
			} else {
				session.setAttribute(ControllerUtils.CAMPAIGN_ID, campaignId);
				session.setAttribute("campaignName", selectedCampaign.getName());
			}
		}
		return ControllerUtils.USER_MENU;
	}
	
	@RequestMapping(value = "/shared/viewCampaignInfo/{asType}", method = RequestMethod.GET)
	public String viewCampaignInfo(HttpSession session, @RequestParam("campaignId") String selectedCampaignId,
			@PathVariable String asType, FolioDto folioDto, FeFeedback feFeedback) {
		
		try {
			Folio folio = folioService.findOneByCampaignIdAndTitle(selectedCampaignId);
			folioDto.setFolio(folio);
			folioDto = folioService.initFolioDto(folioDto, selectedCampaignId, FolioService.VIEW, GetPermissionsFrom.INIT);
			folioDto.setForwardingUrl(getForwardingUrl(asType));
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			feFeedback.setError(errorMessage);
			return getForwardingUrl(asType);
		}
		
		return ControllerUtils.VIEW_CAMPAIGN_INFO;
	}

	private String getForwardingUrl(String asType) {
		if (ControllerUtils.GM_TYPE.equals(asType)) {
			return ControllerUtils.SELECT_CAMPAIGN_GM;
		} else {
			return ControllerUtils.SELECT_CAMPAIGN_PLAYER;
		}
	}
	
	@RequestMapping(value = "/gamemaster/editCampaign", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String getCampaignDto(final CampaignDto campaignDto, FeFeedback feFeedback,
			@RequestParam(value = "campaignId", required = false) String campaignId) {
		campaignService.initCampaignDto(campaignId, campaignDto);
		
		return ControllerUtils.EDIT_CAMPAIGN;
	}
	
	@RequestMapping(value = "/gamemaster/editCampaign", method = RequestMethod.POST)
	@Secured({"GAMEMASTER", "ADMIN"})
	public String postCampaign(final CampaignDto campaignDto, FeFeedback feFeedback) {
		String campaignId = null;
		try {
			campaignService.validateCampaign(campaignDto);
			Campaign campaign = campaignService.saveCampaign(campaignDto);
			campaignId = campaign.getId();
			String campaignPrefix = ControllerUtils.getI18nMessage("editCampaign.info.campaign");
			String created = ControllerUtils.getI18nMessage("editCampaign.info.created");
			feFeedback.setInfo(campaignPrefix + " " + campaignDto.getCampaign().getName() + " " + created + ".");
		} catch (Exception e) {
			feFeedback.setError(e.getMessage());
		}
		campaignService.initCampaignDto(campaignId, campaignDto);
		return ControllerUtils.EDIT_CAMPAIGN;
	}
}