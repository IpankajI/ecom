package com.ecom.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.orderservice.model.OrderLineItem;

public interface OrderLineItemRepository extends JpaRepository<OrderLineItem, Long>{
    
}
