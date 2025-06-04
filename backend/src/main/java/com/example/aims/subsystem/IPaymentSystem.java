package com.example.aims.subsystem;

import java.util.Map;

import com.example.aims.model.Order;
import com.example.aims.model.PaymentTransaction;
import com.example.aims.repository.OrderRepository;

public interface IPaymentSystem {
    public String getPaymentUrl(Order order);
    public String getRefundInfo(PaymentTransaction transaction);
    public PaymentTransaction getTransactionInfo(Map<String, String> vnPayResponse, OrderRepository orderRepository);
}
