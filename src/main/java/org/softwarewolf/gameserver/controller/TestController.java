package org.softwarewolf.gameserver.controller;

import org.softwarewolf.gameserver.controller.helper.ControllerUtils;
import org.softwarewolf.gameserver.controller.helper.FeFeedback;
import org.softwarewolf.gameserver.service.EncodingService;
import org.softwarewolf.gameserver.service.GameMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/test")
public class TestController {
	@Autowired
	private GameMailService gameMailService;
	
	@Autowired
	private EncodingService encodingService;
	
	@RequestMapping(value = "/testMail", method = RequestMethod.GET)
	@Secured({"USER","GAMEMASTER"})
	public String testMail(FeFeedback feFeedback) {
		gameMailService.testMail(feFeedback);
/*
		String testText = "test text";
		String encryptedText = null;
		try {
			encryptedText = encodingService.encrypt(testText);
			String decryptedText = encodingService.decrypt(encryptedText);
			if (decryptedText.equals(testText)) {
				System.out.println("Yes!");
			} else {
				System.out.println("No!");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		return ControllerUtils.CAMPAIGN_HOME;
	}
}
