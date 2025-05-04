package com.example.aims.controller;

import com.example.aims.dto.OrderDTO;
import com.example.aims.dto.RegisterRequest;
import com.example.aims.model.Users;
import com.example.aims.repository.UsersRepository;
import com.example.aims.service.AuthService;
import com.example.aims.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final UsersRepository userRepository;
    private final AuthService authService;
    private final OrderService orderService;

    public AdminController(UsersRepository userRepository, AuthService authService, OrderService orderService) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.orderService = orderService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUsers(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getOrdersByStatus("ALL");
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/status/{status}")
    public ResponseEntity<List<OrderDTO>> getOrdersByStatus(@PathVariable String status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable String orderId, @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }
}