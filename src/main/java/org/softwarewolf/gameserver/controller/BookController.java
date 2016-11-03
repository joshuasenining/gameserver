package org.softwarewolf.gameserver.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.softwarewolf.gameserver.controller.helper.ControllerHelper;
import org.softwarewolf.gameserver.controller.helper.FeFeedback;
import org.softwarewolf.gameserver.domain.Folio;
import org.softwarewolf.gameserver.domain.SimpleTag;
import org.softwarewolf.gameserver.domain.dto.FolioDto;
import org.softwarewolf.gameserver.domain.dto.SelectFolioCreator;
import org.softwarewolf.gameserver.domain.dto.ViewFolioCreator;
import org.softwarewolf.gameserver.service.FolioService;

@Controller
@RequestMapping("/shared")
public class BookController {
	@Autowired
	protected FolioService folioService;
	
	@RequestMapping(value = "/editBook", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER"})
	public String editFolio(HttpSession session, FolioDto folioCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		

		Folio nullFolio = null;
		folioService.initFolioCreator(folioCreator, nullFolio, campaignId);
		folioCreator.setForwardingUrl(ControllerHelper.EDIT_FOLIO);
		feFeedback.setUserStatus("You are creating a new folio");
		return ControllerHelper.EDIT_FOLIO;
	}
	
	@RequestMapping(value = "/editBook/{bookId}", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER"})
	public String editFolioWithId(HttpSession session, FolioDto folioCreator, 
			@PathVariable String bookId, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		

		folioService.initFolioCreator(folioCreator, bookId, campaignId);
		folioCreator.setForwardingUrl(ControllerHelper.EDIT_FOLIO);
		feFeedback.setUserStatus("You are editing folio " + folioCreator.getFolio().getTitle());
		return ControllerHelper.EDIT_FOLIO;
	}
	
	@RequestMapping(value = "/getBook/{bookId}", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER"})
	public String getFolio(HttpSession session, @PathVariable String bookId, 
			FolioDto folioCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		

		Folio folio = folioService.findOne(bookId);
		folioService.initFolioCreator(folioCreator, folio, campaignId);
		feFeedback.setUserStatus("You are editing '" + folio.getTitle() + "'");
		return ControllerHelper.EDIT_FOLIO;
	}
		
	@RequestMapping(value = "/removeFolioFromBook/{folioId}/{bookId}", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER"})
	public String removeTagFromFolio(HttpSession session, FolioDto folioCreator, 
			@PathVariable String folioId, @PathVariable String bookId, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		

		Folio folio = folioService.removeTagFromFolio(folioId, bookId);
		folioService.initFolioCreator(folioCreator, folio, campaignId);
		feFeedback.setInfo("You have modified folio " + folio.getTitle());
		return ControllerHelper.EDIT_FOLIO;
	}
/*
	@RequestMapping(value = "/addFolioToBook/{folioId}/{bookId}", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER"})
	public String addTagToFolio(HttpSession session, FolioDto folioCreator, 
			@PathVariable String folioId, @PathVariable String bookId, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		

		Folio folio = null;
		if (folioId != null) {
			folio = folioService.findOne(folioId);
		}
		if (folio == null) {
			folio = new Folio();
			folio.setCampaignId(campaignId);
			folio.setTitle("Placeholder title");
		} 
		if (folio.getId() == null) {
			try {
				folio = folioService.save(folio);
			} catch (Exception e) {
				String errorMessage = e.getMessage();
				feFeedback.setError(errorMessage);
				folioService.initFolioCreator(folioCreator, folio, campaignId);
				return ControllerHelper.EDIT_FOLIO;
			}
		}
		folio = folioService.addTagToFolio(campaignId, folio.getId(), bookId);
		folioService.initFolioCreator(folioCreator, folio, campaignId);
//		feFeedback.setInfo("You have modified folio " + folio.getTitle());

		return ControllerHelper.EDIT_FOLIO;
	}
*/
	@RequestMapping(value = "/editBook", method = RequestMethod.POST)
	@Secured({"USER","GAMEMASTER"})
	public String postEditPage(HttpSession session, FolioDto folioCreator, 
			final FeFeedback feFeedback) {
/*		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		

		Folio folio = folioCreator.getFolio();
		try {
			List<SimpleTag> selectedTagList = folioCreator.getSelectedTags();

			folio.setTags(selectedTagList);
			folio = folioService.save(folio);
			folioService.initFolioCreator(folioCreator, folio, campaignId);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			feFeedback.setError(errorMessage);
			folioService.initFolioCreator(folioCreator, folio, campaignId);
			feFeedback.setUserStatus("You are editing folio " + folio.getTitle());
			return ControllerHelper.EDIT_FOLIO;
		}
		
		feFeedback.setInfo("You have modified folio " + folio.getTitle());
		feFeedback.setUserStatus("You are editing folio " + folio.getTitle());*/
		return ControllerHelper.EDIT_FOLIO;
	}	

	@RequestMapping(value = "/viewBook", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER"})
	public String viewFolio(HttpSession session, ViewFolioCreator viewFolioCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		
		
		viewFolioCreator.setForwardingUrl(ControllerHelper.VIEW_FOLIO);
		return ControllerHelper.VIEW_FOLIO;
	}

	@RequestMapping(value = "/viewBook/{bookId}", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER"})
	public String viewFolioWithId(HttpSession session, ViewFolioCreator viewFolioCreator, 
			@PathVariable String bookId, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		
		
		folioService.initViewFolioCreator(viewFolioCreator, bookId);
		viewFolioCreator.setForwardingUrl(ControllerHelper.VIEW_FOLIO);
		return ControllerHelper.VIEW_FOLIO;
	}

}
