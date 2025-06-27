package com.example.aims.repository;

import com.example.aims.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Optional<Order> findByOrderID(String orderID);

    // List<Order> findByCustomer(Users customer);

    List<Order> findByStatus(String status);
}