package com.example.aims.subsystem;

import java.util.Map;

import com.example.aims.dto.order.PaymentOrderRequestDTO;
import com.example.aims.dto.order.PaymentOrderResponseFromReturnDTO;
import com.example.aims.dto.transaction.TransactionResponseDTO;
import com.example.aims.model.PaymentTransaction;

public interface IPaymentSystem {
    public String getPaymentUrl(PaymentOrderRequestDTO dto);

    public String getRefundInfo(TransactionResponseDTO dto);

    public PaymentTransaction getTransactionInfo(Map<String, String> vnPayResponse,
            PaymentOrderResponseFromReturnDTO orderDto);

    /**
     * Returns the payment type identifier for this payment system.
     * 
     * @return The payment type (e.g., "vnpay", "momo")
     */
    public String getPaymentType();
}