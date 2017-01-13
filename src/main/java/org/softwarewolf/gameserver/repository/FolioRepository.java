package org.softwarewolf.gameserver.repository;

import java.util.List;

import org.softwarewolf.gameserver.domain.Folio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface FolioRepository extends MongoRepository<Folio, String> {

	public Folio findOneByCampaignIdAndTitle(String campaignId, String title);
	
	public List<Folio> findAllByCampaignId(String campaignId);
	
	public List<Folio> findAllByReaders(String userId);
	
	public List<Folio> findAllByWriters(String userId);

	public List<Folio> findAllByOwners(String ownerId);
	
	public List<Folio> findAllByOwnersAndCampaignId(String ownerId, String campaignId);

	public List<Folio> findAllByReadersAndCampaignId(String userId, String campaignId);

	public List<Folio> findAllByWritersAndCampaignId(String userId, String campaignId);

	/// Find by key value pair
    @Query("{?0 : ?1}")
    public List<Folio> findAllByKeyValue(String key, Object value);

    /// Find by key and array of values
    @Query("{?0 : {$in : ?1}}")
    public List<Folio> findAllByKeyValues(String key, Object[] value);

    public List<Folio> deleteByCampaignId(String campaignId);
}
