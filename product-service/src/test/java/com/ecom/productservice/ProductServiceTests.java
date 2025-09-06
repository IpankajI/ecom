package com.ecom.productservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.data.domain.Pageable;

import com.ecom.productservice.dto.ProductRequest;
import com.ecom.productservice.model.Product;
import com.ecom.productservice.repository.ProductRepository;
import com.ecom.productservice.service.ProductService;
import com.ecom.productservice.utils.IDGenerator;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {
    
    @Mock
    private ProductRepository productRepository;
    @Mock
    private IDGenerator idGenerator;
    @Mock
    private Logger logger;

    @InjectMocks
    private ProductService productService;


    private ProductRequest productRequest;
    Product product;

    private final String name="Iphone15";
    private final String description="Iphone15 for India";
    private final BigDecimal price=BigDecimal.valueOf(65000+1);
    private final Long productId=11L;


    @BeforeEach
    void setUp(){
        productRequest=new ProductRequest(name, description, price.toString());
        product=new Product(productId, name, description, price);
    }


    @Test
    void testAddProduct(){
        when(idGenerator.next()).thenReturn(productId);
        when(productRepository.save(any(Product.class))).thenReturn(product);


        Product addedProduct=productService.addProduct(productRequest);

        assertNotNull(addedProduct);
        assertEquals(name, addedProduct.getName());
        assertEquals(productId, addedProduct.getId());
        assertEquals(price, addedProduct.getPrice());
        assertEquals(description, addedProduct.getDescription());

    }


    @Test
    void testGetProduct(){
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Product foundProduct=productService.getProduct(productId);
        assertNotNull(foundProduct);
        assertEquals(name, foundProduct.getName());
        assertEquals(productId, foundProduct.getId());
        assertEquals(price, foundProduct.getPrice());
        assertEquals(description, foundProduct.getDescription());
    }


    @Test
    void testGetProducts(){

        List<Product> products=List.of(product);
        when(productRepository.findProductsWithLessThanId(anyLong(), any(Pageable.class))).thenReturn(products);
        when(productRepository.findProductsWithGreaterThanId(anyLong(), any(Pageable.class))).thenReturn(products);


        List<Product> founProducts=productService.getProducts(productId, 0l, 1);
        assertEquals(1, founProducts.size());

        Product foundProduct=founProducts.get(0);
        assertNotNull(foundProduct);
        assertEquals(description, foundProduct.getDescription());
        assertEquals(productId, foundProduct.getId());
        assertEquals(price, foundProduct.getPrice());

        founProducts=productService.getProducts(0l, productId, 1);
        assertEquals(1, founProducts.size());

        foundProduct=founProducts.get(0);
        assertNotNull(foundProduct);
        assertEquals(description, foundProduct.getDescription());
        assertEquals(productId, foundProduct.getId());
        assertEquals(price, foundProduct.getPrice());


    }


}
