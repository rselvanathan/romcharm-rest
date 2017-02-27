package com.romcharm.repositories;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Romesh Selvan
 */
public interface Repository<T> {

    default T findOne(String id) {
        return getDynamoDBMapper().load(getClassType(), id);
    }

    default List<T> getProjects() {
        DynamoDBScanExpression expression = new DynamoDBScanExpression();
        PaginatedScanList<T> list = getDynamoDBMapper().scan(getClassType(), expression);
        // Forcing entire list to be loaded by iterating over the list. This is because AWS uses Lazy loading when returning
        // a scan table result.
        return list.stream().collect(Collectors.toList());
    }

    default T save(T object) {
        getDynamoDBMapper().save(object);
        return object;
    }

    Class<T> getClassType();

    DynamoDBMapper getDynamoDBMapper();
}
