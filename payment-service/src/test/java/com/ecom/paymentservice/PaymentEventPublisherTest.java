package com.ecom.paymentservice;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecom.paymentservice.model.Payment;
import com.ecom.paymentservice.model.PaymentEvent;
import com.ecom.paymentservice.model.PaymentMode;
import com.ecom.paymentservice.model.PaymentStatus;
import com.ecom.paymentservice.publisher.PaymentEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

@ExtendWith(MockitoExtension.class)
class PaymentEventPublisherTest {

    @Mock
    private SqsClient sqsClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentEventPublisher paymentEventPublisher;

    private Payment mockPayment;
    private String mockJsonMessage;

    @BeforeEach
    void setUp() {
        mockPayment = Payment.builder()
            .id(12345L)
            .orderId(98765L)
            .paymentMode(PaymentMode.PAYMENT_MODE_CASH)
            .paymentStatus(PaymentStatus.PAYMENT_STATUS_SUCCESS)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        mockJsonMessage = "{\"id\":\"12345\",\"orderId\":98765,\"paymentMode\":\"PAYMENT_MODE_CASH\"}";
    }

    @Test
    void shouldSendPaymentEvent_whenCalledWithValidPayment() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(any(PaymentEvent.class))).thenReturn(mockJsonMessage);

        ArgumentCaptor<SendMessageRequest> captor = ArgumentCaptor.forClass(SendMessageRequest.class);

        paymentEventPublisher.sendPaymentEvent(mockPayment);

        verify(objectMapper).writeValueAsString(any(PaymentEvent.class));
        verify(sqsClient).sendMessage(captor.capture());

        SendMessageRequest capturedRequest = captor.getValue();

        assertEquals("/000000000000/queue-payment-events", capturedRequest.queueUrl());
        assertEquals(mockJsonMessage, capturedRequest.messageBody());
    }

    @Test
    void shouldHandleSqsException_whenSendMessageFails() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(any(PaymentEvent.class))).thenReturn(mockJsonMessage);
        doThrow(SqsException.builder().message("Test SQS failure").build())
            .when(sqsClient).sendMessage(any(SendMessageRequest.class));

        assertDoesNotThrow(() -> paymentEventPublisher.sendPaymentEvent(mockPayment));

        verify(sqsClient).sendMessage(any(SendMessageRequest.class));
    }

    @Test
    void shouldHandleJsonProcessingException_whenSerializationFails() throws JsonProcessingException {
        doThrow(new JsonProcessingException("Test JSON processing failure") {})
            .when(objectMapper).writeValueAsString(any(PaymentEvent.class));

            //parsing fails above
        assertDoesNotThrow(() -> paymentEventPublisher.sendPaymentEvent(mockPayment));

        verify(sqsClient, never()).sendMessage(any(SendMessageRequest.class));
    }
}
