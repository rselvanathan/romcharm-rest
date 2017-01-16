package com.romcharm.config;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSAsyncClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonSNSConfig {

    @Bean
    public AmazonSNSAsyncClient amazonSNSAsyncClient() {
        AmazonSNSAsyncClient client = new AmazonSNSAsyncClient(new DefaultAWSCredentialsProviderChain());
        client.setRegion(Region.getRegion(Regions.EU_WEST_1));
        return client;
    }
}
