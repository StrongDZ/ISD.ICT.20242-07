package com.example.aims.repository;

import com.example.aims.model.Product;
import com.example.aims.common.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
        @Query("SELECT p FROM Product p " +
        "WHERE (:keyword IS NULL OR :keyword = '' OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
        "AND (:category IS NULL OR p.category = :category) " +
        "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
        "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
        Page<Product> searchProducts(
                @Param("keyword") String keyword,
                @Param("category") ProductType category,
                @Param("minPrice") Double minPrice,
                @Param("maxPrice") Double maxPrice,
                Pageable pageable);
 
}