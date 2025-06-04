package com.example.aims.controller;

import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.dto.InvoiceDTO;
import com.example.aims.dto.OrderDTO;
import com.example.aims.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = "*")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // @GetMapping("/orders")
    // public ResponseEntity<List<OrderDTO>> getCustomerOrders() {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     String customerID = authentication.getName();
        
    //     return ResponseEntity.ok(orderService.getCustomerOrders(customerID));
    // }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // @PostMapping("/orders")
    // public ResponseEntity<OrderDTO> createOrder(@RequestBody InvoiceDTO invoiceDTO) {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     String customerID = authentication.getName();
        
    //     return ResponseEntity.ok(orderService.createOrder(customerID, invoiceDTO));
    // }
}
