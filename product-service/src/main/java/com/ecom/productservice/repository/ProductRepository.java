package com.ecom.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.ecom.productservice.model.Product;

public interface ProductRepository extends MongoRepository<Product, Long>{
    
}
