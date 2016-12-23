package com.romcharm.repositories;

import com.romcharm.domain.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccessRepository extends MongoRepository<UserRole, String>{
    @Override
    UserRole findOne(String username);

    @Override
    UserRole save(UserRole userRole);
}
