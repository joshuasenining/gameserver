package org.softwarewolf.gameserver.controller.utils;

import java.util.Locale;

import org.softwarewolf.gameserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class ControllerUtils {
	private static UserService userService;	
	@Autowired
	private void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	private static MessageSource messageSource;	
	@Autowired
	private void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public static final String CAMPAIGN_HOME = "/gamemaster/campaignHome";
	public static final String CREATE_ACCOUNT = "/public/createAccount";
	public static final String CREATE_ROLE = "/admin/createRole";
	public static final String CHANGE_EMAIL = "/shared/changeEmail";
	public static final String DELETE_CAMPAIGN = "/admin/deleteCampaign";
	public static final String DELETE_ROLE = "/admin/deleteRole";
	public static final String EDIT_CAMPAIGN = "/gamemaster/editCampaign";
	public static final String EDIT_FOLIO = "/shared/editFolio";
	public static final String EDIT_MESSAGE_BOARD = "/admin/editMessageBoard";
	public static final String EMAIL_SETTINGS = "/admin/emailSettings";
	public static final String EXPORT_CAMPAIGN = "/admin/exportCampaign";
	public static final String GET_FOLIO = "/shared/getFolio";
	public static final String IMPORT_CAMPAIGN = "/admin/importCampaign";
	public static final String LOGIN = "/login";
	public static final String MESSAGE_BOARD = "/shared/messageBoard";
	public static final String RESET_PASSWORD = "/shared/resetPassword";
	public static final String SELECT_CAMPAIGN = "/shared/selectCampaign";
	public static final String SELECT_FOLIO = "/shared/selectFolio";
	public static final String SETTINGS = "/admin/settings";
	public static final String EDIT_USER = "/admin/editUser";
	public static final String USER_MENU = "/shared/menu";
	public static final String VIEW_FOLIO = "/shared/viewFolio";
	public static final String VIEW_CAMPAIGN_INFO = "/shared/viewCampaignInfo";
	
	public static final String CAMPAIGN_ID = "campaignId";
	
	public static final String ADMIN_TYPE = "admin";
	public static final String GM_TYPE = "gm";
	public static final String PLAYER_TYPE = "player";
	
	public static final String ROLE_OWNER = "ROLE_OWNER";
	public static final String ROLE_USER = "ROLE_USER";
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_GAMEMASTER = "ROLE_GAMEMASTER";
	public static final String PERMISSION_OWNER = "PERMISSION_OWNER";
	public static final String PERMISSION_GAMEMASTER = "PERMISSION_GAMEMASTER";
	public static final String PERMISSION_PLAYER = "PERMISSION_PLAYER";
	public static final String PERMISSION_READ_WRITE = "PERMISSION_READ_WRITE";
	public static final String PERMISSION_READ = "PERMISSION_READ";
	public static final String OWNER = "Owner";
	public static final String READ = "Read";
	public static final String NO_ACCESS = "No Access";

	public static String getI18nMessage(String key) {
		Locale locale = userService.getCurrentUserLocale();
		return messageSource.getMessage(key, null, locale);
	}
}
