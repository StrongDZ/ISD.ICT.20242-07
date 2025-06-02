package com.example.aims.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DeliveryInfo")
public class DeliveryInfo {
    
    @Id
    private String orderID;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "orderID")
    private Order order;
    
    private String city;
    private String district;
    private String addressDetail;
    private String phoneNumber;
    private String recipientName;
    private String mail;
    
}