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
		boolean fromDb = true;
		editMessageBoardDto = messageBoardService.initEditMessageBoardDto(null, editMessageBoardDto, fromDb);
		editMessageBoardDto.setForwardingUrl(ControllerUtils.MESSAGE_BOARD);
		String message = ControllerUtils.getI18nMessage("editMessageBoard.status.creatingNew");
		feFeedback.setUserStatus(message);
		return ControllerUtils.EDIT_MESSAGE_BOARD;
	}
	
	@RequestMapping(value = "/admin/editMessageBoard/{messageBoardId}", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public String editMessageBoardWithId(final EditMessageBoardDto editMessageBoardDto, 
			@PathVariable String messageBoardId, final FeFeedback feFeedback) {
		boolean fromDb = true;
		messageBoardService.initEditMessageBoardDto(messageBoardId, editMessageBoardDto, fromDb);
		editMessageBoardDto.setForwardingUrl(ControllerUtils.EDIT_MESSAGE_BOARD);
		
		String message = ControllerUtils.getI18nMessage("editMessageBoard.status.editing");
		feFeedback.setUserStatus(message + " '" + editMessageBoardDto.getMessageBoard().getName() + "'");
		return ControllerUtils.EDIT_MESSAGE_BOARD;
	}
	
	@RequestMapping(value = "/admin/editMessageBoard", method = RequestMethod.POST)
	@Secured({"ADMIN","USER"})
	public String postEditPage(EditMessageBoardDto editMessageBoardDto, 
			final FeFeedback feFeedback) {
		String editingMsg = ControllerUtils.getI18nMessage("editMessageBoard.status.editing");
		MessageBoard messageBoard = null;
		try {
			messageBoard = messageBoardService.saveMessageBoard(editMessageBoardDto);
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
		try {
			messageBoardService.initMessageBoardDto(null, messageBoardDto);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			feFeedback.setError(errorMessage);
			String editingMsg = ControllerUtils.getI18nMessage("messageBoard.select");
			feFeedback.setUserStatus(editingMsg);
			return ControllerUtils.VIEW_MESSAGE_BOARD;
		}
		String pick = ControllerUtils.getI18nMessage("messageBoard.status.pick");
		feFeedback.setUserStatus(pick);
		return ControllerUtils.VIEW_MESSAGE_BOARD;
	}
	
	@RequestMapping(value = "/shared/viewMessageBoard/{messageBoardId}", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public String selectMessageBoard(MessageBoardDto messageBoardDto, 
			@PathVariable String messageBoardId, final FeFeedback feFeedback) {
		messageBoardService.initMessageBoardDto(messageBoardId, messageBoardDto);
		messageBoardDto.setForwardingUrl(ControllerUtils.VIEW_MESSAGE_BOARD);
		
		String message = ControllerUtils.getI18nMessage("editMessageBoard.status.editing");
		feFeedback.setUserStatus(message + " '" + messageBoardDto.getMessageBoardName() + "'");
		return ControllerUtils.VIEW_MESSAGE_BOARD;
	}
}