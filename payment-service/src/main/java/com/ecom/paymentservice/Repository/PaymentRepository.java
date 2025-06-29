package com.ecom.paymentservice.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.ecom.paymentservice.model.Payment;

import jakarta.persistence.LockModeType;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @NonNull Optional<Payment> findById(@NonNull Integer id);
}