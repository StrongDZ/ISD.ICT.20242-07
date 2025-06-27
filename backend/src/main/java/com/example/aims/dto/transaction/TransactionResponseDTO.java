package com.example.aims.dto.transaction;

import java.util.Date;

import com.example.aims.dto.order.PaymentOrderResponseFromReturnDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class TransactionResponseDTO {
    private String transactionId;
    private String transactionNo;
    private Double amount;
    private Date datetime;
    private PaymentOrderResponseFromReturnDTO order;
    private String paymentType;
}