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

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.softwarewolf.gameserver.controller.helper.ControllerHelper;
import org.softwarewolf.gameserver.domain.Folio;
import org.softwarewolf.gameserver.domain.helper.FeFeedback;
import org.softwarewolf.gameserver.domain.helper.FolioCreator;
import org.softwarewolf.gameserver.domain.SimpleTag;
import org.softwarewolf.gameserver.domain.helper.SelectFolioCreator;
import org.softwarewolf.gameserver.domain.helper.ViewFolioCreator;
import org.softwarewolf.gameserver.service.FolioService;

@Controller
public class FolioController {
	@Autowired
	protected FolioService folioService;
	
	@RequestMapping(value = "/gamemaster/editFolio", method = RequestMethod.GET)
	@Secured({"USER"})
	public String editFolio(HttpSession session, FolioCreator folioCreator, final FeFeedback feFeedback) {
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
	
	@RequestMapping(value = "/gamemaster/editFolio/{folioId}", method = RequestMethod.GET)
	@Secured({"USER"})
	public String editFolioWithId(HttpSession session, FolioCreator folioCreator, 
			@PathVariable String folioId, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		

		folioService.initFolioCreator(folioCreator, folioId, campaignId);
		folioCreator.setForwardingUrl(ControllerHelper.EDIT_FOLIO);
		feFeedback.setUserStatus("You are editing folio " + folioCreator.getFolio().getTitle());
		return ControllerHelper.EDIT_FOLIO;
	}
	
	@RequestMapping(value = "/gamemaster/getFolio/{folioId}", method = RequestMethod.GET)
	@Secured({"USER"})
	public String getFolio(HttpSession session, @PathVariable String folioId, 
			FolioCreator folioCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		

		Folio folio = folioService.findOne(folioId);
		folioService.initFolioCreator(folioCreator, folio, campaignId);
		feFeedback.setUserStatus("You are editing '" + folio.getTitle() + "'");
		return ControllerHelper.EDIT_FOLIO;
	}
		
	@RequestMapping(value = "/gamemaster/removeTagFromFolio/{folioId}/{tagId}", method = RequestMethod.GET)
	@Secured({"USER"})
	public String removeTagFromFolio(HttpSession session, FolioCreator folioCreator, 
			@PathVariable String folioId, @PathVariable String tagId, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		

		Folio folio = folioService.removeTagFromFolio(folioId, tagId);
		folioService.initFolioCreator(folioCreator, folio, campaignId);
		feFeedback.setInfo("You have modified folio " + folio.getTitle());
		return ControllerHelper.EDIT_FOLIO;
	}

	@RequestMapping(value = "/gamemaster/addTagToFolio/{folioId}/{tagName}", method = RequestMethod.GET)
	@Secured({"USER"})
	public String addTagToFolio(HttpSession session, FolioCreator folioCreator, 
			@PathVariable String folioId, @PathVariable String tagName, final FeFeedback feFeedback) {
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
		folio = folioService.addTagToFolio(campaignId, folio.getId(), tagName);
		folioService.initFolioCreator(folioCreator, folio, campaignId);
		feFeedback.setInfo("You have modified folio " + folio.getTitle());

		return ControllerHelper.EDIT_FOLIO;
	}

	@RequestMapping(value = "/gamemaster/editFolio", method = RequestMethod.POST)
	@Secured({"USER","GAMEMASTER"})
	public String postEditPage(HttpSession session, FolioCreator folioCreator, 
			final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		

		Folio folio = folioCreator.getFolio();
		try {
			String selectedTags = folioCreator.getSelectedTags();
			ObjectMapper mapper = new ObjectMapper();
			JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, SimpleTag.class);
			List<SimpleTag> selectedTagList = null;

			if (selectedTags.isEmpty() || "{}".equals(selectedTags)) {
				selectedTagList = new ArrayList<>();
			} else {
				try {
					selectedTagList = mapper.readValue(selectedTags, type);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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
		feFeedback.setUserStatus("You are editing folio " + folio.getTitle());
		return ControllerHelper.EDIT_FOLIO;
	}	
	
	@RequestMapping(value = "/shared/selectFolio/{returnCode}", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String selectFolio(HttpSession session, SelectFolioCreator selectFolioCreator,
			@PathVariable String returnCode, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		
		
		folioService.initSelectFolioCreator(campaignId, selectFolioCreator);
		// Need to pick the correct forwarding url, this is just generic
		if ("edit".equals(returnCode)) {
			selectFolioCreator.setForwardingUrl(ControllerHelper.EDIT_FOLIO);
		} else if ("view".equals(returnCode)) {
			selectFolioCreator.setForwardingUrl(ControllerHelper.VIEW_FOLIO);
		}
		return ControllerHelper.SELECT_FOLIO;
	}

	@RequestMapping(value = "/shared/folio/addTagToSearch", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String addTagToSearch(HttpSession session, SelectFolioCreator selectFolioCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		
		
		folioService.initSelectFolioCreator(campaignId, selectFolioCreator); 
		return ControllerHelper.SELECT_FOLIO;
	}

	@RequestMapping(value = "/shared/folio/removeTagFromSearch", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String removeTagFromSearch(HttpSession session, SelectFolioCreator selectFolioCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		
		
		folioService.initSelectFolioCreator(campaignId, selectFolioCreator); 
		return ControllerHelper.SELECT_FOLIO;
	}

	@RequestMapping(value = "/shared/viewFolio", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String viewFolio(HttpSession session, ViewFolioCreator viewFolioCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		
		
		viewFolioCreator.setForwardingUrl(ControllerHelper.VIEW_FOLIO);
		return ControllerHelper.VIEW_FOLIO;
	}

	@RequestMapping(value = "/shared/viewFolio/{folioId}", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String viewFolioWithId(HttpSession session, ViewFolioCreator viewFolioCreator, 
			@PathVariable String folioId, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		
		
		folioService.initViewFolioCreator(viewFolioCreator, folioId);
		viewFolioCreator.setForwardingUrl(ControllerHelper.VIEW_FOLIO);
		return ControllerHelper.VIEW_FOLIO;
	}

}
