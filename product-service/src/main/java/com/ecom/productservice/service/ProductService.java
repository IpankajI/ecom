package com.ecom.productservice.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import com.ecom.productservice.dto.ProductRequest;
import com.ecom.productservice.dto.ProductResponse;
import com.ecom.productservice.model.Product;
import com.ecom.productservice.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {


    private final ProductRepository productRepository;
    private final Logger logger;

    public void addProduct(ProductRequest productRequest){
        Product product= Product.builder()
            .name(productRequest.getName())
            .description(productRequest.getDescription())
            .price(BigDecimal.valueOf(Long.valueOf(productRequest.getPrice())))
            .build();

        productRepository.save(product);
        log.info("product with id: {} created", product.getId());
    }

    public List<Product> getProducts(){
        return productRepository.findAll();
    }

    public Product getProduct(String productId){
        Optional<Product> product=productRepository.findById(productId);

        if(!product.isPresent()){
            logger.error("no such product");
            return null;
        }
 
        return product.get();
    }

}
