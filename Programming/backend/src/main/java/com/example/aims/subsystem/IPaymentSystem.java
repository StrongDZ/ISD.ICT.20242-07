package com.example.aims.subsystem;

import java.util.Map;

import com.example.aims.model.Order;
import com.example.aims.model.PaymentTransaction;
import com.example.aims.repository.OrderRepository;
import com.example.aims.repository.PaymentTransactionRepository;

public interface IPaymentSystem {
    public String getPaymentUrl(Order order);

    public PaymentTransaction getTransactionInfo(Map<String, String> response, OrderRepository orderRepository, PaymentTransactionRepository transactionInfoRepository);

    public String refundTransaction(PaymentTransaction transactionInfoEntity);
}
