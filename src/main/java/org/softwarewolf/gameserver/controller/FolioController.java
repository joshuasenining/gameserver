package org.softwarewolf.gameserver.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.softwarewolf.gameserver.controller.utils.FeFeedback;
import org.softwarewolf.gameserver.controller.utils.GetPermissionsFrom;
import org.softwarewolf.gameserver.domain.Folio;
import org.softwarewolf.gameserver.domain.dto.FolioDto;
import org.softwarewolf.gameserver.domain.dto.SelectFolioDto;
import org.softwarewolf.gameserver.service.CampaignService;
import org.softwarewolf.gameserver.service.FolioService;
import org.softwarewolf.gameserver.service.SimpleTagService;

@Controller
@RequestMapping("/shared")
public class FolioController {
	@Autowired
	protected FolioService folioService;
	
	@Autowired
	protected SimpleTagService simpleTagService;
	
	@Autowired
	protected CampaignService campaignService;
	
	@RequestMapping(value = "/editFolio", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER"})
	public String editFolio(HttpSession session, FolioDto folioDto, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerUtils.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerUtils.USER_MENU;
		}		

		folioDto = folioService.initFolioDto(folioDto, campaignId, FolioService.EDIT, GetPermissionsFrom.INIT);
		folioDto.setForwardingUrl(ControllerUtils.EDIT_FOLIO);
		String message = ControllerUtils.getI18nMessage("editFolio.status.creatingNew");
		feFeedback.setUserStatus(message);
		return ControllerUtils.EDIT_FOLIO;
	}
	
	@RequestMapping(value = "/editFolio/{folioId}", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER"})
	public String editFolioWithId(HttpSession session, final FolioDto folioDto, 
			@PathVariable String folioId, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerUtils.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerUtils.USER_MENU;
		}

		folioService.initFolioDto(folioId, folioDto, campaignId, FolioService.EDIT);
		folioDto.setForwardingUrl(ControllerUtils.EDIT_FOLIO);
		
		String message = ControllerUtils.getI18nMessage("editFolio.status.editing");
		feFeedback.setUserStatus(message + " '" + folioDto.getFolio().getTitle() + "'");
		return ControllerUtils.EDIT_FOLIO;
	}
	
	@RequestMapping(value = "/editFolio", method = RequestMethod.POST)
	@Secured({"USER","GAMEMASTER"})
	public String postEditPage(HttpSession session, FolioDto folioDto, 
			final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerUtils.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerUtils.USER_MENU;
		}		

		String editingMsg = ControllerUtils.getI18nMessage("editFolio.status.editing");
		Folio folio = null;
		try {
			folio = folioService.saveFolio(folioDto);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			feFeedback.setError(errorMessage);
			feFeedback.setUserStatus(editingMsg + " " +(folio == null ? "" : folio.getTitle()));
			folioDto = folioService.initFolioDto(folioDto, campaignId, FolioService.EDIT, GetPermissionsFrom.FOLIO_DTO);
			return ControllerUtils.EDIT_FOLIO;
		}
		folioService.initFolioDto(folio.getId(), folioDto, campaignId, FolioService.EDIT);
		String modified = ControllerUtils.getI18nMessage("editFolio.modified");
		feFeedback.setInfo(modified + " " + folio.getTitle());
		feFeedback.setUserStatus(editingMsg + " " + folio.getTitle());
		return ControllerUtils.EDIT_FOLIO;
	}	
	
	@RequestMapping(value = "/selectFolio", method = RequestMethod.GET)
	@Secured({"USER", "GAMEMASTER"})
	public String selectViewFolio(HttpSession session, @RequestParam("operation") String operation,
			SelectFolioDto selectFolioDto, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerUtils.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerUtils.USER_MENU;
		}		
		
		folioService.initSelectFolioDto(campaignId, selectFolioDto, operation);

		return ControllerUtils.SELECT_FOLIO;
	}
	
	@RequestMapping(value = "/folio/addTagToSearch", method = RequestMethod.POST)
	@Secured({"USER","GAMEMASTER"})
	public String addTagToSearch(HttpSession session, SelectFolioDto selectFolioDto, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerUtils.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerUtils.USER_MENU;
		}		
		
		folioService.initSelectFolioDto(campaignId, selectFolioDto, selectFolioDto.getOperationType()); 
		return ControllerUtils.SELECT_FOLIO;
	}

	@RequestMapping(value = "/folio/removeTagFromSearch", method = RequestMethod.POST)
	@Secured({"USER","GAMEMASTER"})
	public String removeTagFromSearch(HttpSession session, SelectFolioDto selectFolioDto, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerUtils.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerUtils.USER_MENU;
		}		
		
		folioService.initSelectFolioDto(campaignId, selectFolioDto, selectFolioDto.getOperationType()); 
		return ControllerUtils.SELECT_FOLIO;
	}

	@RequestMapping(value = "/viewFolio", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER"})
	public String viewFolio(HttpSession session, FolioDto folioDto, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerUtils.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerUtils.USER_MENU;
		}		
		
		folioDto = folioService.initFolioDto(folioDto, campaignId, FolioService.VIEW, GetPermissionsFrom.INIT);
		folioDto.setForwardingUrl(ControllerUtils.VIEW_FOLIO);
		return ControllerUtils.VIEW_FOLIO;
	}

	@RequestMapping(value = "/viewFolio/{folioId}", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER"})
	public String viewFolioWithId(HttpSession session, final FolioDto folioDto, 
			@PathVariable String folioId, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerUtils.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerUtils.USER_MENU;
		}		
		
		folioService.initFolioDto(folioId, folioDto, campaignId, FolioService.VIEW);
		folioDto.setForwardingUrl(ControllerUtils.VIEW_FOLIO);
		return ControllerUtils.VIEW_FOLIO;
	}

	@RequestMapping(value = "/addTag", method = RequestMethod.POST)
	@Secured({"USER","GAMEMASTER","ADMIN"})
	public String addTag(HttpSession session, FolioDto folioDto, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerUtils.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerUtils.USER_MENU;
		}		

		try {
			simpleTagService.save(folioDto.getAddTag(), campaignId);
		} catch (Exception e) {
			String message = ControllerUtils.getI18nMessage("editFolio.error.noTagName");
			feFeedback.setError(message);
			feFeedback.setInfo(getFeInfo(folioDto));
			return folioDto.getForwardingUrl();
		}
		
		folioDto = folioService.initFolioDto(folioDto, campaignId, folioDto.getOperationType(), GetPermissionsFrom.DONT);
		String info = getFeInfo(folioDto);
		feFeedback.setUserStatus(info);
		return folioDto.getForwardingUrl();
	}

	private String getFeInfo(FolioDto folioDto) {
		String info;
		if (folioDto.getFolio().getTitle() == null || folioDto.getFolio().getTitle() == null) { 
			info = ControllerUtils.getI18nMessage("editFolio.status.creatingNew");
		} else {
			info = ControllerUtils.getI18nMessage("editFolio.status.editing") + " '" + folioDto.getFolio().getTitle() + "'";
		}
		return info;
		
	}
}