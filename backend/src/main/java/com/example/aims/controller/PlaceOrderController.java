package com.example.aims.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.aims.dto.order.OrderDTO;
import com.example.aims.dto.order.OrderRequestDTO;
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
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderRequestDTO request) {
        OrderDTO order = placeOrderService.createOrder(request);
        return ResponseEntity.ok(order);
    }

}