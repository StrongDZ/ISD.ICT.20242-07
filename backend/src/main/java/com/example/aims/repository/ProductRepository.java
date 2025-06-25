package com.example.aims.repository;

import com.example.aims.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByCategory(String category);

    List<Product> findByTitleContainingIgnoreCase(String title);

    // Pagination support methods
    Page<Product> findByCategory(String category, Pageable pageable);

    Page<Product> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}