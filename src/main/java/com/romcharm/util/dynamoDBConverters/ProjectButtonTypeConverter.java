package com.romcharm.util.dynamoDBConverters;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.romcharm.defaults.ProjectButtonTypes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Romesh Selvan
 */
public class ProjectButtonTypeConverter implements DynamoDBTypeConverter<List<String>, List<ProjectButtonTypes>> {

    @Override
    public List<String> convert(List<ProjectButtonTypes> object) {
        return object.stream().map(ProjectButtonTypes::name).collect(Collectors.toList());
    }

    @Override
    public List<ProjectButtonTypes> unconvert(List<String> object) {
        return object.stream().map(ProjectButtonTypes::valueOf).collect(Collectors.toList());
    }
}
