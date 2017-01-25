package com.romcharm.notification;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.sns.AmazonSNSAsyncClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.romcharm.notification.domain.EmailMessage;
import com.romcharm.util.JSONMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationServiceTest {

    private final static String SNS_TOPIC = "topic";
    private final static String APP_TYPE = "rom";

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private JSONMapper jsonMapperMock;

    @Mock
    private AmazonSNSAsyncClient amazonSNSAsyncClientMock;

    @InjectMocks
    private NotificationService notificationService;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(notificationService, "emailSNSTopic", SNS_TOPIC);
        ReflectionTestUtils.setField(notificationService, "appType", APP_TYPE);
    }

    @Test
    public void whenSendingAnEmailNotificationSendANotificationToAmazonClientWithExpectedEmailString() throws JsonProcessingException {
        EmailMessage message = new EmailMessage("email", "firstname", "lastname", true, 5);
        String expected = objectMapper.writeValueAsString(message);

        PublishRequest request = new PublishRequest(SNS_TOPIC, expected);
        HashMap<String, MessageAttributeValue> attributes = new HashMap<>();
        attributes.put("apptype", new MessageAttributeValue().withDataType("String").withStringValue(APP_TYPE));
        request.setMessageAttributes(attributes);

        when(jsonMapperMock.getJSONStringFromObject(message)).thenReturn(expected);
        notificationService.sendEmailNotification(message);

        verify(amazonSNSAsyncClientMock).publishAsync(eq(request), any(AsyncHandler.class));
    }
}