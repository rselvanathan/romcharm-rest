package com.romcharm.config;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSAsyncClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonSNSConfig {

    @Value("${AWS_ACCESS_KEY_ID}")
    private String accessKey;

    @Value("${AWS_SECRET_ACCESS_KEY}")
    private String secretKey;

    @Bean
    public AmazonSNSAsyncClient amazonSNSAsyncClient() {
        AmazonSNSAsyncClient client = new AmazonSNSAsyncClient(new BasicAWSCredentials(accessKey, secretKey));
        client.setRegion(Region.getRegion(Regions.EU_WEST_1));
        return client;
    }
}
