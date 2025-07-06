package com.ecom.eventhandler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.SqsException;

@Component
@RequiredArgsConstructor
public class Queue {
    final private SqsClient sqsClient;

    @Bean
    public String createQueue() {

        String[] queues=new String[]{"queue-order-events", "queue-payment-events"};
        try {
            for(String queueName: queues){
                System.out.println("\nCreate Queue");
                CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                        .queueName(queueName)
                        .build();

                sqsClient.createQueue(createQueueRequest);

                System.out.println("\nGet queue url");

                GetQueueUrlResponse getQueueUrlResponse = sqsClient
                        .getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build());
                System.out.println(".....queue created: "+getQueueUrlResponse.queueUrl());
            }

        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
}
