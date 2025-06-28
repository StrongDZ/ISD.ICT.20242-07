package com.example.aims.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.PayOrderResponseObjectDTO;
import com.example.aims.service.CancelOrderService;

@RestController
@RequestMapping("/api/cancel-order")
@CrossOrigin(origins = "*")
public class CancelOrderController {
    @Autowired
    private CancelOrderService cancelOrderService;

    /**
     * Cancel order
     * This method is used to cancel the order.
     * 
     * @param orderId
     * @param transactionId
     * @param paymentType
     * @return ResponseEntity with success message
     */
    @GetMapping("")
    public ResponseEntity<Boolean> cancelOrder(@RequestParam("orderId") String orderId,
            @RequestParam("transactionId") String transactionId, @RequestParam("paymentType") String paymentType) {
        OrderStatus status = cancelOrderService.cancelOrder(orderId, transactionId, paymentType);
        return ResponseEntity.ok(status == OrderStatus.CANCELLED);
    }
}
