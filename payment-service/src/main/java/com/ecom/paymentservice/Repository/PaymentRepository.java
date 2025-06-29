package com.ecom.paymentservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.paymentservice.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    
}