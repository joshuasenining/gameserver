package org.softwarewolf.gameserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.softwarewolf.gameserver.controller.utils.FeFeedback;
import org.softwarewolf.gameserver.domain.MessageBoard;
import org.softwarewolf.gameserver.domain.dto.EditMessageBoardDto;
import org.softwarewolf.gameserver.domain.dto.MessageBoardDto;
import org.softwarewolf.gameserver.service.MessageBoardService;

@Controller
public class MessageBoardController {
	@Autowired
	protected MessageBoardService messageBoardService;
	
	@RequestMapping(value = "/admin/editMessageBoard", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public String editMessageBoard(EditMessageBoardDto editMessageBoardDto, final FeFeedback feFeedback) {
		return editMessageBoardParent(editMessageBoardDto, null, feFeedback);
	}
	
	@RequestMapping(value = "/admin/editMessageBoard/{messageBoardId}", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public String editMessageBoardWithId(final EditMessageBoardDto editMessageBoardDto, 
			@PathVariable String messageBoardId, final FeFeedback feFeedback) {
		return editMessageBoardParent(editMessageBoardDto, messageBoardId, feFeedback);
	}
	
	private String editMessageBoardParent(EditMessageBoardDto editMessageBoardDto, String messageBoardId, FeFeedback feFeedback) {
		boolean fromDb = true;
		if (messageBoardId == null) {
			fromDb = false;
		}
		messageBoardService.initEditMessageBoardDto(messageBoardId, editMessageBoardDto, fromDb);
		editMessageBoardDto.setForwardingUrl(ControllerUtils.EDIT_MESSAGE_BOARD);
		
		String message = null;
		if (messageBoardId == null) {
			message = ControllerUtils.getI18nMessage("editMessageBoard.status.creatingNew");
			feFeedback.setUserStatus(message);
		} else {
			message = ControllerUtils.getI18nMessage("editMessageBoard.status.editing");
			feFeedback.setUserStatus(message + " '" + editMessageBoardDto.getMessageBoard().getName() + "'");
		}
		return editMessageBoardDto.getForwardingUrl();
	}
	
	
	@RequestMapping(value = "/admin/editMessageBoard", method = RequestMethod.POST)
	@Secured({"ADMIN","USER"})
	public String postEditPage(EditMessageBoardDto editMessageBoardDto, 
			final FeFeedback feFeedback) {
		String editingMsg = ControllerUtils.getI18nMessage("editMessageBoard.status.editing");
		MessageBoard messageBoard = null;
		try {
			messageBoard = messageBoardService.saveMessageBoard(editMessageBoardDto);
			boolean fromDb = true;
			messageBoardService.initEditMessageBoardDto(messageBoard.getId(), editMessageBoardDto, fromDb);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			feFeedback.setError(errorMessage);
			feFeedback.setUserStatus(editingMsg + " " +(messageBoard == null ? "" : messageBoard.getName()));
			boolean fromDb = true;
			editMessageBoardDto = messageBoardService.initEditMessageBoardDto(editMessageBoardDto.getMessageBoard().getId(), editMessageBoardDto, fromDb);
			return ControllerUtils.EDIT_MESSAGE_BOARD;
		}
		String modified = ControllerUtils.getI18nMessage("editMessageBoard.modified");
		feFeedback.setInfo(modified + " " + messageBoard.getName());
		feFeedback.setUserStatus(editingMsg + " " + messageBoard.getName());
		return ControllerUtils.EDIT_MESSAGE_BOARD;
	}	
	
	@RequestMapping(value = "/shared/viewMessageBoard", method = RequestMethod.GET)
	@Secured({"ADMIN","USER"})
	public String viewMessageBoard(MessageBoardDto messageBoardDto, 
			final FeFeedback feFeedback) {
		return viewMessageBoardParent(messageBoardDto, null, feFeedback);
	}
	
	@RequestMapping(value = "/shared/viewMessageBoard/{messageBoardId}", method = RequestMethod.GET)
	@Secured({"ADMIN", "GAMEMASTER", "USER"})
	public String selectMessage(MessageBoardDto messageBoardDto, 
			@PathVariable String messageBoardId, FeFeedback feFeedback) {
		return viewMessageBoardParent(messageBoardDto, messageBoardId, feFeedback);
	}
	
	private String viewMessageBoardParent(MessageBoardDto messageBoardDto, String messageBoardId, FeFeedback feFeedback) {
		try {
			messageBoardService.initMessageBoardDto(messageBoardId, messageBoardDto);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			feFeedback.setError(errorMessage);
			String editingMsg = ControllerUtils.getI18nMessage("messageBoard.select");
			feFeedback.setUserStatus(editingMsg);
			return ControllerUtils.VIEW_MESSAGE_BOARD;
		}
		String message = null;
		if (messageBoardId == null) {
			message = ControllerUtils.getI18nMessage("messageBoard.status.pick");
			feFeedback.setUserStatus(message);
		} else {
			message = ControllerUtils.getI18nMessage("messageBoard");
			feFeedback.setUserStatus(message + " '" + messageBoardDto.getMessageBoardName() + "'");
		}
		return ControllerUtils.VIEW_MESSAGE_BOARD;
	}
	
	@RequestMapping(value = "/shared/postToMessageBoard", method = RequestMethod.POST)
	@Secured({"ADMIN", "GAMEMASTER", "USER"})
	public String postMessage(MessageBoardDto messageBoardDto, FeFeedback feFeedback) {
		messageBoardService.saveGsMessage(messageBoardDto);
		messageBoardService.initMessageBoardDto(messageBoardDto.getMessageBoardId(), messageBoardDto);
		messageBoardDto.setForwardingUrl(ControllerUtils.VIEW_MESSAGE_BOARD);
		
		String info = ControllerUtils.getI18nMessage("messageBoard.status.success");
		feFeedback.setInfo(info);
		String messageBoard = ControllerUtils.getI18nMessage("messageBoard");
		feFeedback.setUserStatus(messageBoard + ": " + messageBoardDto.getMessageBoardName());
		return ControllerUtils.VIEW_MESSAGE_BOARD;
	}	

	@RequestMapping(value = "/shared/readFullMessage/{messageId}", method = RequestMethod.GET)
	@Secured({"ADMIN", "GAMEMASTER", "USER"})
	public String readFullMessage(MessageBoardDto messageBoardDto, @PathVariable String messageId, FeFeedback feFeedback) {
		messageBoardService.initMessage(messageId, messageBoardDto);
		messageBoardDto.setForwardingUrl(ControllerUtils.VIEW_MESSAGE_BOARD);
		
		feFeedback.setInfo(null);
		String messageBoard = ControllerUtils.getI18nMessage("messageBoard");
		feFeedback.setUserStatus(messageBoard + ": " + messageBoardDto.getMessageBoardName());
		return ControllerUtils.VIEW_MESSAGE_BOARD;
	}	

	@RequestMapping(value = "/shared/replyToMessage/{messageId}", method = RequestMethod.GET)
	@Secured({"ADMIN", "GAMEMASTER", "USER"})
	public String replyToMessage(MessageBoardDto messageBoardDto, @PathVariable String messageId, FeFeedback feFeedback) {
		// TODO: fix for reply
		messageBoardService.initMessage(messageId, messageBoardDto);
		messageBoardDto.setForwardingUrl(ControllerUtils.VIEW_MESSAGE_BOARD);
		
		feFeedback.setInfo(null);
		String messageBoard = ControllerUtils.getI18nMessage("messageBoard");
		feFeedback.setUserStatus(messageBoard + ": " + messageBoardDto.getMessageBoardName());
		
		return ControllerUtils.VIEW_MESSAGE_BOARD;
	}	
	
}