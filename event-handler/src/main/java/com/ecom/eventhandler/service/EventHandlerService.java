package com.ecom.eventhandler.service;

import java.util.List;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.eventhandler.dto.PaymentStatusRequest;
import com.ecom.eventhandler.event.OrderPaymentStatus;
import com.ecom.eventhandler.event.PaymentEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class EventHandlerService {

    final private SqsClient sqsClient;
    private final WebClient webClient;

    @Scheduled(cron = "*/1 * * * * *")
    public void handleOrderEvents(){

        String queueUrl="/000000000000/queue-order-events";
        // sendMessage(queueUrl);
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .build();
        List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();
        // System.out.println("Received message count on order-events: " + messages.size());
        for (Message message : messages) {
            // System.out.println("Received message on order-events: " + message.body());

            // Delete the message after processing
            DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(message.receiptHandle())
                    .build();
            sqsClient.deleteMessage(deleteRequest);
            System.out.println("Message deleted.");
        }
    }

    @Scheduled(cron = "*/1 * * * * *")
    public void handlePaymentEvents(){
        
        String queueUrl="/000000000000/queue-payment-events";

        // sendMessage(queueUrl);
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .build();
        List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();
        // System.out.println("Received message count on payment-events:: " + messages.size());
        for (Message message : messages) {
            // System.out.println("Received message on payment-events: " + message.body());
            PaymentEvent paymentEvent=null;
            ObjectMapper objectMapper=new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            try {
                paymentEvent=objectMapper.readValue(message.body(), PaymentEvent.class);
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            updateOrderStatus(paymentEvent);
            // Delete the message after processing
            DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(message.receiptHandle())
                    .build();
            sqsClient.deleteMessage(deleteRequest);
            System.out.println("Message deleted.");
        }
    }

    private void updateOrderStatus(PaymentEvent paymentEvent){

        OrderPaymentStatus orderPaymentStatus=null;
        switch (paymentEvent.getPaymentStatus()) {
            case PAYMENT_STATUS_SUCCESS:
                orderPaymentStatus=OrderPaymentStatus.ORDER_PAYMENT_STATUS_COMPLETED;
                break;
            case PAYMENT_STATUS_INITIATED:
                orderPaymentStatus=OrderPaymentStatus.ORDER_PAYMENT_STATUS_INITIATED;
                break;
            default:
                throw new RuntimeException("cannot update order status, invalid payment status");
        }
        
        PaymentStatusRequest paymentStatusRequest=PaymentStatusRequest.builder()
                        .paymentStatus(orderPaymentStatus)
                        .build();
        String data=webClient.patch()
        .uri("http://order-service:30003/api/orders/"+paymentEvent.getOrderId()+"/paymentStatus")
        // .body(Mono.just(paymentStatusRequest), PaymentStatusRequest.class)
        // .body(BodyInserters.fromFormData("paymentStatus", "ORDER_PAYMENT_STATUS_COMPLETED"))
        .bodyValue(paymentStatusRequest)
        .retrieve()
        .bodyToMono(String.class)
        .block();

    }

    private void sendMessage( String queueUrl){
        // String queueUrl="/000000000000/queue-order-events";
        
        try {
            // Send a message
            SendMessageRequest sendRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody("This is a test message")
                    .build();
            sqsClient.sendMessage(sendRequest);
            System.out.println("Message sent successfully!");
            

        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            // System.exit(1);
        } finally {
            sqsClient.close();
        }
    }
}
