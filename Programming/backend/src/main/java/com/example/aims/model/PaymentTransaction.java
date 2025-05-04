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
    private String orderID;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "orderID")
    private Order order;
    
    private String content;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetime;
}