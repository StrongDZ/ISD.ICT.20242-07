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

    // Get all cart items
    @GetMapping
    public ResponseEntity<List<CartItemDTO>> getCartItems(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Integer customerId = userDetails.getId();
        List<CartItemDTO> cartItems = cartService.getCartItems(customerId);
        return ResponseEntity.ok(cartItems);
    }

    // Add product to cart
    @PostMapping
    public ResponseEntity<CartItemDTO> addToCart(@RequestBody CartItemDTO cartItemDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Integer customerId = userDetails.getId();
        String productId = cartItemDTO.getProductDTO().getProductID();
        Integer quantity = cartItemDTO.getQuantity();

        CartItemDTO cartItem = cartService.addToCart(customerId, productId, quantity);
        return ResponseEntity.ok(cartItem);
    }

    // Update cart item
    @PutMapping
    public ResponseEntity<CartItemDTO> updateCartItem(@RequestBody CartItemDTO cartItemDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Integer customerId = userDetails.getId();
        String productId = cartItemDTO.getProductDTO().getProductID();
        Integer quantity = cartItemDTO.getQuantity();

        CartItemDTO cartItem = cartService.updateCartItem(customerId, productId, quantity);
        if (cartItem == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cartItem);
    }

    // Remove product from cart
    @DeleteMapping
    public ResponseEntity<Void> removeFromCart(@RequestBody CartItemDTO cartItemDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Integer customerId = userDetails.getId();
        String productId = cartItemDTO.getProductDTO().getProductID();

        cartService.removeFromCart(customerId, productId);
        return ResponseEntity.ok().build();
    }

    // Clear entire cart
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Integer customerId = userDetails.getId();
        cartService.clearCart(customerId);
        return ResponseEntity.ok().build();
    }
}
