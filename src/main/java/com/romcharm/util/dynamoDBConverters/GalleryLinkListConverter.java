package com.romcharm.util.dynamoDBConverters;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.romcharm.domain.mypage.GalleryLink;

import java.io.IOException;
import java.util.List;

/**
 * @author Romesh Selvan
 */
public class GalleryLinkListConverter implements DynamoDBTypeConverter<String, List<GalleryLink>> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convert(List<GalleryLink> object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new DynamoDBMappingException("Error occured when converting object to JSON String", e);
        }
    }

    @Override
    public List<GalleryLink> unconvert(String object) {
        CollectionType collectionType =
            objectMapper.writer().getTypeFactory().constructCollectionType(List.class, GalleryLink.class);
        try {
            return objectMapper.readValue(object, collectionType);
        } catch (IOException e) {
            throw new DynamoDBMappingException("Error occured when converting JSON String to object", e);
        }
    }
}
