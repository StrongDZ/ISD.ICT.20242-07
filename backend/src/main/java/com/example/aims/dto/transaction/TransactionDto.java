package com.example.aims.dto.transaction;

import java.util.Date;

import com.example.aims.dto.PaymentOrderResponseDTO;
import com.example.aims.model.Order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class TransactionDto {
    private String transactionId;
    private String transactionNo;
    private Double amount;
    private Date datetime;
    private PaymentOrderResponseDTO order;
}