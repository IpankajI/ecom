package com.ecom.paymentservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ecom.paymentservice.model.Payment;
import com.ecom.paymentservice.model.PaymentMode;
import com.ecom.paymentservice.model.PaymentStatus;
import com.ecom.paymentservice.repository.PaymentRepository;
import com.ecom.paymentservice.utils.IDGenerator;

@DataJpaTest
class PaymentRepositoryTests {
    
    @Autowired
    private PaymentRepository paymentRepository;


    @Mock
    private IDGenerator idGenerator;

    private Payment payment;
    private final Long orderId=1000L;
    private final PaymentMode paymentMode=PaymentMode.PAYMENT_MODE_CASH;
    private final PaymentStatus paymentStatus=PaymentStatus.PAYMENT_STATUS_INITIATED;
    private final LocalDateTime now=LocalDateTime.now();
    private final Long id=11L;

    @BeforeEach
    void setUp(){
        when(idGenerator.next()).thenReturn(id);
        payment=Payment.builder()
            .id(idGenerator.next())
            .createdAt(now)
            .orderId(orderId)
            .paymentMode(paymentMode)
            .updatedAt(now)
            .paymentStatus(paymentStatus)
            .build();
    }

    @Test
    void testSave(){
        Payment savedPayment=paymentRepository.save(payment);
        assertNotNull(savedPayment);
        assertEquals(orderId, savedPayment.getOrderId());
        assertEquals(now, savedPayment.getCreatedAt());
        assertEquals(id, savedPayment.getId());
        assertEquals(paymentMode, savedPayment.getPaymentMode());
        assertEquals(paymentStatus, savedPayment.getPaymentStatus());
        assertEquals(now, savedPayment.getUpdatedAt());
    }


    @Test
    void testFindById(){
        paymentRepository.save(payment);
        Payment foundItem=paymentRepository.findById(id).get();
        assertNotNull(foundItem);
        assertEquals(orderId, foundItem.getOrderId());
        assertEquals(now, foundItem.getCreatedAt());
        assertEquals(id, foundItem.getId());
        assertEquals(paymentMode, foundItem.getPaymentMode());
        assertEquals(paymentStatus, foundItem.getPaymentStatus());
        assertEquals(now, foundItem.getUpdatedAt());
    }
}
