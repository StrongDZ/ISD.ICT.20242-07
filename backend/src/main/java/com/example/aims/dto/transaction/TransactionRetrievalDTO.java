package com.example.aims.dto.transaction;

import java.util.Date;

import com.example.aims.dto.order.PaymentOrderRetrievalDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class TransactionRetrievalDTO {
    private String transactionId;
    private String transactionNo;
    private Double amount;
    private Date datetime;
    private PaymentOrderRetrievalDTO order;
}