package com.ecom.productservice.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.ecom.productservice.model.Product;

public interface ProductRepository extends MongoRepository<Product, Long>{
    @Query("{'_id': {$gt: ?0} }")
    List<Product> findProductsWithGreaterThanId(Long id, Pageable pageable);

    @Query("{'_id': {$lt: ?0} }")
    List<Product> findProductsWithLessThanId(Long id, Pageable pageable);

}
