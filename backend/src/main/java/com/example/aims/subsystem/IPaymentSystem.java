package com.example.aims.subsystem;

import com.example.aims.model.Order;
import com.example.aims.model.PaymentTransaction;

public interface IPaymentSystem {
    public String getPaymentUrl(Order order);
    public String getRefundUrl(PaymentTransaction transaction);
    public PaymentTransaction getTransactionInfo();
}
