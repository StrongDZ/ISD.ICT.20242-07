package com.example.aims.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.DeliveryInfoDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private String id;
    // private Integer customerID;
    private OrderStatus status;
    // private List<OrderItemDTO> items;
    private DeliveryInfoDTO deliveryInfo;
    private Double totalPrice;
}