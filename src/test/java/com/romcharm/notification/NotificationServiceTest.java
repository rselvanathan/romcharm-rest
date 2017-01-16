package com.romcharm.notification;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.sns.AmazonSNSAsyncClient;
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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationServiceTest {

    private final static String SNS_TOPIC = "topic";

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
    }

    @Test
    public void whenSendingAnEmailNotificationSendANotificationToAmazonClientWithExpectedEmailString() throws JsonProcessingException {
        EmailMessage message = new EmailMessage("email", "firstname", "lastname", true, 5);
        String expected = objectMapper.writeValueAsString(message);

        when(jsonMapperMock.getJSONStringFromObject(message)).thenReturn(expected);
        notificationService.sendEmailNotificiation(message);

        verify(amazonSNSAsyncClientMock).publishAsync(eq(SNS_TOPIC), eq(expected), any(AsyncHandler.class));
    }
}