package org.softwarewolf.gameserver.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.softwarewolf.gameserver.controller.utils.FeFeedback;
import org.softwarewolf.gameserver.domain.MessageBoard;
import org.softwarewolf.gameserver.domain.dto.MessageBoardDto;
import org.softwarewolf.gameserver.service.MessageBoardService;

@Controller
@RequestMapping("/shared")
public class MessageBoardController {
	@Autowired
	protected MessageBoardService messageBoardService;
	
	@RequestMapping(value = "/messageBoards", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public String editMessageBoard(HttpSession session, MessageBoardDto messageBoardDto, final FeFeedback feFeedback) {
		messageBoardDto = messageBoardService.initMessageBoardDto(messageBoardDto);
		messageBoardDto.setForwardingUrl(ControllerUtils.MESSAGE_BOARD);
		String message = ControllerUtils.getI18nMessage("editMessageBoard.status.creatingNew");
		feFeedback.setUserStatus(message);
		return ControllerUtils.MESSAGE_BOARD;
	}
	
	@RequestMapping(value = "/messageBoards/{messageBoardId}", method = RequestMethod.GET)
	@Secured({"ADMIN"})
	public String editMessageBoardWithId(HttpSession session, final MessageBoardDto messageBoardDto, 
			@PathVariable String messageBoardId, final FeFeedback feFeedback) {
		messageBoardService.initMessageBoardDto(messageBoardId, messageBoardDto);
		messageBoardDto.setForwardingUrl(ControllerUtils.MESSAGE_BOARD);
		
		String message = ControllerUtils.getI18nMessage("editMessageBoard.status.editing");
		feFeedback.setUserStatus(message + " '" + messageBoardDto.getMessageBoard().getName() + "'");
		return ControllerUtils.MESSAGE_BOARD;
	}
	
	@RequestMapping(value = "/messageBoards", method = RequestMethod.POST)
	@Secured({"ADMIN"})
	public String postEditPage(HttpSession session, MessageBoardDto messageBoardDto, 
			final FeFeedback feFeedback) {
		String editingMsg = ControllerUtils.getI18nMessage("editMessageBoard.status.editing");
		MessageBoard messageBoard = null;
		try {
			messageBoard = messageBoardService.saveMessageBoard(messageBoardDto);
			messageBoardService.initMessageBoardDto(messageBoard.getId(), messageBoardDto);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			feFeedback.setError(errorMessage);
			feFeedback.setUserStatus(editingMsg + " " +(messageBoard == null ? "" : messageBoard.getName()));
			messageBoardDto = messageBoardService.initMessageBoardDto(messageBoardDto);
			return ControllerUtils.MESSAGE_BOARD;
		}
		String modified = ControllerUtils.getI18nMessage("editMessageBoard.modified");
		feFeedback.setInfo(modified + " " + messageBoard.getName());
		feFeedback.setUserStatus(editingMsg + " " + messageBoard.getName());
		return ControllerUtils.MESSAGE_BOARD;
	}	
}