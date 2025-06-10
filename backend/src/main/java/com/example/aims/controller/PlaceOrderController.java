package com.example.aims.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.dto.OrderDTO;
import com.example.aims.dto.OrderRequestDTO;
import com.example.aims.model.CartItem;
import com.example.aims.security.UserDetailsImpl;
import com.example.aims.service.PlaceOrderService;

@RestController
@RequestMapping("/api")
public class PlaceOrderController {

    private PlaceOrderService placeOrderService;

    public PlaceOrderController(PlaceOrderService placeOrderService) {
        this.placeOrderService = placeOrderService;
    }
    @PostMapping("/create-order")
    public ResponseEntity<OrderDTO> createOrder(UserDetailsImpl userDetails, @RequestBody OrderRequestDTO orderRequestDTO) {

        Integer userId = userDetails.getId();
        List<CartItem> cartItems = orderRequestDTO.getCartItems();
        DeliveryInfoDTO deliveryInfoDTO = orderRequestDTO.getDeliveryInfo();

        if (userId == null) {
            return ResponseEntity.ok(placeOrderService.createOrderNoAccount(
                cartItems, 
                deliveryInfoDTO
            ));
        } else {
            return ResponseEntity.ok(placeOrderService.createOrderWithAccount(
                cartItems, 
                deliveryInfoDTO,
                userId
            ));   
        }


}

}