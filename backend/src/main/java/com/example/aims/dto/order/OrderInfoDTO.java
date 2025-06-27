package com.example.aims.dto.order;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.DeliveryInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfoDTO {
    private String orderID;
    private OrderStatus status;
    private Double totalAmount;
    private DeliveryInfoDTO deliveryInfo;

}