package com.ecom.productservice.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

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
            .price(productRequest.getPrice())
            .build();

        productRepository.save(product);
        log.info("product with id: {} created", product.getId());
    }
    

    public List<ProductResponse> getProducts(){
        List<Product> products=productRepository.findAll();

        return products.stream().map(this::productResponseFrom).toList();
    }

    public ProductResponse getProduct(String productId){
        Optional<Product> product=productRepository.findById(productId);

        if(!product.isPresent()){
            logger.error("no such product");
            return null;
        }
 
        return productResponseFrom(product.get());
    }

    private ProductResponse productResponseFrom(Product product){
        return ProductResponse.builder()
            .description(product.getDescription())
            .id(product.getId())
            .name(product.getName())
            .price(product.getPrice())
            .build();
    }
}
