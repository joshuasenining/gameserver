package org.softwarewolf.gameserver.repository;

import java.util.List;

import org.softwarewolf.gameserver.domain.GsMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GsMessageRepository extends MongoRepository<GsMessage, String> {
	
	List<GsMessage> findAllByThreadId(String threadId);
	
	List<GsMessage> findAllByParentId(String parentId);
}
