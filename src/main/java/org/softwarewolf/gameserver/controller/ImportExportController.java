package org.softwarewolf.gameserver.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.softwarewolf.gameserver.service.ImportExportService;
import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.softwarewolf.gameserver.controller.utils.FeFeedback;
import org.softwarewolf.gameserver.domain.dto.ImportExportSelectorDto;

@Controller
@RequestMapping(value = "/admin")
public class ImportExportController {
	@Autowired
	private ImportExportService importExportService;

	@RequestMapping(value = "/exportCampaign", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public String exportCampaign(HttpSession session, ImportExportSelectorDto importExportSelectorDto,
			final FeFeedback feFeedback) {

		boolean isImport = false;
		try {
			importExportService.initImportExportBundle(importExportSelectorDto, isImport, ControllerUtils.EXPORT_CAMPAIGN);
		} catch (IOException e) {
			feFeedback.setError(e.getMessage());
			return ControllerUtils.EXPORT_CAMPAIGN;
		}
		String message = ControllerUtils.getI18nMessage("exportCampaign.status");
		feFeedback.setUserStatus(message);

		return importExportSelectorDto.getForwardingUrl();
	}
	
	@RequestMapping(value = "/exportCampaign", method = RequestMethod.POST)
	@Secured({"ADMIN"})
	public String exportCampaignPost(HttpSession session, final ImportExportSelectorDto importExportSelectorDto, 
			final FeFeedback feFeedback) {
		String backupFile;
		try {
			String campaignId = importExportSelectorDto.getSelectedCampaignId();
			backupFile = importExportService.exportCampaign(campaignId);
			feFeedback.setError(null);
			String info = ControllerUtils.getI18nMessage("exportCampaign.success") + " " + backupFile;
			feFeedback.setInfo(info);
			String status = ControllerUtils.getI18nMessage("exportCampaign.status");
			feFeedback.setUserStatus(status);
		} catch (IOException e) {
			feFeedback.setError(e.getMessage());
			String status = ControllerUtils.getI18nMessage("exportCampaign.status");
			feFeedback.setUserStatus(status);
			return ControllerUtils.EXPORT_CAMPAIGN;
		}
		
		boolean isImport = false;
		try {
			importExportService.initImportExportBundle(importExportSelectorDto, isImport, ControllerUtils.EXPORT_CAMPAIGN);
		} catch (IOException e) {
			feFeedback.setError(e.getMessage());
		}
		return importExportSelectorDto.getForwardingUrl();
	}
	
	@RequestMapping(value = "/importCampaign", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public String importCampaign(HttpSession session, final ImportExportSelectorDto importExportSelectorDto, final FeFeedback feFeedback) {
		boolean isImport = true;
		try {
			importExportService.initImportExportBundle(importExportSelectorDto, isImport, ControllerUtils.IMPORT_CAMPAIGN);
		} catch (Exception e) {
			feFeedback.setError(e.getMessage());
			return ControllerUtils.IMPORT_CAMPAIGN;
		}

		String status = ControllerUtils.getI18nMessage("importCampaign.selectFile");
		feFeedback.setUserStatus(status);
		return importExportSelectorDto.getForwardingUrl();
	}


	@RequestMapping(value = "/importCampaign", method = RequestMethod.POST)
	public String handleFileUpload(@RequestParam("file") MultipartFile file, 
			final ImportExportSelectorDto importExportSelectorDto,  FeFeedback feFeedback, 
			HttpServletRequest request) {	    
	  	if (!file.isEmpty()) {
	  		try {
	  			importExportService.importCampaignData(file);
	  			
	  			String filePath = importExportSelectorDto.getFilePath();
	  			String status = ControllerUtils.getI18nMessage("importCampaign.selectFile");
		        feFeedback.setUserStatus(status);
		        String info = ControllerUtils.getI18nMessage("importCampaign.success");
		        feFeedback.setInfo(info + " " + filePath);
		        return ControllerUtils.IMPORT_CAMPAIGN;
	  		} catch (Exception e) {
	  			e.printStackTrace();
	  			feFeedback.setError(e.getMessage());
	  			String status = ControllerUtils.getI18nMessage("importCampaign.selectFile");
	  			feFeedback.setUserStatus(status);
	  			return ControllerUtils.IMPORT_CAMPAIGN;
	  		}
	  	} else {
	  		System.out.println("FAILURE >>>>> Upload file is empty");
		    return ControllerUtils.IMPORT_CAMPAIGN;
	    }
	}
}

