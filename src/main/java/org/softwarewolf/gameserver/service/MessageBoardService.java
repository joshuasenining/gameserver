package org.softwarewolf.gameserver.service;

import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.softwarewolf.gameserver.controller.utils.ControllerUtils;
import org.softwarewolf.gameserver.domain.GsMessage;
import org.softwarewolf.gameserver.domain.ItemSelector;
import org.softwarewolf.gameserver.domain.MessageBoard;
import org.softwarewolf.gameserver.domain.MessageBoardUser;
import org.softwarewolf.gameserver.domain.User;
import org.softwarewolf.gameserver.domain.dto.EditMessageBoardDto;
import org.softwarewolf.gameserver.domain.dto.MessageBoardDto;
import org.softwarewolf.gameserver.domain.dto.MessagePreview;
import org.softwarewolf.gameserver.repository.GsMessageRepository;
import org.softwarewolf.gameserver.repository.MessageBoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MessageBoardService implements Serializable {
	@Autowired
	private MessageBoardRepository messageBoardRepository;

	@Autowired
	private UserService userService;
	
	@Autowired
	private GsMessageRepository gsMessageRepository;
	
	private static final long serialVersionUID = 1L;

	public MessageBoard saveMessageBoard(EditMessageBoardDto editMessageBoardDto) throws Exception {
		MessageBoard messageBoard = editMessageBoardDto.getMessageBoard();
		messageBoard.setId(editMessageBoardDto.getSelectedMessageBoardId());
		// Put the values for the use permissions from the dto into the messageBoard
		usersFromDtoIntoMessageBoard(editMessageBoardDto);
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
		List<MessageBoard> allBoards = messageBoardRepository.findAll();
		List<String> allBoardsNameList = allBoards.stream().map(m -> m.getName()).collect(Collectors.toList());
		if (messageBoard.getId() == null && allBoardsNameList.contains(messageBoard.getName())) {
			String message = ControllerUtils.getI18nMessage("messageBoard.error.duplicateName");
			throw new RuntimeException(message);
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
	
	public EditMessageBoardDto initEditMessageBoardDto(String selectedMessageBoardId, EditMessageBoardDto editMessageBoardDto, boolean fromDb) {
		MessageBoard messageBoard = null;
		String userId = userService.getCurrentUserId();
		if (selectedMessageBoardId != null) {
			if (fromDb) {
				messageBoard = messageBoardRepository.findOne(selectedMessageBoardId);
				if (messageBoard == null) {
					String message = ControllerUtils.getI18nMessage("messageBoard.error.cantView");
					throw new RuntimeException(message);
				}
			} else {
				messageBoard = editMessageBoardDto.getMessageBoard();
				if (messageBoard == null) {
					messageBoard = new MessageBoard();
				}
				editMessageBoardDto.setIsOwner(Boolean.TRUE);
				messageBoard.addOwner(userId);
			}
			List<String> allUsers = new ArrayList<String>(messageBoard.getOwnerList());
			allUsers.addAll(messageBoard.getWriterList());
			allUsers.addAll(messageBoard.getReaderList());
			if (!allUsers.contains(userId)) {
				String message = ControllerUtils.getI18nMessage("messageBoard.error.cantView");
				throw new RuntimeException(message);
			}
			editMessageBoardDto.setSelectedMessageBoardId(selectedMessageBoardId);
			List<String> ownerIdList = messageBoard.getOwnerList();
			if (ownerIdList.contains(userService.getCurrentUserId())) {
				editMessageBoardDto.setIsOwner(Boolean.TRUE);
			} else {
				editMessageBoardDto.setIsOwner(Boolean.FALSE);
			}
		} else {
			messageBoard = new MessageBoard();
			editMessageBoardDto.setIsOwner(Boolean.TRUE);
			messageBoard.addOwner(userId);
		}
		
		editMessageBoardDto.setMessageBoard(messageBoard);
		editMessageBoardDto.setUsers(getMessageBoardUsers(messageBoard));
		editMessageBoardDto.setMessageBoardList(getAllViewableMessageBoards(userId));
		return editMessageBoardDto;
	}

	public MessageBoardDto initMessageBoardDto(String selectedMessageBoardId, MessageBoardDto messageBoardDto) {
		Long boardCount = messageBoardRepository.count();
		if (boardCount == 0L) {
			messageBoardDto.setIsFirstBoard(Boolean.TRUE);
		} else {
			messageBoardDto.setIsFirstBoard(Boolean.FALSE);
		}
		if (selectedMessageBoardId != null) {
			MessageBoard messageBoard = messageBoardRepository.findOne(selectedMessageBoardId);
			if (messageBoard != null) {
				messageBoardDto.setMessageBoardId(selectedMessageBoardId);
				messageBoardDto.setMessageBoardName(messageBoard.getName());
				if (messageBoard.getOwnerList().contains(userService.getCurrentUserId())) {
					messageBoardDto.setIsOwner(Boolean.TRUE);
				} else {
					messageBoardDto.setIsOwner(Boolean.FALSE);
				}
				
				List<MessagePreview> messagePreviewList = getMessagePreviewList(selectedMessageBoardId);
				if (!messagePreviewList.isEmpty()) {
					ObjectMapper mapper = new ObjectMapper();
					try {
						String preview = mapper.writeValueAsString(messagePreviewList);
						if (preview != null) {
							messageBoardDto.setMessagePreviewList(preview);
						}
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				messageBoardDto.setMessageBoardId(null);
				messageBoardDto.setMessageBoardName(null);
				messageBoardDto.setIsOwner(Boolean.TRUE);
			}
		}
		String subjectString = ControllerUtils.getI18nMessage("messageBoard.subject");
		messageBoardDto.setSubjectString(subjectString);
		String userId = userService.getCurrentUserId();
		messageBoardDto.setMessageBoardList(getAllViewableMessageBoards(userId));
		return messageBoardDto;
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

	// This method takes the values for user permissions from the EditMessageBoardDto and
	// puts them into the MessageBoard
	private void usersFromDtoIntoMessageBoard(EditMessageBoardDto editMessageBoardDto) {
		String usersString = editMessageBoardDto.getUsers();
		ObjectMapper mapper = new ObjectMapper();
		List<MessageBoardUser> messageBoardUserList = null;
		try {
			messageBoardUserList = mapper.readValue(usersString, new TypeReference<List<MessageBoardUser>>() {});
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		MessageBoard messageBoard = editMessageBoardDto.getMessageBoard();
		messageBoard.setOwnerList(null);
		messageBoard.setWriterList(null);
		messageBoard.setReaderList(null);
		// put values from EditMessageBoardDto into messageBoard
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
	
	public List<ItemSelector> getAllViewableMessageBoards(String userId) {
		List<MessageBoard> allMessageBoards = messageBoardRepository.findAll();
		List<ItemSelector> filteredBoards = allMessageBoards.stream().filter(m -> m.isOwner(userId))
				.map(m -> m.getItemSelector()).collect(Collectors.toList());
		
		return filteredBoards;
	}
	
	public List<MessageBoard> getAllEditableMessageBoards(String userId) {
		List<MessageBoard> allowedMessageBoards = messageBoardRepository.findAllByOwnerList(userId);

		return allowedMessageBoards;
	}
	
	public MessageBoard findOne(String id) {
		return messageBoardRepository.findOne(id);
	}
	
	public void deleteByName(String name) {
		messageBoardRepository.deleteByName(name);
	}
	
	public void delete(MessageBoard messageBoard) {
		messageBoardRepository.delete(messageBoard);
	}

	public List<MessagePreview> getMessagePreviewList(String messageBoardId) {
		List<GsMessage> messageList = gsMessageRepository.findAllByMessageBoardId(messageBoardId);
		List<MessagePreview> previewList = messageList.stream().map(m -> m.createPreview()).collect(Collectors.toList());
		return previewList;
	}
	
	public GsMessage saveGsMessage(MessageBoardDto messageBoardDto) {
		GsMessage gsMessage = new GsMessage();
		gsMessage.setId(messageBoardDto.getMessageId());
		gsMessage.setThreadId(messageBoardDto.getThreadId());
		gsMessage.setCreated(Instant.now());
		gsMessage.setParentId(messageBoardDto.getMessageParentId());
		gsMessage.setMessage(messageBoardDto.getMessageContent());
		gsMessage.setMessageBoardId(messageBoardDto.getMessageBoardId());
		gsMessage.setPosterId(userService.getCurrentUserId());
		gsMessage.setPosterName(userService.getCurrentUserName());
		gsMessage.setSubject(messageBoardDto.getMessageSubject());
		gsMessage = gsMessageRepository.save(gsMessage);
		if (gsMessage.getParentId() == null) { 
			gsMessage.setThreadId(gsMessage.getId());
		}
		gsMessageRepository.save(gsMessage);
		return gsMessage;
	}
}
