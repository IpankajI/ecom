package com.ecom.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.orderservice.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{
    
}
