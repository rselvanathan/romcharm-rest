package com.romcharm.config;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSAsyncClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonSNSConfig {

    private final AmazonCredentialsProvider amazonCredentialsProvider;

    @Autowired
    public AmazonSNSConfig(@SuppressWarnings("SpringJavaAutowiringInspection") AmazonCredentialsProvider amazonCredentialsProvider) {
        this.amazonCredentialsProvider = amazonCredentialsProvider;
    }

    @Bean
    public AmazonSNSAsyncClient amazonSNSAsyncClient() {
        AmazonSNSAsyncClient client = new AmazonSNSAsyncClient(amazonCredentialsProvider);
        client.setRegion(Region.getRegion(Regions.EU_WEST_1));
        return client;
    }
}
