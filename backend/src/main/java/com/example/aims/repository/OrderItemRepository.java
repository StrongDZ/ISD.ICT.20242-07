package com.example.aims.repository;

import com.example.aims.model.Order;
import com.example.aims.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItem.OrderItemId> {
    List<OrderItem> findByOrder(Order order);
}