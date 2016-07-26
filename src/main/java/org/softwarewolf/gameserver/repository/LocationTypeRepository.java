package org.softwarewolf.gameserver.repository;

import java.util.List;

import org.softwarewolf.gameserver.domain.LocationType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface LocationTypeRepository extends MongoRepository<LocationType, String> {

	public LocationType findOneByNameAndCampaignId(String arg0, String arg1);
	
    /// Find by key value pair
    @Query("{?0 : ?1}")
    List<LocationType> findAllByKeyValue(String key, Object value);

    /// Find by key and array of values
    @Query("{?0 : {$in : ?1}}")
    List<LocationType> findAllByKeyValues(String key, Object[] value);
}
