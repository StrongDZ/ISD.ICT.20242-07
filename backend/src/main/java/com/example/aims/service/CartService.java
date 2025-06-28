package com.example.aims.service;

import com.example.aims.dto.CartItemDTO;
import com.example.aims.exception.ResourceNotFoundException;
import com.example.aims.mapper.CartItemMapper;
import com.example.aims.model.CartItem;
import com.example.aims.model.Product;
import com.example.aims.model.Users;
import com.example.aims.repository.CartItemRepository;
import com.example.aims.repository.ProductRepository;
import com.example.aims.repository.UsersRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class handling shopping cart operations.
 * This class follows Single Responsibility Principle by focusing only on
 * cart business logic operations.
 * Data validation and DTO mapping are delegated to specialized components.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UsersRepository userRepository;
    private final CartItemMapper cartItemMapper;
    private final ProductRepository productRepository;

    /**
     * Retrieves all items in a user's cart.
     *
     * @param customerId the ID of the customer whose cart items to retrieve
     * @return list of CartItemDTO objects representing the cart items
     * @throws ResourceNotFoundException if the customer is not found
     */
    public List<CartItemDTO> getCartItems(Integer customerId) {
        log.debug("Fetching cart items for customer ID: {}", customerId);

        Users customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        List<CartItem> cartItems = cartItemRepository.findByCustomer(customer);

        return cartItems.stream()
                .map(cartItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Adds a product to the customer's cart or updates quantity if already exists.
     *
     * @param customerId the ID of the customer
     * @param productId  the ID of the product to add
     * @param quantity   the quantity to add
     * @return CartItemDTO representing the added/updated cart item
     * @throws com.example.aims.exception.BadRequestException if quantity is invalid
     *                                                        or insufficient stock
     * 
     * @throws ResourceNotFoundException                      if customer or product
     *                                                        not found
     */
    @Transactional
    public CartItemDTO addToCart(Integer customerId, String productId, Integer quantity) {
        log.debug("Adding product {} to cart for customer {}, quantity: {}", productId, customerId, quantity);

        Users customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.validateStock(quantity);

        CartItem cartItem = new CartItem();
        cartItem.setId(new CartItem.CartItemId(customerId, productId));
        cartItem.setCustomer(customer);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);

        cartItem = cartItemRepository.save(cartItem);

        return cartItemMapper.toDTO(cartItem);
    }

    /**
     * Updates the quantity of an item in the cart.
     *
     * @param customerId the ID of the customer
     * @param productId  the ID of the product to update
     * @param quantity   the new quantity
     * @return CartItemDTO representing the updated cart item
     * @throws com.example.aims.exception.BadRequestException if quantity is invalid
     *                                                        or insufficient stock
     * @throws ResourceNotFoundException                      if cart item not found
     */
    @Transactional
    public CartItemDTO updateCartItem(Integer customerId, String productId, Integer quantity) {
        log.debug("Updating cart item for customer {}, product {}, new quantity: {}", customerId, productId, quantity);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.validateStock(quantity);

        CartItem cartItem = cartItemRepository.findById(new CartItem.CartItemId(customerId, productId))
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cartItem.setQuantity(quantity);
        cartItem = cartItemRepository.save(cartItem);

        return cartItemMapper.toDTO(cartItem);
    }

    /**
     * Removes an item from the cart.
     *
     * @param customerId the ID of the customer
     * @param productId  the ID of the product to remove
     * @throws ResourceNotFoundException if cart item not found
     */
    @Transactional
    public void removeFromCart(Integer customerId, String productId) {
        log.debug("Removing product {} from cart for customer {}", productId, customerId);

        CartItem.CartItemId cartItemId = new CartItem.CartItemId(customerId, productId);
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new ResourceNotFoundException("Cart item not found");
        }
        cartItemRepository.deleteById(cartItemId);
    }

    /**
     * Clears all items from a customer's cart.
     *
     * @param customerId the ID of the customer whose cart to clear
     * @throws ResourceNotFoundException if the customer is not found
     */
    @Transactional
    public void clearCart(Integer customerId) {
        log.debug("Clearing cart for customer {}", customerId);

        Users customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        cartItemRepository.deleteByCustomer(customer);
    }

}
