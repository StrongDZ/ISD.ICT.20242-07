package com.example.aims.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

import com.example.aims.model.Order;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private String orderID;
    private Order order;

    private String content;
    private Date datetime;
}
