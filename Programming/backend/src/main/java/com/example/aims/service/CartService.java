package com.example.aims.service;

import com.example.aims.dto.CartItemDTO;
import com.example.aims.exception.BadRequestException;
import com.example.aims.exception.ResourceNotFoundException;
import com.example.aims.model.CartItem;
import com.example.aims.model.Product;
import com.example.aims.model.Users;
import com.example.aims.repository.CartItemRepository;
import com.example.aims.repository.ProductRepository;
import com.example.aims.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UsersRepository userRepository;
    private final ProductRepository productRepository;

    public List<CartItemDTO> getCartItems(String customerId) {
        Users customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Users", "id", customerId));
        
        List<CartItem> cartItems = cartItemRepository.findByCustomer(customer);
        List<CartItemDTO> cartItemDTOs = new ArrayList<>();
        
        for (CartItem cartItem : cartItems) {
            CartItemDTO dto = new CartItemDTO();
            dto.setProductID(cartItem.getProduct().getProductID());
            dto.setProductTitle(cartItem.getProduct().getTitle());
            dto.setProductPrice(cartItem.getProduct().getPrice());
            dto.setQuantity(cartItem.getQuantity());
            dto.setImageURL(cartItem.getProduct().getImageURL());
            
            cartItemDTOs.add(dto);
        }
        
        return cartItemDTOs;
    }

    @Transactional
    public CartItemDTO addToCart(String customerId, String productId, Integer quantity) {
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }
        
        Users customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Users", "id", customerId));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        // Check if product is in stock
        if (product.getQuantity() < quantity) {
            throw new BadRequestException("Not enough stock available. Available: " + product.getQuantity());
        }
        
        // Check if the product is already in the cart
        CartItem.CartItemId cartItemId = new CartItem.CartItemId(customerId, productId);
        Optional<CartItem> existingCartItem = cartItemRepository.findById(cartItemId);
        
        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            
            // Check if the new quantity exceeds available stock
            if (newQuantity > product.getQuantity()) {
                throw new BadRequestException("Not enough stock available. Available: " + product.getQuantity());
            }
            
            cartItem.setQuantity(newQuantity);
        } else {
            cartItem = new CartItem();
            cartItem.setId(cartItemId);
            cartItem.setCustomer(customer);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        }
        
        cartItemRepository.save(cartItem);
        
        CartItemDTO dto = new CartItemDTO();
        dto.setProductID(product.getProductID());
        dto.setProductTitle(product.getTitle());
        dto.setProductPrice(product.getPrice());
        dto.setQuantity(cartItem.getQuantity());
        dto.setImageURL(product.getImageURL());
        
        return dto;
    }

    @Transactional
    public CartItemDTO updateCartItem(String customerId, String productId, Integer quantity) {
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }
        
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        // Check if product is in stock
        if (product.getQuantity() < quantity) {
            throw new BadRequestException("Not enough stock available. Available: " + product.getQuantity());
        }
        
        CartItem.CartItemId cartItemId = new CartItem.CartItemId(customerId, productId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        
        CartItemDTO dto = new CartItemDTO();
        dto.setProductID(product.getProductID());
        dto.setProductTitle(product.getTitle());
        dto.setProductPrice(product.getPrice());
        dto.setQuantity(cartItem.getQuantity());
        dto.setImageURL(product.getImageURL());
        
        return dto;
    }

    @Transactional
    public void removeFromCart(String customerId, String productId) {
        CartItem.CartItemId cartItemId = new CartItem.CartItemId(customerId, productId);
        
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new ResourceNotFoundException("Cart item not found");
        }
        
        cartItemRepository.deleteById(cartItemId);
    }

    @Transactional
    public void clearCart(String customerId) {
        Users customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Users", "id", customerId));
        
        cartItemRepository.deleteByCustomer(customer);
    }
}
