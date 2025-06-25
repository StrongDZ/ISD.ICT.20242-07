package com.example.aims.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.List;
import com.example.aims.dto.CartItemDTO;
import com.example.aims.service.CartService;
import com.example.aims.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    // Cart endpoints
    @GetMapping
    public ResponseEntity<List<CartItemDTO>> getCartItems(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Integer customerId = userDetails.getId();
        List<CartItemDTO> cartItems = cartService.getCartItems(customerId);
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/{productId}")
    public ResponseEntity<CartItemDTO> addToCart(@PathVariable String productId, @RequestParam Integer quantity,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Integer customerId = userDetails.getId();
        CartItemDTO cartItem = cartService.addToCart(customerId, productId, quantity);
        return ResponseEntity.ok(cartItem);
    }

    @PutMapping("{productId}")
    public ResponseEntity<CartItemDTO> updateCartItem(@PathVariable String productId, @RequestParam Integer quantity,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Integer customerId = userDetails.getId();
        CartItemDTO cartItem = cartService.updateCartItem(customerId, productId, quantity);
        // If the cart item is not found, return a 404 response
        if (cartItem == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cartItem);
    }

    @DeleteMapping("{productId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable String productId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Integer customerId = userDetails.getId();
        cartService.removeFromCart(customerId, productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Integer customerId = userDetails.getId();

        cartService.clearCart(customerId);
        return ResponseEntity.ok().build();
    }
}
