package com.example.aims.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Invoice")
public class Invoice {
    
    @Id
    private Integer orderID;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "orderID")
    private Order order;
    
    private Double productPriceExcludingVAT;
    private Double productPriceIncludingVAT;
    private Double deliveryFee;
}