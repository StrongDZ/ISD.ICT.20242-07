package com.example.aims.controller;

import com.example.aims.dto.PayOrderResponseObjectDTO;
import com.example.aims.dto.transaction.TransactionRetrievalDTO;
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

    /**
     * Create a payment order
     * This method is called when the user initiates a payment for an order.
     * It will create a payment order and return the payment URL for the user to complete the payment.
     * @param orderId The ID of the order to be paid
     * @return String indicating the payment URL
     */
    @GetMapping("/url")
    public String getPaymentURL(@RequestParam("orderId") String orderId) {
        // Call the VNPay subsystem to get the payment URL
        return payOrderService.getPaymentURL(orderId);
    }

    /**
     * Process the payment return from VNPay
     * This method is called when the user returns from the VNPay payment page. 
     * @param vnpayResponse
     * @return String indicating the result of the payment processing
     * * This method will handle the response from VNPay, validate it, and update the order status accordingly.
     * If the payment is successful, it will update the order status to PAID and return a success message.
     * If the payment fails or is cancelled, it will update the order status to FAILED or CANCELLED and return an appropriate message.
     */
    @GetMapping("/vnpay-return")
    public String vnpayReturn(@RequestParam Map<String, String> vnpayResponse) {
        return payOrderService.processPayment(vnpayResponse);
    }

    // Transaction history (test)
    /**
     * Get transaction history by order ID
     * @param orderId
     * @return ResponseEntity with transaction details
     * @throws RuntimeException if the order ID is not found
     */
    @GetMapping("/transaction_history")
    public ResponseEntity<PayOrderResponseObjectDTO> getTransactionHistory(@RequestParam String orderId) {
        TransactionRetrievalDTO transactionDto = payOrderService.getPaymentHistory(orderId);
        return ResponseEntity.ok(PayOrderResponseObjectDTO.builder()
                .message("Get transaction history success")
                .responseCode(HttpStatus.OK.value())
                .data(transactionDto)
                .build());
    }
    // @GetMapping("/vnpay-refund")
}