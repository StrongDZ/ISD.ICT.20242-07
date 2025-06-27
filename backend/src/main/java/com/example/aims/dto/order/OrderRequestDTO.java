package com.example.aims.dto.order;

import java.util.List;

import com.example.aims.dto.CartItemDTO;
import com.example.aims.dto.DeliveryInfoDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {
    private List<CartItemDTO> cartItems;
    private DeliveryInfoDTO deliveryInfo;
}
