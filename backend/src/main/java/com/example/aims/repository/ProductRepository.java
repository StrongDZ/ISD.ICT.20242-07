package com.example.aims.repository;

import com.example.aims.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByCategory(String category);
    List<Product> findByTitleContainingIgnoreCase(String title);
    default void updateProductQuantity(String id, int newQuantity) {
            Product product = findById(id).orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
            product.setQuantity(newQuantity);
            save(product);
        };
}