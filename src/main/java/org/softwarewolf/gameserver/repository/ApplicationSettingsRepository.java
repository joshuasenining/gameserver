package org.softwarewolf.gameserver.repository;

import org.softwarewolf.gameserver.domain.ApplicationSettings;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApplicationSettingsRepository extends MongoRepository<ApplicationSettings, String> {
}
