package org.softwarewolf.gameserver.service;

import java.io.File;
import java.util.List;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.softwarewolf.gameserver.domain.ApplicationSettings;
import org.softwarewolf.gameserver.domain.dto.ApplicationSettingsDto;
import org.softwarewolf.gameserver.repository.ApplicationSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationSettingsService {
	@Autowired
	private ApplicationSettingsRepository applicationSettingsRepository;

	private ApplicationSettings getApplicationSettings() {
    	List<ApplicationSettings> settingsList = applicationSettingsRepository.findAll();
    	ApplicationSettings settings = null;
    	if (settingsList.isEmpty()) {
    		settings = new ApplicationSettings();
    	} else {
    		settings = settingsList.get(0);
    	}
    	return settings;
	}
	
	public String getExportDir() {
		ApplicationSettings settings = getApplicationSettings();
    	return settings.getExportDir();
	}
	
	public void setExportDir(String exportDir) {
		if (!exportDir.endsWith("/")) {
			exportDir += "/";
		}
		File dir = new File(exportDir);
		if (!dir.exists()) {
			String message = ControllerUtils.getI18nMessage("settings.error.dirDoesNotExist");
			throw new RuntimeException(message);
		}
		
		ApplicationSettings settings = getApplicationSettings();
		settings.setExportDir(exportDir);
		applicationSettingsRepository.save(settings);
	}
	
	public void initApplicationSettingsDto(ApplicationSettingsDto applicationSettingsDto) {
		ApplicationSettings settings = getApplicationSettings();
		applicationSettingsDto.setExportDir(settings.getExportDir());
	}
}
