package com.example.aims.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.aims.dto.CartItemDTO;
import com.example.aims.dto.order.OrderDTO;
import com.example.aims.dto.order.OrderRequestDTO;
import com.example.aims.service.PlaceOrderService;

@RestController
@RequestMapping("/api")
public class PlaceOrderController {

    private PlaceOrderService placeOrderService;

    public PlaceOrderController(PlaceOrderService placeOrderService) {
        this.placeOrderService = placeOrderService;
    }

    @PostMapping("/check-inventory")
    public ResponseEntity<?> checkInventory(@RequestBody List<CartItemDTO> cartItems) {
        List<CartItemDTO> insufficientItems = placeOrderService.checkInventoryAvailability(cartItems);
        
        Map<String, Object> response = new HashMap<>();
        
        if (insufficientItems.isEmpty()) {
            response.put("success", true);
            response.put("message", "Tồn kho đủ cho tất cả sản phẩm");
            response.put("insufficientItems", null);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Tồn kho không đủ cho một số sản phẩm. Vui lòng cập nhật giỏ hàng.");
            response.put("insufficientItems", insufficientItems);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/create-order")
    public ResponseEntity<OrderDTO> placeOrder(@RequestBody OrderRequestDTO request) {
        OrderDTO order = placeOrderService.placeOrder(request);
        return ResponseEntity.ok(order);
    }

}