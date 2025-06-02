package com.example.aims.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryInfoDTO {
    private String deliveryAddress;
    private String phoneNumber;
    private String recipientName;
    private String mail;
    private String province;
}