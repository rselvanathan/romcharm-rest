package com.romcharm.repositories;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.romcharm.domain.romcharm.Family;

class FamiliesRepository implements Repository<Family> {

    private final DynamoDBMapper dynamoDBMapper;

    FamiliesRepository(DynamoDBMapper mapper) {
        this.dynamoDBMapper = mapper;
    }

    @Override
    public Class<Family> getClassType() {
        return Family.class;
    }

    @Override
    public DynamoDBMapper getDynamoDBMapper() {
        return dynamoDBMapper;
    }
}
