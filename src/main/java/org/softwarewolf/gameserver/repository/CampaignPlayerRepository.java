package org.softwarewolf.gameserver.repository;

import java.util.List;

import org.softwarewolf.gameserver.domain.CampaignPlayer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CampaignPlayerRepository extends MongoRepository<CampaignPlayer, String> {

//	CampaignPlayer findOne(String arg0);

	@Query("{'ownerId' : ?1}")
	List<CampaignPlayer> findByOwnerId(String arg0);
	
    /// Find by key value pair
    @Query("{?0 : ?1}")
    List<CampaignPlayer> findAllByKeyValue(String key, Object value);

    /// Find by key and array of values
    @Query("{?0 : {$in : ?1}}")
    List<CampaignPlayer> findAllByKeyValues(String key, Object[] value);
}
