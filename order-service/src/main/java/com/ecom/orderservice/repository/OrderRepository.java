package com.ecom.orderservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.lang.NonNull;

import com.ecom.orderservice.model.Order;

import jakarta.persistence.LockModeType;

public interface OrderRepository extends JpaRepository<Order, Long>{
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @NonNull Optional<Order> findById(@NonNull Long id);
}
