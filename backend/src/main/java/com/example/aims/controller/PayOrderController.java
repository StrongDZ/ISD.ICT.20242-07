package com.example.aims.controller;

import com.example.aims.dto.PayOrderResponseObjectDTO;
import com.example.aims.dto.transaction.TransactionDto;
import com.example.aims.service.*;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.aims.subsystem.IPaymentSystem;
import com.example.aims.subsystem.VNPay.VNPaySubsystem;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")

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

    // Transaction history (test)
    @GetMapping("/transaction_history")
    public ResponseEntity<PayOrderResponseObjectDTO> getTransactionHistory(@RequestParam String orderId) {
        TransactionDto transactionDto = payOrderService.getPaymentHistory(orderId);
        return ResponseEntity.ok(PayOrderResponseObjectDTO.builder()
                .message("Get transaction history success")
                .responseCode(HttpStatus.OK.value())
                .data(transactionDto)
                .build());
    }
    // @GetMapping("/vnpay-refund")
}