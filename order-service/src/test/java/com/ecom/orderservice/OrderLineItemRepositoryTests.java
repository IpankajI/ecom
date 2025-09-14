package com.ecom.orderservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.ecom.orderservice.model.OrderLineItem;
import com.ecom.orderservice.repository.OrderLineItemRepository;

@DataJpaTest
class OrderLineItemRepositoryTests {
    
    @Autowired
    private OrderLineItemRepository orderLineItemRepository;

    private OrderLineItem orderLineItem;

    @BeforeEach
    void cleanUp(){
        orderLineItemRepository.deleteAll();

        orderLineItem=new OrderLineItem(11l, BigDecimal.valueOf(11l), 11, null, 11l, 11l);
        
    }
    
    @Test
    void testSave(){
        orderLineItemRepository.save(orderLineItem);

        OrderLineItem foundItem=orderLineItemRepository.findById(11l).get();

        assertNotNull(foundItem);

        assertEquals(11L, foundItem.getId());
    }


}
