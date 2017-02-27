package com.romcharm.repositories;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.romcharm.domain.User;

class UserRepository implements Repository<User> {

    private final DynamoDBMapper dynamoDBMapper;

    UserRepository(DynamoDBMapper mapper) {
        dynamoDBMapper = mapper;
    }

    @Override
    public Class<User> getClassType() {
        return User.class;
    }

    @Override
    public DynamoDBMapper getDynamoDBMapper() {
        return dynamoDBMapper;
    }
}
