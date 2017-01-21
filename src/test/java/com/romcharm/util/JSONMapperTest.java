package com.romcharm.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.romcharm.util.exceptions.JSONMapperException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JSONMapperTest {

    private class JsonProcessingExceptionPublic extends JsonProcessingException {
        JsonProcessingExceptionPublic(String msg) {
            super(msg);
        }
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private ObjectMapper objectMapperMock;

    @InjectMocks
    private JSONMapper jsonMapper;

    @Test
    public void whenObjectMapperSucceedsReturnString() throws JsonProcessingException {
        String expected = "success";
        Object testObject = new Object();
        when(objectMapperMock.writeValueAsString(testObject)).thenReturn(expected);

        String result = jsonMapper.getJSONStringFromObject(testObject);

        assertThat(result, is(expected));
    }

    @Test
    public void whenObjectMapperFailsExpectJsonMapperException() throws JsonProcessingException {
        Object testObject = new Object();
        when(objectMapperMock.writeValueAsString(testObject)).thenThrow(new JsonProcessingExceptionPublic(""));

        exception.expect(JSONMapperException.class);

        jsonMapper.getJSONStringFromObject(testObject);
    }
}