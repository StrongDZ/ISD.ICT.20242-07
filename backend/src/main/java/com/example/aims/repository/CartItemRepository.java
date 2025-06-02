package com.example.aims.repository;

import com.example.aims.model.CartItem;
import com.example.aims.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, CartItem.CartItemId> {
    List<CartItem> findByCustomer(Users customer);
    void deleteByCustomer(Users customer);
}