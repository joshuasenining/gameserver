package org.softwarewolf.gameserver.repository;

import java.util.List;

import org.softwarewolf.gameserver.domain.SimpleTag;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface SimpleTagRepository extends MongoRepository<SimpleTag, String> {

	SimpleTag findOneByName(String arg0);
	
	SimpleTag findOneByNameAndCampaignId(String arg0, String arg1);
	
	List<SimpleTag> findAllByCampaignId(String arg0);

    /// Find by key value pair
    @Query("{?0 : ?1}")
    List<SimpleTag> findAllByKeyValue(String key, Object value);

    /// Find by key and array of values
    @Query("{?0 : {$in : ?1}}")
    List<SimpleTag> findAllByKeyValues(String key, Object[] value);	
}
