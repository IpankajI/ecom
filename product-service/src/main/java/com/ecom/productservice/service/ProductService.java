package com.ecom.productservice.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ecom.productservice.dto.ProductRequest;
import com.ecom.productservice.model.Product;
import com.ecom.productservice.repository.ProductRepository;
import com.ecom.productservice.utils.IDGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {


    private final ProductRepository productRepository;
    private final Logger logger;
    private final IDGenerator idGenerator;

    public void addProduct(ProductRequest productRequest){
        Product product= Product.builder()
            .id(idGenerator.next())
            .name(productRequest.getName())
            .description(productRequest.getDescription())
            .price(BigDecimal.valueOf(Long.valueOf(productRequest.getPrice())))
            .build();

        productRepository.save(product);
        log.info("product with id: {} created", product.getId());
    }

    public List<Product> getProducts(Long before, Long after, Integer limit){
        if(before>0){
            Pageable pageable=PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC,"_id"));
            return productRepository.findProductsWithLessThanId(before, pageable).reversed();
        }
        Pageable pageable=PageRequest.of(0, limit, Sort.by(Sort.Direction.ASC,"_id"));
        return productRepository.findProductsWithGreaterThanId(after, pageable);
    }

    public Product getProduct(Long productId){
        Optional<Product> product=productRepository.findById(productId);

        if(!product.isPresent()){
            logger.error("no such product");
            return null;
        }
 
        return product.get();
    }

}
