package com.example.aims.dto.order.response;

import com.example.aims.common.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOrderResponseDTO {
    private String orderID;
    private String customerName;
    private String phoneNumber;
    private OrderStatus status;
    private String shippingAddress;
    private String province;
    private Double totalAmount;
}
