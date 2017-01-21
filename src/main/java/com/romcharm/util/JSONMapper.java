package com.romcharm.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.romcharm.util.exceptions.JSONMapperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JSONMapper {
    private final ObjectMapper objectMapper;

    @Autowired
    public JSONMapper(ObjectMapper mapper) {
        objectMapper = mapper;
    }

    public String getJSONStringFromObject(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JSONMapperException(e);
        }
    }
}
