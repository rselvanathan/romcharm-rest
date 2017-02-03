package com.romcharm.repositories;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.romcharm.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserRepository {

    private final DynamoDBMapper dynamoDBMapper;

    @Autowired
    public UserRepository(DynamoDBMapper mapper) {
        dynamoDBMapper = mapper;
    }

    public User findOne(String username) {
        return dynamoDBMapper.load(User.class, username);
    }

    public User save(User userRole) {
        dynamoDBMapper.save(userRole);
        return userRole;
    }
}
