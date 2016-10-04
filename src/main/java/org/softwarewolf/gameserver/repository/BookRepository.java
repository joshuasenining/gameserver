package org.softwarewolf.gameserver.repository;

import java.util.List;

import org.softwarewolf.gameserver.domain.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface BookRepository extends MongoRepository<Book, String> {

	public Book findOneByCampaignIdAndTitle(String arg0, String arg1);

    /// Find by key value pair
    @Query("{?0 : ?1}")
    public List<Book> findAllByKeyValue(String key, Object value);

    /// Find by key and array of values
    @Query("{?0 : {$in : ?1}}")
    public List<Book> findAllByKeyValues(String key, Object[] value);

}
