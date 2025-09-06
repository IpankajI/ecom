package com.ecom.productservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.ecom.productservice.model.Product;
import com.ecom.productservice.repository.ProductRepository;

@DataMongoTest
class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;
    
    private final String name="Iphone15";
    private final String description="Iphone15 for India";
    private final BigDecimal price=BigDecimal.valueOf(65000+1);
    private final Long productId=11L;

    @Spy
    private Product product;
    @BeforeEach
    void setUp(){
        product.setDescription(description);
        product.setId(productId);
        product.setName(name);
        product.setPrice(price);
    }

    @AfterEach
    void cleanUp(){
        productRepository.delete(product);   
    }

    @Test
    void testSave(){
        Product createdProduct= productRepository.save(product);
        assertNotNull(createdProduct);
        assertEquals(description, createdProduct.getDescription());
        assertEquals(productId, createdProduct.getId());
        assertEquals(price, createdProduct.getPrice());
    }

    @Test
    void testFindById(){
        productRepository.save(product);
        Product foundProduct=productRepository.findById(productId).get();
        assertNotNull(foundProduct);
        assertEquals(description, foundProduct.getDescription());
        assertEquals(productId, foundProduct.getId());
        assertEquals(price, foundProduct.getPrice());
    }

    @Test
    void testFindProductsWithLessThanId(){
        productRepository.save(product);
        Pageable pageable=PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC,"_id"));

        List<Product> products= productRepository.findProductsWithLessThanId(productId+1, pageable).reversed();
        assertEquals(1, products.size());
        Product foundProduct=products.get(0);
        assertNotNull(foundProduct);
        assertEquals(description, foundProduct.getDescription());
        assertEquals(productId, foundProduct.getId());
        assertEquals(price, foundProduct.getPrice());
    }

    @Test
    void testFindProductsWithGreaterThanId(){
        productRepository.save(product);
        Pageable pageable=PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC,"_id"));

        List<Product> products= productRepository.findProductsWithGreaterThanId(productId-1, pageable).reversed();
        assertEquals(1, products.size());
        Product foundProduct=products.get(0);
        assertNotNull(foundProduct);
        assertEquals(description, foundProduct.getDescription());
        assertEquals(productId, foundProduct.getId());
        assertEquals(price, foundProduct.getPrice());
    }


}
