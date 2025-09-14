package com.ecom.orderservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ecom.orderservice.model.Order;
import com.ecom.orderservice.model.OrderLineItem;
import com.ecom.orderservice.model.OrderPaymentStatus;
import com.ecom.orderservice.model.OrderStatus;
import com.ecom.orderservice.repository.OrderRepository;

@DataJpaTest
class OrderRepositoryTests {
    
    @Autowired
    private OrderRepository orderRepository;


    private Order order;
    private List<OrderLineItem> orderLineItems;

    @BeforeEach
    void setUp(){

        orderRepository.deleteAll();

        order=new Order(11L,
             "OrderNumber-11", 
                null, 
                BigDecimal.valueOf(11), 
                OrderStatus.ORDER_STATUS_CREATED, 
                OrderPaymentStatus.ORDER_PAYMENT_STATUS_INITIATED);

        orderLineItems=List.of(
            new OrderLineItem(1L, BigDecimal.valueOf(1000L), 1, order, 1l, 1l),
            new OrderLineItem(2L, BigDecimal.valueOf(2000L), 1, order, 2l, 1l)
        );

        order.setOrderLineItems(orderLineItems);
    }

    @Test
    void testSaveAndFindById(){

        orderRepository.save(order);

        Order foundOrder=orderRepository.findById(11l).get();
        assertNotNull(foundOrder);

        assertEquals(order.getId(), foundOrder.getId());
        assertEquals(order.getOrderLineItems().size(), foundOrder.getOrderLineItems().size());
        assertEquals(order.getOrderNumber(), foundOrder.getOrderNumber());
        assertEquals(order.getPaymentStatus(), foundOrder.getPaymentStatus());
        assertEquals(order.getStatus(), foundOrder.getStatus());
        assertEquals(order.getTotalAmount(), foundOrder.getTotalAmount());

    }

    @Test
    void testSaveAndFindAll(){
        orderRepository.save(order);
        
        List<Order> foundOrders=orderRepository.findAll();
        assertNotNull(foundOrders);
        assertEquals(1, foundOrders.size());
    }

}
