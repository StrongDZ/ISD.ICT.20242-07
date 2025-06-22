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
    private UsersDTO customer;
    private String customerName;
    private String phoneNumber;
    private OrderStatus status;
    private String shippingAddress;
    private String province;
    private Double totalAmount;
    private DeliveryInfoDTO deliveryInfo;
}