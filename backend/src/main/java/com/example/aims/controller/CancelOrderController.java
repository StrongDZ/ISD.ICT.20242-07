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

    @GetMapping("/payment_test")
    public String test() {
        // New delivery info
        DeliveryInfo deliveryInfoEntity = new DeliveryInfo();
        deliveryInfoEntity.setAddress("Hanoi");
        deliveryInfoEntity.setPhone("0123456789");
        deliveryInfoEntity.setEmail("dontunderstandyou12345@gmail.com");
        // Test payment
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(1);
        orderEntity.setOrderDate("2021-09-01");
        orderEntity.setShippingFee(100000.0);
        orderEntity.setDeliveryInfo(deliveryInfoEntity);
        return "redirect:" + vnpay.getPaymentUrl(orderEntity);
    }
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
