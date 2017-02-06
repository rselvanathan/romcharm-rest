package com.romcharm.repositories;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.romcharm.domain.romcharm.Family;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FamiliesRepository {

    private final DynamoDBMapper dynamoDBMapper;

    @Autowired
    public FamiliesRepository(DynamoDBMapper mapper) {
        dynamoDBMapper = mapper;
    }

    public Family findOne(String email) {
        return dynamoDBMapper.load(Family.class, email);
    }

    public Family save(Family family) {
        dynamoDBMapper.save(family);
        return family;
    }
}
