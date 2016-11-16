package org.softwarewolf.gameserver.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.softwarewolf.gameserver.controller.helper.ControllerHelper;
import org.softwarewolf.gameserver.controller.helper.FeFeedback;
import org.softwarewolf.gameserver.domain.Folio;
import org.softwarewolf.gameserver.domain.dto.FolioDto;
import org.softwarewolf.gameserver.domain.dto.SelectFolioDto;
import org.softwarewolf.gameserver.domain.dto.ViewFolioDto;
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
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		

		Folio nullFolio = null;
		folioService.initFolioDto(folioDto, nullFolio, campaignId);
		folioDto.setForwardingUrl(ControllerHelper.EDIT_FOLIO);
		feFeedback.setUserStatus(NEW_FOLIO);
		return ControllerHelper.EDIT_FOLIO;
	}
	
	@RequestMapping(value = "/editFolio/{folioId}", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER"})
	public String editFolioWithId(HttpSession session, FolioDto folioDto, 
			@PathVariable String folioId, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		

		folioService.initFolioDto(folioDto, folioId, campaignId);
		folioDto.setForwardingUrl(ControllerHelper.EDIT_FOLIO);
		feFeedback.setUserStatus(EDITING_FOLIO + "'" + folioDto.getFolio().getTitle() + "'");
		return ControllerHelper.EDIT_FOLIO;
	}
	
	@RequestMapping(value = "/editFolio", method = RequestMethod.POST)
	@Secured({"USER","GAMEMASTER"})
	public String postEditPage(HttpSession session, FolioDto folioDto, 
			final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		

		Folio folio = null;
		try {
			folio = folioService.saveFolio(folioDto);
			folioService.initFolioDto(folioDto, folio, campaignId);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			feFeedback.setError(errorMessage);
			folioService.initFolioDto(folioDto, folio, campaignId);
			feFeedback.setUserStatus("You are editing folio " + folio.getTitle());
			return ControllerHelper.EDIT_FOLIO;
		}
		
		feFeedback.setInfo("You have modified folio " + folio.getTitle());
		feFeedback.setUserStatus("You are editing folio " + folio.getTitle());
		return ControllerHelper.EDIT_FOLIO;
	}	
	
	@RequestMapping(value = "/selectFolio", method = RequestMethod.GET)
	@Secured({"USER", "GAMEMASTER"})
	public String selectFolio(HttpSession session, SelectFolioDto selectFolioDto,
			@RequestParam String from, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		
		
		folioService.initSelectFolioCreator(campaignId, selectFolioDto);
		// Need to pick the correct forwarding url, this is just generic
		if ("edit".equals(from)) {
			selectFolioDto.setForwardingUrl(ControllerHelper.EDIT_FOLIO);
		} else if ("view".equals(from)) {
			selectFolioDto.setForwardingUrl(ControllerHelper.VIEW_FOLIO);
		}
		return ControllerHelper.SELECT_FOLIO;
	}

	@RequestMapping(value = "/folio/addTagToSearch", method = RequestMethod.POST)
	@Secured({"USER","GAMEMASTER"})
	public String addTagToSearch(HttpSession session, SelectFolioDto selectFolioDto, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		
		
		folioService.initSelectFolioCreator(campaignId, selectFolioDto); 
		return ControllerHelper.SELECT_FOLIO;
	}

	@RequestMapping(value = "/folio/removeTagFromSearch", method = RequestMethod.POST)
	@Secured({"USER","GAMEMASTER"})
	public String removeTagFromSearch(HttpSession session, SelectFolioDto selectFolioDto, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		
		
		folioService.initSelectFolioCreator(campaignId, selectFolioDto); 
		return ControllerHelper.SELECT_FOLIO;
	}

	@RequestMapping(value = "/viewFolio", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER"})
	public String viewFolio(HttpSession session, ViewFolioDto viewFolioDto, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		
		
		folioService.initViewFolioDto(viewFolioDto, null);
		viewFolioDto.setForwardingUrl(ControllerHelper.VIEW_FOLIO);
		return ControllerHelper.VIEW_FOLIO;
	}

	@RequestMapping(value = "/viewFolio/{folioId}", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER"})
	public String viewFolioWithId(HttpSession session, ViewFolioDto viewFolioDto, 
			@PathVariable String folioId, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		
		
		folioService.initViewFolioDto(viewFolioDto, folioId);
		viewFolioDto.setForwardingUrl(ControllerHelper.VIEW_FOLIO);
		return ControllerHelper.VIEW_FOLIO;
	}

	@RequestMapping(value = "/addTag", method = RequestMethod.POST)
	@Secured({"USER","GAMEMASTER","ADMIN"})
	public String addTag(HttpSession session, FolioDto folioDto, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		
		String addTagName = folioDto.getAddTag();
		simpleTagService.save(addTagName, campaignId);
		
		folioService.initFolioDto(folioDto, folioDto.getFolio().getId(), campaignId);
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
