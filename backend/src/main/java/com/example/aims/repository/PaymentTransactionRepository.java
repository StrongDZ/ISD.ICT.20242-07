package com.example.aims.repository;

import com.example.aims.model.PaymentTransaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, String> {
    PaymentTransaction findByOrderID(String orderId);
}