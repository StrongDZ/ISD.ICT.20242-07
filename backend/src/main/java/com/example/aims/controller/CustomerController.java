package com.example.aims.controller;

import com.example.aims.dto.CartItemDTO;
import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.dto.InvoiceDTO;
import com.example.aims.dto.OrderDTO;
import com.example.aims.service.CartService;
import com.example.aims.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CartService cartService;
    private final OrderService orderService;

    public CustomerController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    // Cart endpoints
    @GetMapping("/cart")
    public ResponseEntity<List<CartItemDTO>> getCartItems() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String customerID = authentication.getName();
        
        return ResponseEntity.ok(cartService.getCartItems(customerID));
    }

    @PostMapping("/cart/{productId}")
    public ResponseEntity<CartItemDTO> addToCart(@PathVariable String productId, @RequestParam Integer quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String customerID = authentication.getName();
        
        return ResponseEntity.ok(cartService.addToCart(customerID, productId, quantity));
    }

    @PutMapping("/cart/{productId}")
    public ResponseEntity<CartItemDTO> updateCartItem(@PathVariable String productId, @RequestParam Integer quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String customerID = authentication.getName();
        
        return ResponseEntity.ok(cartService.updateCartItem(customerID, productId, quantity));
    }

    @DeleteMapping("/cart/{productId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable String productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String customerID = authentication.getName();
        
        cartService.removeFromCart(customerID, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cart")
    public ResponseEntity<Void> clearCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String customerID = authentication.getName();
        
        cartService.clearCart(customerID);
        return ResponseEntity.noContent().build();
    }

    // Order endpoints
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getCustomerOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String customerID = authentication.getName();
        
        return ResponseEntity.ok(orderService.getCustomerOrders(customerID));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody InvoiceDTO invoiceDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String customerID = authentication.getName();
        
        return ResponseEntity.ok(orderService.createOrder(customerID, invoiceDTO));
    }
}