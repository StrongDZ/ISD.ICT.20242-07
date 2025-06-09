package com.example.aims.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.aims.dto.OrderDTO;
import com.example.aims.dto.OrderRequestDTO;
import com.example.aims.service.OrderService;

@RestController
@RequestMapping("/api")
public class PlaceOrderController {

    private OrderService orderService;

    public PlaceOrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @PostMapping("/create-order")
    public ResponseEntity<OrderDTO> createOrderFromCart(@RequestBody OrderRequestDTO orderRequestDTO) {
        return ResponseEntity.ok(orderService.createOrderFromCart(orderRequestDTO.getCustomerId(), orderRequestDTO.getDeliveryInfo()));   
    }

}
