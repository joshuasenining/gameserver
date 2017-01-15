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
import org.softwarewolf.gameserver.service.MessageBoardService;

@Controller
public class MessageBoardController {
	@Autowired
	protected MessageBoardService messageBoardService;
	
	@RequestMapping(value = "/admin/editMessageBoard", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public String editMessageBoard(EditMessageBoardDto editMessageBoardDto, final FeFeedback feFeedback) {
		editMessageBoardDto = messageBoardService.initMessageBoardDto(editMessageBoardDto);
		editMessageBoardDto.setForwardingUrl(ControllerUtils.MESSAGE_BOARD);
		String message = ControllerUtils.getI18nMessage("editMessageBoard.status.creatingNew");
		feFeedback.setUserStatus(message);
		return ControllerUtils.EDIT_MESSAGE_BOARD;
	}
	
	@RequestMapping(value = "/admin/editMessageBoard/{messageBoardId}", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public String editMessageBoardWithId(final EditMessageBoardDto editMessageBoardDto, 
			@PathVariable String messageBoardId, final FeFeedback feFeedback) {
		messageBoardService.initMessageBoardDto(messageBoardId, editMessageBoardDto);
		editMessageBoardDto.setForwardingUrl(ControllerUtils.EDIT_MESSAGE_BOARD);
		
		String message = ControllerUtils.getI18nMessage("editMessageBoard.status.editing");
		feFeedback.setUserStatus(message + " '" + editMessageBoardDto.getMessageBoard().getName() + "'");
		return ControllerUtils.EDIT_MESSAGE_BOARD;
	}
	
	@RequestMapping(value = "/admin/editMessageBoard", method = RequestMethod.POST)
	@Secured({"ADMIN"})
	public String postEditPage(EditMessageBoardDto editMessageBoardDto, 
			final FeFeedback feFeedback) {
		String editingMsg = ControllerUtils.getI18nMessage("editMessageBoard.status.editing");
		MessageBoard messageBoard = null;
		try {
			messageBoard = messageBoardService.saveMessageBoard(editMessageBoardDto);
			messageBoardService.initMessageBoardDto(messageBoard.getId(), editMessageBoardDto);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			feFeedback.setError(errorMessage);
			feFeedback.setUserStatus(editingMsg + " " +(messageBoard == null ? "" : messageBoard.getName()));
			editMessageBoardDto = messageBoardService.initMessageBoardDto(editMessageBoardDto);
			return ControllerUtils.EDIT_MESSAGE_BOARD;
		}
		String modified = ControllerUtils.getI18nMessage("editMessageBoard.modified");
		feFeedback.setInfo(modified + " " + messageBoard.getName());
		feFeedback.setUserStatus(editingMsg + " " + messageBoard.getName());
		return ControllerUtils.EDIT_MESSAGE_BOARD;
	}	
	
	@RequestMapping(value = "/shared/viewMessageBoard", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public String viewMessageBoard(EditMessageBoardDto editMessageBoardDto, 
			final FeFeedback feFeedback) {
		String editingMsg = ControllerUtils.getI18nMessage("editMessageBoard.status.editing");
		MessageBoard messageBoard = null;
		try {
			messageBoard = messageBoardService.saveMessageBoard(editMessageBoardDto);
			messageBoardService.initMessageBoardDto(messageBoard.getId(), editMessageBoardDto);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			feFeedback.setError(errorMessage);
			feFeedback.setUserStatus(editingMsg + " " +(messageBoard == null ? "" : messageBoard.getName()));
			editMessageBoardDto = messageBoardService.initMessageBoardDto(editMessageBoardDto);
			return ControllerUtils.VIEW_MESSAGE_BOARD;
		}
		String pick = ControllerUtils.getI18nMessage("messageboard.status.pick");
		feFeedback.setUserStatus(pick);
		return ControllerUtils.VIEW_MESSAGE_BOARD;
	}
	
	@RequestMapping(value = "/shared/viewMessageBoard/{messageBoardId}", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public String selectMessageBoard(final EditMessageBoardDto editMessageBoardDto, 
			@PathVariable String messageBoardId, final FeFeedback feFeedback) {
		messageBoardService.initMessageBoardDto(messageBoardId, editMessageBoardDto);
		editMessageBoardDto.setForwardingUrl(ControllerUtils.VIEW_MESSAGE_BOARD);
		
		String message = ControllerUtils.getI18nMessage("editMessageBoard.status.editing");
		feFeedback.setUserStatus(message + " '" + editMessageBoardDto.getMessageBoard().getName() + "'");
		return ControllerUtils.VIEW_MESSAGE_BOARD;
	}
}