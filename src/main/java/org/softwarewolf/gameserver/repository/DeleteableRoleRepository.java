package org.softwarewolf.gameserver.repository;

import org.softwarewolf.gameserver.domain.DeleteableRole;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeleteableRoleRepository extends MongoRepository<DeleteableRole, String> {

	DeleteableRole findOneByRole(String arg0);

}
