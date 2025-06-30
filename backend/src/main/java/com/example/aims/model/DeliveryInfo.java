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
    
    private String city;
    private String district;
    private String addressDetail;
    private String phoneNumber;
    private String recipientName;
    private String mail;
    private Boolean isRushOrder;
    
    // Additional rush delivery information
    private String deliveryTime;
    private String specialInstructions;

}