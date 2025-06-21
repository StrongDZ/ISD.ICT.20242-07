package com.example.aims.dto.order.request;

import java.util.List;

import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.model.CartItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {
    private List<CartItem> cartItems;
    private DeliveryInfoDTO deliveryInfo;
}
