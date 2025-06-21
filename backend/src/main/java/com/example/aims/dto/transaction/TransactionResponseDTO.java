package com.example.aims.dto.transaction;

import java.util.Date;

import com.example.aims.dto.order.response.OrderResponseDTO;
import com.example.aims.dto.order.response.PaymentOrderResponseDTO;
import com.example.aims.model.Order;

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
    private OrderResponseDTO order;
}