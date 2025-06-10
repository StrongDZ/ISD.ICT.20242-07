package com.example.aims.dto;

import java.util.List;

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
