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

    // COHESION: Procedural cohesion — this class contains a sequence of operations related to cart functionality,
    // but each method handles different, mostly unrelated tasks (e.g., getCartItems vs addToCart).
    // SRP VIOLATION: This class is responsible for multiple concerns: data validation, DTO mapping, and cart operations.
    // RECOMMENDATION: Split into multiple components to follow SRP and improve cohesion.

    private final CartItemRepository cartItemRepository;
    private final UsersRepository userRepository;
    private final ProductRepository productRepository;

    public List<CartItemDTO> getCartItems(Integer customerId) {
        // This method retrieves all items in a user's cart and maps them to DTOs.
        // RESPONSIBILITIES: (1) Fetching cart data, (2) Mapping domain objects to DTOs.
        // SRP VIOLATION: Mapping should be delegated to a CartItemMapper or DTOFactory class.

        Users customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Users", "id", customerId));

        List<CartItem> cartItems = cartItemRepository.findByCustomer(customer);
        List<CartItemDTO> cartItemDTOs = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            // Mapping logic — candidate for extraction
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
    public CartItemDTO addToCart(Integer customerId, String productId, Integer quantity) {
        // This method handles the logic of adding an item to the cart, validating user/product,
        // checking stock, and returning a DTO.
        // SRP VIOLATION: This method combines business logic (stock check), persistence, and mapping.
        // SUGGESTION: Extract stock validation to InventoryService, and DTO creation to CartItemMapper.

        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }

        Users customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Users", "id", customerId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // Stock validation logic — should be separated
        if (product.getQuantity() < quantity) {
            throw new BadRequestException("Not enough stock available. Available: " + product.getQuantity());
        }

        CartItem.CartItemId cartItemId = new CartItem.CartItemId(customerId, productId);
        Optional<CartItem> existingCartItem = cartItemRepository.findById(cartItemId);

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;

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

        // Mapping logic — can be delegated
        CartItemDTO dto = new CartItemDTO();
        dto.setProductID(product.getProductID());
        dto.setProductTitle(product.getTitle());
        dto.setProductPrice(product.getPrice());
        dto.setQuantity(cartItem.getQuantity());
        dto.setImageURL(product.getImageURL());

        return dto;
    }

    @Transactional
    public CartItemDTO updateCartItem(Integer customerId, String productId, Integer quantity) {
        // Similar to addToCart — violates SRP by doing multiple tasks.
        // The method performs validation, retrieves data, updates quantity, and maps DTO.

        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (product.getQuantity() < quantity) {
            throw new BadRequestException("Not enough stock available. Available: " + product.getQuantity());
        }

        CartItem.CartItemId cartItemId = new CartItem.CartItemId(customerId, productId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        // DTO conversion
        CartItemDTO dto = new CartItemDTO();
        dto.setProductID(product.getProductID());
        dto.setProductTitle(product.getTitle());
        dto.setProductPrice(product.getPrice());
        dto.setQuantity(cartItem.getQuantity());
        dto.setImageURL(product.getImageURL());

        return dto;
    }

    @Transactional
    public void removeFromCart(Integer customerId, String productId) {
        // Straightforward delete operation — fits CartService responsibility.
        // Minimal SRP concern here.

        CartItem.CartItemId cartItemId = new CartItem.CartItemId(customerId, productId);

        if (!cartItemRepository.existsById(cartItemId)) {
            throw new ResourceNotFoundException("Cart item not found");
        }

        cartItemRepository.deleteById(cartItemId);
    }

    @Transactional
    public void clearCart(Integer customerId) {
        // Method deletes all cart items for a customer — acceptable responsibility.

        Users customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Users", "id", customerId));

        cartItemRepository.deleteByCustomer(customer);
    }
}

