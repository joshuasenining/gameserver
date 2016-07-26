package org.softwarewolf.gameserver.repository;

import java.util.List;

import org.softwarewolf.gameserver.domain.OrganizationType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface OrganizationTypeRepository extends MongoRepository<OrganizationType, String> {

	public OrganizationType findOneByNameAndCampaignId(String arg0, String arg1);
	
    /// Find by key value pair
    @Query("{?0 : ?1}")
    List<OrganizationType> findAllByKeyValue(String key, Object value);

    /// Find by key and array of values
    @Query("{?0 : {$in : ?1}}")
    List<OrganizationType> findAllByKeyValues(String key, Object[] value);
}
