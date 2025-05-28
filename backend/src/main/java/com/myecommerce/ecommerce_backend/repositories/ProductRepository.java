package com.myecommerce.ecommerce_backend.repositories;

import com.myecommerce.ecommerce_backend.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Optional: if you want to add custom query methods returning List

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // You can add custom query methods here if needed in the future
    // For example:
    // List<Product> findByCategory(String category);
    // List<Product> findByNameContainingIgnoreCase(String name);

}
