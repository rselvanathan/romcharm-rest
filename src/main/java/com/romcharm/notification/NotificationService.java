package com.romcharm.notification;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.romcharm.domain.romcharm.Family;
import com.romcharm.util.JSONMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class NotificationService {
    private final AmazonSNSClient amazonSNSClient;
    private final JSONMapper jsonMapper;

    @Value("${AWS_EMAIL_SNS_TOPIC}")
    private String emailSNSTopic;

    @Value("${APP_TYPE}")
    private String appType;

    @Autowired
    public NotificationService(AmazonSNSClient client, JSONMapper mapper) {
        amazonSNSClient = client;
        jsonMapper = mapper;
    }

    public void sendEmailNotification(Family family) {
        String emailMessageJson = jsonMapper.getJSONStringFromObject(family);
        PublishRequest request = new PublishRequest(emailSNSTopic, emailMessageJson);

        HashMap<String, MessageAttributeValue> attributes = new HashMap<>();
        attributes.put("apptype", new MessageAttributeValue().withDataType("String").withStringValue(appType));
        request.setMessageAttributes(attributes);

        amazonSNSClient.publish(request);
    }
}
