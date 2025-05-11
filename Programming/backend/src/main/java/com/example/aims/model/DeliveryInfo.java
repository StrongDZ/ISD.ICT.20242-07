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
     // Cohesion: High – fields and methods focus on delivery information.
    // SRP: Not violated – class is a simple DTO for delivery data.
    @Id
    private String orderID;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "orderID")
    private Order order;
    
    private String deliveryAddress;
    private String phoneNumber;
    private String recipientName;
    private String mail;
    private String province;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}