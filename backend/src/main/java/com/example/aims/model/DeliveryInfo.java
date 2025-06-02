package com.example.aims.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "delivery_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryInfo {

    @Id
    private Integer orderID;

    @OneToOne
    @JoinColumn(name = "orderID", insertable = false, updatable = false)
    private Order order;

    private String deliveryAddress;

    private String phoneNumber;

    private String recipientName;

    private String mail;

    private String province;
}
