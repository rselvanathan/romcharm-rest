package com.romcharm.notification;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.sns.AmazonSNSAsyncClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.romcharm.notification.domain.EmailMessage;
import com.romcharm.notification.exception.NotificationException;
import com.romcharm.util.JSONMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Component
public class NotificationService {
    private final AmazonSNSAsyncClient amazonSNSAsyncClient;
    private final JSONMapper jsonMapper;

    @Value("${AWS_EMAIL_SNS_TOPIC}")
    private String emailSNSTopic;

    @Value("${APP_TYPE}")
    private String appType;

    @Autowired
    public NotificationService(AmazonSNSAsyncClient client, JSONMapper mapper) {
        amazonSNSAsyncClient = client;
        jsonMapper = mapper;
    }

    public CompletableFuture<PublishResult> sendEmailNotificiation(EmailMessage emailMessage) {
        CompletableFuture<PublishResult> future = new CompletableFuture<>();
        String emailMessageJson = jsonMapper.getJSONStringFromObject(emailMessage);
        PublishRequest request = new PublishRequest(emailSNSTopic, emailMessageJson);

        HashMap<String, MessageAttributeValue> attributes = new HashMap<>();
        attributes.put("apptype", new MessageAttributeValue().withDataType("String").withStringValue(appType));
        request.setMessageAttributes(attributes);

        amazonSNSAsyncClient.publishAsync(request, getAsyncHandler(future));
        return future;
    }

    private AsyncHandler<PublishRequest, PublishResult> getAsyncHandler(final CompletableFuture<PublishResult> future) {
        return new AsyncHandler<PublishRequest, PublishResult>() {
            @Override
            public void onError(Exception e) {
                future.completeExceptionally(new NotificationException("An error has occured when trying to send the notification", e));
            }

            @Override
            public void onSuccess(PublishRequest request, PublishResult result) {
                future.complete(result);
            }
        };
    }
}
