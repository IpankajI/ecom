package com.ecom.productservice.service;

import java.util.List;

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

        List<ProductResponse> productResponses = products.stream().map(product -> ProductResponseFrom(product)).toList();

        return productResponses;
    }

    public ProductResponse getProduct(String productId){
        Product product=productRepository.findById(productId).get();

        if(product==null){
            throw new RuntimeException("no such product");
        }
 
        return ProductResponseFrom(product);
    }

    private ProductResponse ProductResponseFrom(Product product){
        ProductResponse productResponse=ProductResponse.builder()
            .description(product.getDescription())
            .id(product.getId())
            .name(product.getName())
            .price(product.getPrice())
            .build();

        return productResponse;
    }
}
