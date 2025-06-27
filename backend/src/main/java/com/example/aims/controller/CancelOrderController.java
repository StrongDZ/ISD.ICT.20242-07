package com.example.aims.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.aims.dto.PayOrderResponseObjectDTO;
import com.example.aims.service.CancelOrderService;

@RestController
@RequestMapping("/api/cancel-order")
@CrossOrigin(origins = "*")
public class CancelOrderController {
    @Autowired
    private CancelOrderService cancelOrderService;

    @GetMapping("/")
    public ResponseEntity<PayOrderResponseObjectDTO> cancelOrder(@RequestParam("orderId") String orderId, @RequestParam("transactionId") String transactionId, @RequestParam("paymentType") String paymentType) {
        String message = cancelOrderService.cancelOrder(orderId, transactionId, paymentType);
        PayOrderResponseObjectDTO response = PayOrderResponseObjectDTO.builder()
                .responseCode(200)
                .message(null)
                .data(message)
                .build();
        return ResponseEntity.ok(response);
    }
}
