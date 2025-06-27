package com.example.aims.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.aims.common.OrderStatus;
import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.dto.UsersDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderResponseFromReturnDTO {
    private String orderID;
    private OrderStatus status;
    private Double totalAmount;
    private DeliveryInfoDTO deliveryInfo;
    private String paymentType;
}