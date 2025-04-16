package com.example.aims.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private String id;
    private String customerID;
    private String status;
    private List<OrderItemDTO> items;
    private DeliveryInfoDTO deliveryInfo;
    private Float totalPrice;
}