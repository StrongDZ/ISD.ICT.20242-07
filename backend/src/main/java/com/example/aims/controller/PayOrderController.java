package com.example.aims.controller;

import com.example.aims.dto.PayOrderResponseObjectDTO;
import com.example.aims.dto.transaction.TransactionResponseDTO;

import com.example.aims.dto.order.OrderInfoDTO;
import com.example.aims.dto.order.OrderItemDTO;

import com.example.aims.service.*;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")

public class PayOrderController {
    @Autowired
    private PayOrderService payOrderService;

    // Test payment request
    // private final IPaymentSystem vnpay = new VNPaySubsystem();

    /**
     * Create a payment order
     * This method is called when the user initiates a payment for an order.
     * It will create a payment order and return the payment URL for the user to
     * complete the payment.
     * 
     * @param orderId     The ID of the order to be paid
     * @param paymentType Loại cổng thanh toán (vnpay, momo, ...)
     * @return String indicating the payment URL
     */
    @GetMapping("/url")
    public String getPaymentURL(@RequestParam("orderId") String orderId,
            @RequestParam("paymentType") String paymentType) {
        // Call the payment subsystem to get the payment URL
        return payOrderService.getPaymentURL(orderId, paymentType);
    }

    /**
     * Process the payment return from payment gateway
     * This method is called when the user returns from the payment page.
     * 
     * @param paymentResponse Response từ cổng thanh toán
     * @return String indicating the result of the payment processing
     *         * This method will handle the response from payment gateway, validate
     *         it, and
     *         update the order status accordingly.
     *         If the payment is successful, it will update the order status to PAID
     *         and return a success message.
     *         If the payment fails or is cancelled, it will update the order status
     *         to FAILED or CANCELLED and return an appropriate message.
     */

    // Giữ lại endpoint cũ cho backward compatibility
    @GetMapping("/vnpay-return")
    public RedirectView vnpayReturn(@RequestParam Map<String, String> vnpayResponse) {
        // Lấy orderId từ response để tìm paymentType từ database
        String orderId = vnpayResponse.get("vnp_TxnRef");
        String redirectUrl = payOrderService.processPaymentReturn(vnpayResponse, orderId);
        return new RedirectView(redirectUrl);
    }

    // Transaction history (test)
    /**
     * Get transaction history by order ID
     * 
     * @param orderId
     * @return ResponseEntity with transaction details
     * @throws RuntimeException if the order ID is not found
     */
    @GetMapping("/transaction_history")
    public ResponseEntity<PayOrderResponseObjectDTO> getTransactionHistory(@RequestParam String orderId) {
        TransactionResponseDTO transactionDto = payOrderService.getPaymentHistory(orderId);
        return ResponseEntity.ok(PayOrderResponseObjectDTO.builder()
                .message("Get transaction history success")
                .responseCode(HttpStatus.OK.value())
                .data(transactionDto)
                .build());
    }

    /**
     * Get order info
     * This method is used to get the order information.
     * 
     * @param id
     * @return
     */
    // Get order info
    @GetMapping("order_info")
    public ResponseEntity<PayOrderResponseObjectDTO> getOrderInfo(@RequestParam String id) {
        OrderInfoDTO orderInfoDto = payOrderService.getOrderInfo(id);
        return ResponseEntity.ok(PayOrderResponseObjectDTO.builder()
                .message("Get order info success")
                .responseCode(HttpStatus.OK.value())
                .data(orderInfoDto)
                .build());
    }

    /**
     * Get order product
     * This method is used to get the product information of the order.
     * 
     * @param id
     * @return
     */
    // Get order product
    @GetMapping("order_product")
    public ResponseEntity<PayOrderResponseObjectDTO> getProductInfo(@RequestParam String id) {
        List<OrderItemDTO> productDtos = payOrderService.getOrderProduct(id);
        return ResponseEntity.ok(PayOrderResponseObjectDTO.builder()
                .message("Get order product success")
                .responseCode(HttpStatus.OK.value())
                .data(productDtos)
                .build());
    }
}
