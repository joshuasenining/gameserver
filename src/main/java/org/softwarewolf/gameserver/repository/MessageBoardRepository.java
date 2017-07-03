package org.softwarewolf.gameserver.repository;

import java.util.List;

import org.softwarewolf.gameserver.domain.MessageBoard;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageBoardRepository extends MongoRepository<MessageBoard, String> {
	public MessageBoard findOneById(String id);
	public List<MessageBoard> findAllByOwnerList(String owner);
	public List<MessageBoard> findAllByWriterList(String writer);
	public List<MessageBoard> findAllByReaderList(String reader);
	
	public void deleteByName(String name);
}
