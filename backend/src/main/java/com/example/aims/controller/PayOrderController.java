package com.example.aims.controller;

import com.example.aims.service.*;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.aims.subsystem.IPaymentSystem;
import com.example.aims.subsystem.VNPay.VNPaySubsystem;

@RestController
@RequestMapping("/api/payments")
public class PayOrderController {
    @Autowired
    private PayOrderService payOrderService;

    // Test payment request
    private final IPaymentSystem vnpay = new VNPaySubsystem();

    @GetMapping("/url")
    public String getPaymentURL(@RequestParam("orderId") String orderId) {
        // Call the VNPay subsystem to get the payment URL
        return payOrderService.getPaymentURL(orderId);
    }

    @GetMapping("/vnpay-return")
    public String vnpayReturn(@RequestParam Map<String, String> vnpayResponse) {
        // Call the VNPay subsystem to get transaction info
        return payOrderService.processPayment(vnpayResponse);
    }

    // @GetMapping("/vnpay-refund")
}