package com.example.aims.repository;

import com.example.aims.model.DVD;
import com.example.aims.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionInfoRepository extends JpaRepository<PaymentTransaction, Integer> {
}
