package com.romcharm.config;

import com.amazonaws.services.sns.AmazonSNSAsyncClient;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class AmazonSNSTestProducer {

    @Bean
    @Primary
    public AmazonSNSAsyncClient amazonSNSAsyncClientMock() {
        return Mockito.mock(AmazonSNSAsyncClient.class);
    }
}
