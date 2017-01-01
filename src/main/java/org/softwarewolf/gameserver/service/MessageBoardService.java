package org.softwarewolf.gameserver.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.softwarewolf.gameserver.domain.MessageBoard;
import org.softwarewolf.gameserver.domain.MessageBoardUser;
import org.softwarewolf.gameserver.domain.User;
import org.softwarewolf.gameserver.domain.dto.MessageBoardDescriptor;
import org.softwarewolf.gameserver.domain.dto.MessageBoardDto;
import org.softwarewolf.gameserver.repository.MessageBoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MessageBoardService implements Serializable {
	@Autowired
	private MessageBoardRepository messageBoardRepository;

	@Autowired
	private UserService userService;
	
	private static final long serialVersionUID = 1L;

	public MessageBoard saveMessageBoard(MessageBoardDto messageBoardDto) throws Exception {
		MessageBoard messageBoard = messageBoardDto.getMessageBoard();
		// Put the values for the use permissions from the dto into the messageBoard
		usersFromDtoIntoMessageBoard(messageBoardDto);
		validateMessageBoard(messageBoard);
				
		return save(messageBoard);
	}
	
	public MessageBoard save(MessageBoard messageBoard) throws Exception {
		String messageBoardId = messageBoard.getId();
		if (messageBoardId != null && messageBoardId.isEmpty()) {
			messageBoard.setId(null);
		}
		if (messageBoard.getOwnerList() == null || messageBoard.getOwnerList().isEmpty()) {
			String ownerId = userService.getCurrentUserId();
			messageBoard.addOwner(ownerId);
		}
		String errorList = validateMessageBoard(messageBoard);
		if (errorList.length() > 0) {
			throw new Exception(errorList);
		}
		return messageBoardRepository.save(messageBoard);
	}
	
	public String validateMessageBoard(MessageBoard messageBoard) {
		String errorList = "";
		if (messageBoard == null) {
			return ControllerUtils.getI18nMessage("editMessageBoard.error.nullMessageBoard");
		}
		if (messageBoard.getName() == null || messageBoard.getName().isEmpty()) {
			errorList = ControllerUtils.getI18nMessage("editMessageBoard.error.emptyTitle");
		}
		if (messageBoard.getDescription() == null || messageBoard.getDescription().isEmpty()) {
			if (errorList.length() > 0) {
				errorList += "\n";
			}
			errorList += ControllerUtils.getI18nMessage("editMessageBoard.error.emptyContent");
		}
		return errorList;
	}
	
	public List<MessageBoard> findAll() {
		return messageBoardRepository.findAll();
	}
	
	public MessageBoardDto initMessageBoardDto(MessageBoardDto messageBoardDto) {
		String messageBoardId = messageBoardDto.getMessageBoard().getId();
		MessageBoard messageBoard = initMessageBoard(messageBoardId);
		messageBoardDto.setIsOwner(Boolean.TRUE);
		messageBoardDto.setMessageBoard(messageBoard);
		messageBoardDto.setUsers(getMessageBoardUsers(messageBoard));
		String userId = userService.getCurrentUserId();
		messageBoardDto.setMessageBoardDescriptorList(this.getAllViewableMessageBoardDescritptorList(userId));
		return messageBoardDto;
	}

	public MessageBoardDto initMessageBoardDto(String messageBoardId, MessageBoardDto messageBoardDto) {
		MessageBoard messageBoard = initMessageBoard(messageBoardId);
		messageBoardDto.setMessageBoard(messageBoard);
		
		return initMessageBoardDto(messageBoardDto);
	}
	private String getMessageBoardUsers(MessageBoard messageBoard) {
		List<User> userList = userService.findAll();
		List<MessageBoardUser> messageBoardUsers = new ArrayList<>();
		for (User user : userList) {
			String userId = user.getId();
			String username = user.getUsername();
			if (messageBoard.getOwnerList().contains(userId)) {
				messageBoardUsers.add(new MessageBoardUser(ControllerUtils.PERMISSION_OWNER, userId, username));
			} else if (messageBoard.getWriterList().contains(userId)) {
				messageBoardUsers.add(new MessageBoardUser(ControllerUtils.PERMISSION_READ_WRITE, userId, username));
			} else if (messageBoard.getReaderList().contains(userId)) {
				messageBoardUsers.add(new MessageBoardUser(ControllerUtils.PERMISSION_READ, userId, username));
			} else {
				messageBoardUsers.add(new MessageBoardUser(ControllerUtils.NO_ACCESS, userId, username));
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		String userString = null;
		try {
			userString = mapper.writeValueAsString(messageBoardUsers);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			String message = ControllerUtils.getI18nMessage("editMessageBoard.error.couldNotProcessUsers");
			throw new RuntimeException(message);
		}
		return userString;
	}
	
	private MessageBoard initMessageBoard(String messageBoardId) {
		MessageBoard messageBoard = null;
		if (messageBoardId != null) {
			messageBoard = messageBoardRepository.findOne(messageBoardId);
		}
		if (messageBoard == null) {
			messageBoard = new MessageBoard();
			messageBoard.addOwner(userService.getCurrentUserId());
		} 
		
		return messageBoard;
	}

	// This method takes the values for user permissions from the MessageBoardDto and
	// puts them into the MessageBoard
	private void usersFromDtoIntoMessageBoard(MessageBoardDto messageBoardDto) {
		String usersString = messageBoardDto.getUsers();
		ObjectMapper mapper = new ObjectMapper();
		List<MessageBoardUser> messageBoardUserList = null;
		try {
			messageBoardUserList = mapper.readValue(usersString, new TypeReference<List<MessageBoardUser>>() {});
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		MessageBoard messageBoard = messageBoardDto.getMessageBoard();
		messageBoard.setOwnerList(null);
		messageBoard.setWriterList(null);
		messageBoard.setReaderList(null);
		// put values from MessageBoardDto into messageBoard
		if (messageBoardUserList != null) {
			for (MessageBoardUser mbu : messageBoardUserList) {
				String id = mbu.getUserId();
				String permission = mbu.getPermission();
				if (ControllerUtils.PERMISSION_OWNER.equals(permission)) {
					messageBoard.addOwner(id);
				} else if (ControllerUtils.PERMISSION_READ_WRITE.equals(permission)) {
					messageBoard.addWriter(id);
				} else if (ControllerUtils.PERMISSION_READ.equals(permission)) {
					messageBoard.addReader(id);
				}
			}
		} else {
			// No values, current user = owner
			String ownerId = userService.getCurrentUserId();
			messageBoard.addOwner(ownerId);
		}
	}
	
	public void deleteAll() {
		messageBoardRepository.deleteAll();
	}
	
	public List<MessageBoardDescriptor> getAllViewableMessageBoardDescritptorList(String userId) {
		List<MessageBoard> allMessageBoards = messageBoardRepository.findAll();
		List<MessageBoard> filteredBoards = allMessageBoards.stream().filter(m -> m.isOwner(userId)).collect(Collectors.toList());
		List<MessageBoardDescriptor> descriptorList = new ArrayList<>();
		for (MessageBoard board : filteredBoards) {
			descriptorList.add(board.createDescriptor());
		}
		
		return descriptorList;
	}
	
	public List<MessageBoard> getAllEditableMessageBoards(String userId) {
		List<MessageBoard> allowedMessageBoards = messageBoardRepository.findAllByOwnerList(userId);

		return allowedMessageBoards;
	}
	
	public MessageBoard findOne(String id) {
		return messageBoardRepository.findOne(id);
	}
	
	private String convertMessageBoardDescriptorListToString(List<MessageBoardDescriptor> messageBoardDescriptorList) {
		String listAsString = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			listAsString = mapper.writeValueAsString(messageBoardDescriptorList);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listAsString;
	}
	
	public void deleteByName(String name) {
		messageBoardRepository.deleteByName(name);
	}
	
	public void delete(MessageBoard messageBoard) {
		messageBoardRepository.delete(messageBoard);
	}

}
