package com.example.aims.subsystem;

import java.util.Map;

import com.example.aims.dto.PaymentOrderRequestDTO;
import com.example.aims.dto.order.OrderResponseDTO;
import com.example.aims.dto.transaction.TransactionDto;
import com.example.aims.dto.transaction.TransactionResponseDTO;
import com.example.aims.model.Order;
import com.example.aims.model.PaymentTransaction;

public interface IPaymentSystem {
    public String getPaymentUrl(PaymentOrderRequestDTO dto);

    public String getRefundInfo(TransactionResponseDTO dto);

    public PaymentTransaction getTransactionInfo(Map<String, String> vnPayResponse, OrderResponseDTO orderDto);
}