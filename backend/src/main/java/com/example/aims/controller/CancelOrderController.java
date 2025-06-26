package com.example.aims.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aims.dto.PayOrderResponseObjectDTO;
import com.example.aims.model.DeliveryInfo;
import com.example.aims.service.CancelOrderService;

@RestController
@RequestMapping("/api/cancel-order")
@CrossOrigin(origins = "*")
public class CancelOrderController {
    @Autowired
    private CancelOrderService cancelOrderService;

    @GetMapping("/")
    public ResponseEntity<PayOrderResponseObjectDTO> cancelOrder(String orderId, String transactionId) {
        String message = cancelOrderService.cancelOrder(orderId, transactionId);
        PayOrderResponseObjectDTO response = PayOrderResponseObjectDTO.builder()
                .responseCode(200)
                .message(null)
                .data(message)
                .build();
        return ResponseEntity.ok(response);
    }
}
