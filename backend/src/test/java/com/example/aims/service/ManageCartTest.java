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
import com.example.aims.dto.products.BookDTO;
import com.example.aims.mapper.CartItemMapper;
import com.example.aims.model.Book;
import com.example.aims.model.CartItem;
import com.example.aims.model.Product;
import com.example.aims.model.Users;
import com.example.aims.repository.CartItemRepository;
import com.example.aims.repository.ProductRepository;
import com.example.aims.repository.UsersRepository;
import com.example.aims.exception.BadRequestException;
import com.example.aims.exception.ResourceNotFoundException;

import java.util.Date;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ManageCartTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartItemMapper cartItemMapper;

    @InjectMocks
    private CartService cartService;

    @Test
    public void test_add_new_product_to_cart_successfully() {
        // Arrange
        Integer customerId = 1;
        String productId = "product456";
        Integer quantity = 2;

        Users mockCustomer = createMockUser(customerId);
        Product mockProduct = createMockProduct(productId);
        CartItemDTO expectedDTO = createMockCartItemDTO(mockProduct, quantity);

        when(usersRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartItemMapper.toDTO(any(CartItem.class))).thenReturn(expectedDTO);

        // Act
        CartItemDTO result = cartService.addToCart(customerId, productId, quantity);

        // Assert
        verify(cartItemRepository).save(any(CartItem.class));
        verify(cartItemMapper).toDTO(any(CartItem.class));
        assertEquals(productId, result.getProduct().getProductID());
        assertEquals("Test Product", result.getProduct().getTitle());
        assertEquals(19.99, result.getProduct().getPrice());
        assertEquals(2, result.getQuantity());
    }

    @Test
    public void test_add_product_with_invalid_quantity_throws_exception() {
        // Arrange
        Integer customerId = 1;
        String productId = "BK-20250609232953-d7d4a7de";
        Integer quantity = 0;

        Users mockCustomer = createMockUser(customerId);
        Product mockProduct = createMockProduct(productId);

        // Mock để pass qua customer validation, để test có thể reach quantity
        // validation
        when(usersRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            cartService.addToCart(customerId, productId, quantity);
        });

        assertEquals("Quantity must be greater than zero", exception.getMessage());

        // Verify that repository methods were called for validation
        verify(usersRepository).findById(customerId);
        verify(productRepository).findById(productId);
        // CartItem should not be saved due to validation failure
        verifyNoInteractions(cartItemRepository);
        verifyNoInteractions(cartItemMapper);
    }

    @Test
    public void test_update_cart_item_successfully() {
        // Arrange
        Integer customerId = 1;
        String productId = "product456";
        int newQuantity = 3;

        Product mockProduct = createMockProduct(productId);
        mockProduct.setQuantity(10); // Có hàng
        CartItemDTO expectedDTO = createMockCartItemDTO(mockProduct, newQuantity);

        CartItem.CartItemId cartItemId = new CartItem.CartItemId(customerId, productId);
        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setQuantity(1); // Giả sử ban đầu là 1

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartItemMapper.toDTO(any(CartItem.class))).thenReturn(expectedDTO);

        // Act
        CartItemDTO result = cartService.updateCartItem(customerId, productId, newQuantity);

        // Assert
        verify(cartItemRepository).save(cartItem);
        verify(cartItemMapper).toDTO(any(CartItem.class));
        assertEquals(productId, result.getProduct().getProductID());
        assertEquals(newQuantity, result.getQuantity());
        assertEquals("Test Product", result.getProduct().getTitle());
        assertEquals(19.99, result.getProduct().getPrice());
        assertEquals("http://example.com/image.jpg", result.getProduct().getImageURL());
    }

    @Test
    public void test_update_cart_item_insufficient_stock_throws_exception() {
        // Arrange
        Integer customerId = 1;
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
        Integer customerId = 1;
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
        Integer customerId = 1;
        String productId = "product456";
        CartItem.CartItemId cartItemId = new CartItem.CartItemId(customerId, productId);

        when(cartItemRepository.existsById(cartItemId)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            cartService.removeFromCart(customerId, productId);
        });

        assertEquals("Cart item not found", ex.getMessage());
    }

    private Users createMockUser(Integer customerId) {
        Users user = new Users();
        user.setId(customerId);
        user.setUsername("hoangmanh");
        user.setPassword("manhlun123");
        return user;
    }

    private Product createMockProduct(String productId) {
        Book book = new Book();
        book.setProductID(productId);
        book.setTitle("Test Product");
        book.setPrice(19.99);
        book.setQuantity(10);
        book.setImageURL("http://example.com/image.jpg");
        book.setCategory("book");
        book.setRushEligible(true);
        book.setWarehouseEntryDate(new Date());
        book.setDimensions("10x10x10");
        book.setWeight(1.0);
        book.setAuthors("John Doe");
        book.setPublisher("Test Publisher");
        return book;
    }

    private CartItemDTO createMockCartItemDTO(Product product, Integer quantity) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setProductID(product.getProductID());
        bookDTO.setTitle(product.getTitle());
        bookDTO.setPrice(product.getPrice());
        bookDTO.setImageURL(product.getImageURL());
        bookDTO.setCategory(product.getCategory());

        CartItemDTO dto = new CartItemDTO();
        dto.setProduct(bookDTO);
        dto.setQuantity(quantity);
        return dto;
    }
}
