package com.example.aims.dto;

import java.util.Date;

import com.example.aims.model.Order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class TransactionDto {
    private String orderID;
    private String transactionNo;
    private Double amount;
    private Date datetime;
    private PaymentOrderResponseDTO order;
}