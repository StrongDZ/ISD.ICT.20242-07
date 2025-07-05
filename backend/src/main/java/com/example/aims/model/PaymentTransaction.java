package com.example.aims.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "PaymentTransaction")


public class PaymentTransaction {


    @Id
    @Column(name = "transaction_Id")
    private String transactionId;

    @OneToOne
    @JoinColumn(name = "orderID")
    private Order order;
    @Column(name = "transaction_No")
    private String transactionNo;
    @Column(name = "transaction_bank")
    private String transactionBank;
    @Column(name = "transaction_status")
    private String transactionStatus;
    @Column(name = "card_type")
    private String cardType;
    @Column(name = "amount", nullable = false)
    private Double amount;
    @Column(name = "datetime", nullable = false)
    private Date datetime;
    @Column(name = "payment_type")
    private String paymentType;
}