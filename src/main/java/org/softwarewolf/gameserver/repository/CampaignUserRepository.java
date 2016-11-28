package org.softwarewolf.gameserver.repository;

import java.util.List;

import org.softwarewolf.gameserver.domain.CampaignUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CampaignUserRepository extends MongoRepository<CampaignUser, String> {

//	CampaignPlayer findOne(String arg0);

	@Query("{'campaignId' : ?0}")
	List<CampaignUser> findByCampaignId(String campaignId);
	
	List<CampaignUser> findAllByCampaignIdAndRole(String campaignId, String role);
	
	CampaignUser findByCampaignIdAndUserId(String campaignId, String userId);

	List<CampaignUser> findAllByUserId(String userId);
	
	List<CampaignUser> findAllByUserIdAndRole(String userId, String role);
	
    /// Find by key value pair
    @Query("{?0 : ?1}")
    List<CampaignUser> findAllByKeyValue(String key, Object value);

    /// Find by key and array of values
    @Query("{?0 : {$in : ?1}}")
    List<CampaignUser> findAllByKeyValues(String key, Object[] value);
    
    List<CampaignUser> deleteByCampaignId(String campaignId);
    
    List<CampaignUser> deleteByCampaignIdAndUserId(String campaignId, String userId);
    
    List<CampaignUser> deleteByUserId(String userId);
    
    void deleteAll();
}
