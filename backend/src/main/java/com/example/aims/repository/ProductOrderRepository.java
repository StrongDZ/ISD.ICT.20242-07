package com.example.aims.repository;

import com.example.aims.model.ProductOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrderEntity, Integer>  {
    List<ProductOrderEntity> findByOrder_OrderID(String orderId);

}