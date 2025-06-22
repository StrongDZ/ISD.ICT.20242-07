package com.example.aims.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.aims.dto.transaction.TransactionResponseDTO;
import com.example.aims.dto.transaction.TransactionRetrievalDTO;
import com.example.aims.model.PaymentTransaction;

@Component
public class TransactionMapper {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * Converts a PaymentTransaction entity to a TransactionResponseDTO.
     * @param transaction
     * @return TransactionResponseDTO
     */
    public TransactionResponseDTO toTransactionResponseDTO(PaymentTransaction transaction) {
        if (transaction == null) {
            return null;
        }
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setTransactionNo(transaction.getTransactionNo());
        dto.setAmount(transaction.getAmount());
        dto.setDatetime(transaction.getDatetime());
        dto.setOrder(orderMapper.toPaymentOrderResponseFromReturnDTO(transaction.getOrder())); // Assuming you want to set order details as null for now
        return dto;
    }
    /**
     * Converts a PaymentTransaction entity to a TransactionRetrievalDTO.
     * @param transaction
     * @return TransactionRetrievalDTO
     */
    public TransactionRetrievalDTO toTransactionRetrievalDTO(PaymentTransaction transaction) {
        if (transaction == null) {
            return null;
        }
        TransactionRetrievalDTO dto = new TransactionRetrievalDTO();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setTransactionNo(transaction.getTransactionNo());
        dto.setAmount(transaction.getAmount());
        dto.setDatetime(transaction.getDatetime());
        dto.setOrder(orderMapper.toPaymentOrderRetrievalDTO(transaction.getOrder())); // Assuming you want to set order details as null for now
        return dto;
    }
}
