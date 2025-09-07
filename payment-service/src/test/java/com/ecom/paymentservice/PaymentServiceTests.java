package com.ecom.paymentservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecom.paymentservice.client.OrderServiceClient;
import com.ecom.paymentservice.dto.OrderResponse;
import com.ecom.paymentservice.dto.PaymentRequest;
import com.ecom.paymentservice.model.Payment;
import com.ecom.paymentservice.model.PaymentMode;
import com.ecom.paymentservice.model.PaymentStatus;
import com.ecom.paymentservice.publisher.PaymentEventPublisher;
import com.ecom.paymentservice.repository.PaymentRepository;
import com.ecom.paymentservice.service.PaymentService;
import com.ecom.paymentservice.utils.IDGenerator;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTests {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private IDGenerator idGenerator;
    @Mock
    private OrderServiceClient orderServiceClient;
    @Mock
    private PaymentEventPublisher paymentEventPublisher;

    @InjectMocks
    private PaymentService paymentService;

    
    private PaymentRequest paymentRequest;
    private OrderResponse orderResponse;
    private Payment expectedPayment;

    @Captor
    ArgumentCaptor<Payment> paymentCaptor;
    @Captor
    ArgumentCaptor<Payment> eventCaptor;

    @BeforeEach
    void setUp(){
        paymentRequest = PaymentRequest.builder()
            .orderId("1000")
            .paymentMode(PaymentMode.PAYMENT_MODE_CASH)
            .build();
        
        orderResponse = new OrderResponse();
        orderResponse.setId(123L);
        orderResponse.setTotalAmount(BigDecimal.valueOf(100.00));

        expectedPayment = Payment.builder()
            .id(456L)
            .orderId(1000L)
            .paymentMode(PaymentMode.PAYMENT_MODE_CASH)
            .paymentStatus(PaymentStatus.PAYMENT_STATUS_INITIATED)
            .build();
    }

    @Test
    void testInitiatePayment_Success() {
        when(orderServiceClient.getOrderById(paymentRequest.getOrderId())).thenReturn(orderResponse);
        when(idGenerator.next()).thenReturn(456L);
        when(paymentRepository.save(any(Payment.class))).thenReturn(expectedPayment);

        Payment createdPayment = paymentService.initiatePayment(paymentRequest);

        assertNotNull(createdPayment);
        assertEquals(expectedPayment.getId(), createdPayment.getId());
        assertEquals(PaymentStatus.PAYMENT_STATUS_INITIATED, createdPayment.getPaymentStatus());

        verify(paymentRepository).save(paymentCaptor.capture());
        verify(paymentEventPublisher).sendPaymentEvent(eventCaptor.capture());
    }

    @Test
    void testInitiatePayment_OrderNotFound() {
        when(orderServiceClient.getOrderById(paymentRequest.getOrderId())).thenReturn(null);

        Payment createdPayment = paymentService.initiatePayment(paymentRequest);

        assertNull(createdPayment);
    }


    @Test
    void testUpdateStatus(){
        when(paymentRepository.findById(expectedPayment.getId())).thenReturn(Optional.of(expectedPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(expectedPayment);

        Payment updatedPayment=paymentService.updateStatus(expectedPayment.getId(), PaymentStatus.PAYMENT_STATUS_SUCCESS);
        assertNotNull(updatedPayment);
        assertEquals(PaymentStatus.PAYMENT_STATUS_SUCCESS, updatedPayment.getPaymentStatus());


        verify(paymentRepository).save(paymentCaptor.capture());
        verify(paymentEventPublisher).sendPaymentEvent(eventCaptor.capture());
    }

    @Test
    void testGetPayment(){
        when(paymentRepository.findById(expectedPayment.getId())).thenReturn(Optional.of(expectedPayment));

        Payment foundPayment = paymentService.getPayment(expectedPayment.getId());
        assertNotNull(foundPayment);
        assertEquals(expectedPayment.getId(), foundPayment.getId());
        assertEquals(expectedPayment.getCreatedAt(), foundPayment.getCreatedAt());
        assertEquals(expectedPayment.getOrderId(), foundPayment.getOrderId());
        assertEquals(expectedPayment.getPaymentMode(), foundPayment.getPaymentMode());
        assertEquals(expectedPayment.getPaymentStatus(), foundPayment.getPaymentStatus());
        assertEquals(expectedPayment.getUpdatedAt(), foundPayment.getUpdatedAt());
    }
}


