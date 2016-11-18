package org.softwarewolf.gameserver.controller;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.softwarewolf.gameserver.controller.helper.ControllerUtils;
import org.softwarewolf.gameserver.controller.helper.FeFeedback;
import org.softwarewolf.gameserver.controller.helper.ImportExportHelper;
import org.softwarewolf.gameserver.service.ImportExportService;
import org.softwarewolf.gameserver.service.CampaignService;

@Controller
@RequestMapping(value = "/admin")
public class ImportExportController {
	@Autowired
	private ImportExportService importExportService;
	@Autowired
	private CampaignService campaignService;
	
	@RequestMapping(value = "/exportCampaign", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public String exportCampaign(HttpSession session, final ImportExportHelper importExportHelper,
			final FeFeedback feFeedback) {

		boolean isImport = false;
		try {
			importExportService.initImportExportHelper(importExportHelper, isImport, ControllerUtils.EXPORT_CAMPAIGN);
		} catch (IOException e) {
			feFeedback.setError(e.getMessage());
			return ControllerUtils.EXPORT_CAMPAIGN;
		}
		feFeedback.setUserStatus("You are about to backup a campaign");

		return importExportHelper.getForwardingUrl();
	}
	
	@RequestMapping(value = "/exportCampaign", method = RequestMethod.POST)
	@Secured({"ADMIN"})
	public String exportCampaignPost(HttpSession session, final ImportExportHelper importExportHelper, 
			final FeFeedback feFeedback) {
		String backupFile;
		try {
			String campaignId = importExportHelper.getSelectedCampaignId();
			backupFile = importExportService.exportCampaign(campaignId);
			feFeedback.setError(null);
			feFeedback.setInfo("You have successfull exported " + backupFile);
			feFeedback.setUserStatus("Success");
		} catch (IOException e) {
			feFeedback.setError(e.getMessage());
			return ControllerUtils.EXPORT_CAMPAIGN;
		}
		
		boolean isImport = false;
		try {
			importExportService.initImportExportHelper(importExportHelper, isImport, ControllerUtils.EXPORT_CAMPAIGN);
		} catch (IOException e) {
			feFeedback.setError(e.getMessage());
		}
		return importExportHelper.getForwardingUrl();
	}
	
	@RequestMapping(value = "/importCampaign", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public String importCampaign(HttpSession session, final ImportExportHelper importExportHelper, final FeFeedback feFeedback) {
		boolean isImport = true;
		try {
			importExportService.initImportExportHelper(importExportHelper, isImport, ControllerUtils.IMPORT_CAMPAIGN);
		} catch (Exception e) {
			feFeedback.setError(e.getMessage());
			return ControllerUtils.IMPORT_CAMPAIGN;
		}

		feFeedback.setUserStatus("Select a campaign to import");
		return importExportHelper.getForwardingUrl();
	}
/*	
	@RequestMapping(value = "/importCampaign", method = RequestMethod.POST)
	@Secured({"ADMIN"})
	public String importCampaignPost(HttpSession session, final ImportExportHelper importExportHelper, final FeFeedback feFeedback) {
//		String campaignId = importExportHelper.getSelectedCampaignId();
//		if (campaignId == null) {
//			feFeedback.setError("You must select a campaign to import.");
//			return ControllerHelper.IMPORT_CAMPAIGN;
//		}
		
		importExportService.importCampaign(importExportHelper.getImportFilePath());
		feFeedback.setInfo("You have successfully imported " + importExportHelper.getSelectedCampaignName());
		feFeedback.setUserStatus("Success");
		
		return importExportHelper.getForwardingUrl();
	}
*/	

	  @RequestMapping(value = "/importCampaign", method = RequestMethod.POST)
	  public String handleFileUpload(
	      @RequestParam("file") MultipartFile file, 
	      final ImportExportHelper importExportHelper,  FeFeedback feFeedback, 
	      HttpServletRequest request) {	    

		  	if (!file.isEmpty()) {
		  		try {
		  			importExportService.importCampaignData(file);
		  			
		  			String fileName = importExportHelper.getImportFilename();
			        feFeedback.setUserStatus("Select a campaign file to import");
			        feFeedback.setInfo("You have successfully imported " + fileName);
			        return ControllerUtils.IMPORT_CAMPAIGN;
		  		} catch (Exception e) {
		  			System.out.println("FAILURE >>>>> File upload failed.");
			        e.printStackTrace();
			        return ControllerUtils.IMPORT_CAMPAIGN;
		  		}
		    } else {
		    	System.out.println("FAILURE >>>>> Upload file is empty");
			    return ControllerUtils.IMPORT_CAMPAIGN;
		    }
	  }

}

