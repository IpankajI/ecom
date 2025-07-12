package com.ecom.paymentservice.appconfig;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.paymentservice.utils.IDGenerator;
import com.ecom.paymentservice.utils.IDGeneratorRandom;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Component
public class AppConfig {
    
    private Logger logger;

    @Bean
    public WebClient webClient(){
        return WebClient.builder().build();
    }

    @Bean
    public SqsClient sqsClient(){
        String endpoint = "http://queue:4566";
        String accessKey = "x";
        String secretKey = "x";
        URI uri=null;
        try{
            uri=new URI(endpoint);
        }
        catch(Exception e){
            logger.error(e.getMessage());
        }

        return SqsClient.builder()
            .credentialsProvider(StaticCredentialsProvider
                    .create(AwsBasicCredentials
                        .create(accessKey, secretKey)))
            .endpointOverride(uri)
            .region(Region.US_EAST_1)
        .build();
    }

    @Bean
    public IDGenerator idGenerator(){
        return new IDGeneratorRandom();
    }

    @Bean
    public Logger logger(){
        logger=LoggerFactory.getLogger(AppConfig.class);
        return logger;
    }
}
