package com.MicroserviceP1.ProductService.Repository;

import com.MicroserviceP1.ProductService.Model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
