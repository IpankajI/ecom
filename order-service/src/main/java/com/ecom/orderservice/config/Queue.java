package com.ecom.orderservice.config;

import java.net.URI;

import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsClient;

@Component
@RequiredArgsConstructor
public class Queue {
    private final Logger logger;

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
            logger.error("{}",e.getMessage());
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
    public SqsAsyncClient sqsAsyncClient(){
        String endpoint = "http://queue:4566";
        String accessKey = "x";
        String secretKey = "x";
        URI uri=null;
        try{
            uri=new URI(endpoint);
        }
        catch(Exception e){
            logger.error("{}",e.getMessage());
        }

        return SqsAsyncClient.builder()
            .credentialsProvider(StaticCredentialsProvider
                    .create(AwsBasicCredentials
                        .create(accessKey, secretKey)))
            .endpointOverride(uri)
            .region(Region.US_EAST_1)
        .build();

    }
}
