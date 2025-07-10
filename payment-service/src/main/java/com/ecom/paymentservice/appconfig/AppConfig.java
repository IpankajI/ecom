package com.ecom.paymentservice.appconfig;

import java.net.URI;

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
    

    @Bean
    public WebClient webClient(){
        return WebClient.builder().build();
    }

    @Bean
    public SqsClient sqsClient(){
        String endpoint = "http://queue:4566";
        // String endpoint = "http://localhost:9325/";
        // String region = "elasticmq";
        String accessKey = "x";
        String secretKey = "x";
        URI uri=null;
        try{
            uri=new URI(endpoint);
        }
        catch(Exception e){
            System.out.println("endpoint error: "+e.getStackTrace());
        }

        SqsClient sqsClient=SqsClient.builder()
            .credentialsProvider(StaticCredentialsProvider
                    .create(AwsBasicCredentials
                        .create(accessKey, secretKey)))
            .endpointOverride(uri)
            .region(Region.US_EAST_1)
        .build();

        return sqsClient;
    }

    @Bean
    public IDGenerator idGenerator(){
        return new IDGeneratorRandom();
    }
}
