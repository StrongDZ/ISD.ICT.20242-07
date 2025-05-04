package com.example.aims.repository;

import com.example.aims.model.ShopItem;
import com.example.aims.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopItemRepository extends JpaRepository<ShopItem, ShopItem.ShopItemId> {
    List<ShopItem> findByManager(Users manager);
}