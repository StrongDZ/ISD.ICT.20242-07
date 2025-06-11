package com.example.aims.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.aims.dto.OrderDTO;
import com.example.aims.dto.OrderRequestDTO;
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
    public ResponseEntity<OrderDTO> createOrder(UserDetailsImpl userDetails, @RequestBody OrderRequestDTO request) {
        Integer userId = (userDetails != null) ? userDetails.getId() : null;
        OrderDTO order = (userId == null)
                ? handleOrderNoAccount(request)
                : handleOrderWithAccount(request, userId);
        return ResponseEntity.ok(order);
    }

    private OrderDTO handleOrderNoAccount(OrderRequestDTO request) {
        return placeOrderService.createOrderNoAccount(
                request.getCartItems(),
                request.getDeliveryInfo());
    }

    private OrderDTO handleOrderWithAccount(OrderRequestDTO request, Integer userId) {
        return placeOrderService.createOrderWithAccount(
                request.getCartItems(),
                request.getDeliveryInfo(),
                userId);
    }

}