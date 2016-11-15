package org.softwarewolf.gameserver.repository;

import org.softwarewolf.gameserver.domain.EmailSettings;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmailSettingsRepository extends MongoRepository<EmailSettings, String> {

}
