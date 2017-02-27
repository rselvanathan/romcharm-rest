package com.romcharm.config;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Romesh Selvan
 */
@Configuration
public class AWSConfig {

    private final AmazonCredentialsProvider amazonCredentialsProvider;

    @Autowired
    public AWSConfig(@SuppressWarnings("SpringJavaAutowiringInspection") AmazonCredentialsProvider amazonCredentialsProvider) {
        this.amazonCredentialsProvider = amazonCredentialsProvider;
    }

    @Bean
    public DynamoDBMapper dynamoDBMapper() {
        AmazonDynamoDB amazonDynamoDB =
            AmazonDynamoDBClientBuilder.standard()
                                       .withCredentials(amazonCredentialsProvider)
                                       .withRegion(Regions.EU_WEST_1)
                                       .build();
        return new DynamoDBMapper(amazonDynamoDB);

    }

    @Bean
    public AmazonSNSClient amazonSNSAsyncClient() {
        return (AmazonSNSClient)AmazonSNSClientBuilder.standard()
                                                      .withCredentials(amazonCredentialsProvider)
                                                      .withRegion(Regions.EU_WEST_1)
                                                      .build();
    }
}
