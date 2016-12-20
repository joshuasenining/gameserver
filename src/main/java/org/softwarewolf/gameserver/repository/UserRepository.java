package org.softwarewolf.gameserver.repository;

import java.util.Collection;
import java.util.List;

import org.softwarewolf.gameserver.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, String> {

	User findOneByUsername(String arg0);
	
	List<User> findByAuthorities(Collection arg0);

    /// Find by key value pair
    @Query("{?0 : ?1}")
    List<User> findAllByKeyValue(String key, Object value);

    /// Find by key and array of values
    @Query("{?0 : {$in : ?1}}")
    List<User> findAllByKeyValues(String key, Object[] value);		
    
    List<User> deleteByUsername(String username);
    
    User findOneByEmail(String email);
}
