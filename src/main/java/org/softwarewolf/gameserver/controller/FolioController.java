package org.softwarewolf.gameserver.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.softwarewolf.gameserver.controller.helper.ControllerUtils;
import org.softwarewolf.gameserver.controller.helper.FeFeedback;
import org.softwarewolf.gameserver.domain.Folio;
import org.softwarewolf.gameserver.domain.dto.FolioDto;
import org.softwarewolf.gameserver.domain.dto.SelectFolioDto;
import org.softwarewolf.gameserver.service.FolioService;
import org.softwarewolf.gameserver.service.SimpleTagService;

@Controller
@RequestMapping("/shared")
public class FolioController {
	@Autowired
	protected FolioService folioService;
	
	@Autowired
	protected SimpleTagService simpleTagService;
	
	private static final String NEW_FOLIO = "You are creating a new folio";
	private static final String EDITING_FOLIO = "You are editing folio '";
	
	@RequestMapping(value = "/editFolio", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER"})
	public String editFolio(HttpSession session, FolioDto folioDto, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerUtils.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerUtils.USER_MENU;
		}		

		Folio nullFolio = null;
		folioService.initFolioDto(folioDto, nullFolio, campaignId, FolioService.EDIT);
		folioDto.setForwardingUrl(ControllerUtils.EDIT_FOLIO);
		feFeedback.setUserStatus(NEW_FOLIO);
		return ControllerUtils.EDIT_FOLIO;
	}
	
	@RequestMapping(value = "/editFolio/{folioId}", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER"})
	public String editFolioWithId(HttpSession session, FolioDto folioDto, 
			@PathVariable String folioId, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerUtils.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerUtils.USER_MENU;
		}		

		folioService.initFolioDto(folioDto, folioId, campaignId, FolioService.EDIT);
		folioDto.setForwardingUrl(ControllerUtils.EDIT_FOLIO);
		feFeedback.setUserStatus(EDITING_FOLIO + "'" + folioDto.getFolio().getTitle() + "'");
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

		Folio folio = null;
		try {
			folio = folioService.saveFolio(folioDto);
			folioService.initFolioDto(folioDto, folio, campaignId, FolioService.EDIT);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			feFeedback.setError(errorMessage);
			folioService.initFolioDto(folioDto, folio, campaignId, FolioService.EDIT);
			feFeedback.setUserStatus("You are editing folio " + (folio == null ? "" : folio.getTitle()));
			return ControllerUtils.EDIT_FOLIO;
		}
		
		feFeedback.setInfo("You have modified folio " + folio.getTitle());
		feFeedback.setUserStatus("You are editing folio " + folio.getTitle());
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
		
		String noFolio = null;
		folioService.initFolioDto(folioDto, noFolio, campaignId, FolioService.VIEW);
		folioDto.setForwardingUrl(ControllerUtils.VIEW_FOLIO);
		return ControllerUtils.VIEW_FOLIO;
	}

	@RequestMapping(value = "/viewFolio/{folioId}", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER"})
	public String viewFolioWithId(HttpSession session, FolioDto folioDto, 
			@PathVariable String folioId, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerUtils.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerUtils.USER_MENU;
		}		
		
		folioService.initFolioDto(folioDto, folioId, campaignId, FolioService.VIEW);
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
		String addTagName = folioDto.getAddTag();
		simpleTagService.save(addTagName, campaignId);
		
		folioService.initFolioDto(folioDto, folioDto.getFolio().getId(), campaignId, folioDto.getOperationType());
		String info = null;
		if (folioDto.getFolio().getTitle() == null || folioDto.getFolio().getTitle() == null) { 
			info = NEW_FOLIO;
		} else {
			info = EDITING_FOLIO + folioDto.getFolio().getTitle() + "'";
		}
		feFeedback.setUserStatus(info);
		return folioDto.getForwardingUrl();
	}
}
