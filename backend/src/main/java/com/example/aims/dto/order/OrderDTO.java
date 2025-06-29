package com.example.aims.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.DeliveryInfoDTO;

import java.util.List;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private String id;
    private OrderStatus status;
    private DeliveryInfoDTO deliveryInfo;
    private Double totalPrice;
    private Date orderDate;
    private List<OrderItemDTO> items;
    private String paymentMethod;
    private Boolean isRushOrder;
}