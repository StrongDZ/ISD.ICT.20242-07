package com.example.aims.controller;

import com.example.aims.dto.OrderDTO;
import com.example.aims.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // @PostMapping("/delivery-info")
    // public ResponseEntity<DeliveryInfoDTO> createDeliveryInfo(@RequestBody DeliveryInfoDTO deliveryInfoDTO) {

    // }
    // @PostMapping("/orders")
    // public ResponseEntity<OrderDTO> createOrder(@RequestBody InvoiceDTO invoiceDTO) {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     String customerID = authentication.getName();
        
    //     return ResponseEntity.ok(orderService.createOrder(customerID, invoiceDTO));
    // }
}
