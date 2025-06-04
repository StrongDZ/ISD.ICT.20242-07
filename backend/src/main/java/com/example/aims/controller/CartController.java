package com.example.aims.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import com.example.aims.dto.CartItemDTO;
import com.example.aims.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor    
public class CartController {
        private final CartService cartService;
        // Cart endpoints
        @GetMapping
        public ResponseEntity<List<CartItemDTO>> getCartItems() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String customerID = authentication.getName();
            
            return ResponseEntity.ok(cartService.getCartItems(customerID));
        }
    
        @PostMapping("/{productId}")
        public ResponseEntity<CartItemDTO> addToCart(@PathVariable String productId, @RequestParam Integer quantity) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String customerID = authentication.getName();
            
            return ResponseEntity.ok(cartService.addToCart(customerID, productId, quantity));
        }
    
        @PutMapping("{productId}")
        public ResponseEntity<CartItemDTO> updateCartItem(@PathVariable String productId, @RequestParam Integer quantity) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String customerID = authentication.getName();
            
            return ResponseEntity.ok(cartService.updateCartItem(customerID, productId, quantity));
        }
    
        @DeleteMapping("{productId}")
        public ResponseEntity<Void> removeFromCart(@PathVariable String productId) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String customerID = authentication.getName();
            
            cartService.removeFromCart(customerID, productId);
            return ResponseEntity.noContent().build();
        }
    
        @DeleteMapping()
        public ResponseEntity<Void> clearCart() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String customerID = authentication.getName();
            
            cartService.clearCart(customerID);
            return ResponseEntity.noContent().build();
        }
}
