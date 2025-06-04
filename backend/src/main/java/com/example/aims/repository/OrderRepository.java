package com.example.aims.repository;

import com.example.aims.model.Order;
import com.example.aims.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findByOrderID(String orderID);

    List<Order> findByCustomer(Users customer);
    List<Order> findByStatus(String status);
}