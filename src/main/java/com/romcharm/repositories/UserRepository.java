package com.romcharm.repositories;

import com.romcharm.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String>{
    @Override
    User findOne(String username);

    @Override
    User save(User userRole);
}
