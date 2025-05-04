
package com.example.aims.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


import com.example.aims.dto.CartItemDTO;
import com.example.aims.model.CartItem;
import com.example.aims.model.Product;
import com.example.aims.model.Users;
import com.example.aims.repository.CartItemRepository;
import com.example.aims.repository.ProductRepository;
import com.example.aims.repository.UsersRepository;
import com.example.aims.exception.BadRequestException;
import com.example.aims.exception.ResourceNotFoundException;

import java.util.Optional;
@ExtendWith(MockitoExtension.class)
public class ManageCartTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private  CartService cartService;



    @Test
    public void test_add_new_product_to_cart_successfully() {
        // Arrange
        String customerId = "customer123";
        String productId = "product456";
        Integer quantity = 2;

        Users mockCustomer = createMockUser(customerId);
        Product mockProduct = createMockProduct(productId);

        when(usersRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(cartItemRepository.findById(any(CartItem.CartItemId.class))).thenReturn(Optional.empty());

        // Act
        CartItemDTO result = cartService.addToCart(customerId, productId, quantity);

        // Assert
        verify(cartItemRepository).save(any(CartItem.class));
        assertEquals(productId, result.getProductID());
        assertEquals("Test Product", result.getProductTitle());
        assertEquals(19.99, result.getProductPrice());
        assertEquals(2, result.getQuantity());
        assertEquals("http://example.com/image.jpg", result.getImageURL());
    }

    @Test
    public void test_add_product_with_invalid_quantity_throws_exception() {
        // Arrange
        String customerId = "customer123";
        String productId = "product456";
        Integer quantity = 0;

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            cartService.addToCart(customerId, productId, quantity);
        });

        assertEquals("Quantity must be greater than zero", exception.getMessage());

        // Verify that no repository methods were called
        verifyNoInteractions(usersRepository);
        verifyNoInteractions(productRepository);
        verifyNoInteractions(cartItemRepository);
    }

        @Test
    public void test_update_cart_item_successfully() {
        // Arrange
        String customerId = "customer123";
        String productId = "product456";
        int newQuantity = 3;

        Product mockProduct = createMockProduct(productId);
        mockProduct.setQuantity(10); // Có hàng

        CartItem.CartItemId cartItemId = new CartItem.CartItemId(customerId, productId);
        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setQuantity(1); // Giả sử ban đầu là 1

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        // Act
        CartItemDTO result = cartService.updateCartItem(customerId, productId, newQuantity);

        // Assert
        verify(cartItemRepository).save(cartItem);
        assertEquals(productId, result.getProductID());
        assertEquals(newQuantity, result.getQuantity());
        assertEquals("Test Product", result.getProductTitle());
        assertEquals(19.99, result.getProductPrice());
        assertEquals("http://example.com/image.jpg", result.getImageURL());
    }

    @Test
    public void test_update_cart_item_insufficient_stock_throws_exception() {
        // Arrange
        String customerId = "customer123";
        String productId = "product456";
        int newQuantity = 15;

        Product mockProduct = createMockProduct(productId);
        mockProduct.setQuantity(10); // Không đủ hàng

        CartItem.CartItemId cartItemId = new CartItem.CartItemId(customerId, productId);
        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));

        // Act & Assert
        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            cartService.updateCartItem(customerId, productId, newQuantity);
        });

        assertEquals("Not enough stock available. Available: 10", ex.getMessage());
    }

    @Test
    public void test_remove_cart_item_successfully() {
        // Arrange
        String customerId = "customer123";
        String productId = "product456";
        CartItem.CartItemId cartItemId = new CartItem.CartItemId(customerId, productId);

        when(cartItemRepository.existsById(cartItemId)).thenReturn(true);

        // Act
        cartService.removeFromCart(customerId, productId);

        // Assert
        verify(cartItemRepository).deleteById(cartItemId);
    }

    @Test
    public void test_remove_nonexistent_cart_item_throws_exception() {
        // Arrange
        String customerId = "customer123";
        String productId = "product456";
        CartItem.CartItemId cartItemId = new CartItem.CartItemId(customerId, productId);

        when(cartItemRepository.existsById(cartItemId)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            cartService.removeFromCart(customerId, productId);
        });

        assertEquals("Cart item not found", ex.getMessage());
    }



    private Users createMockUser(String customerId) {
        Users user = new Users("1", "customer", "hoangmanh", "manhlun123");
        user.setId(customerId);
        return user;
    }

    private Product createMockProduct(String productId) {
        Product product = new Product();
        product.setProductID(productId);
        product.setTitle("Test Product");
        product.setPrice(19.99);
        product.setQuantity(10);
        product.setImageURL("http://example.com/image.jpg");
        return product;
    }
}
