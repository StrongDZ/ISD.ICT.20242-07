package com.example.aims.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import com.example.aims.dto.CartItemDTO;
import com.example.aims.dto.products.BookDTO;
import com.example.aims.dto.products.ProductDTO;
import com.example.aims.exception.BadRequestException;
import com.example.aims.exception.ResourceNotFoundException;
import com.example.aims.model.Users;
import com.example.aims.repository.UsersRepository;
import com.example.aims.service.products.ProductServiceImpl;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ManageCartTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private UsersRepository usersRepository;

    private Users testCustomer;
    private BookDTO testProduct;

    @BeforeEach
    public void setUp() {
        // Create test customer
        testCustomer = createTestCustomer();
        testCustomer = usersRepository.save(testCustomer);

        // Create test product
        testProduct = createTestProduct();
        testProduct = (BookDTO) productService.createProduct(testProduct, 1);
    }

    @Test
    public void test_add_new_product_to_cart_successfully() {
        // Arrange
        Integer customerId = testCustomer.getId();
        String productId = testProduct.getProductID();
        Integer quantity = 2;

        // Act
        CartItemDTO result = cartService.addToCart(customerId, productId, quantity);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getProductDTO().getProductID());
        assertEquals("Test Book for Cart", result.getProductDTO().getTitle());
        assertEquals(150000.0, result.getProductDTO().getPrice());
        assertEquals(2, result.getQuantity());
    }

    @Test
    public void test_add_product_with_invalid_quantity_throws_exception() {
        // Arrange
        Integer customerId = testCustomer.getId();
        String productId = testProduct.getProductID();
        Integer quantity = 0;

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            cartService.addToCart(customerId, productId, quantity);
        });

        assertEquals("Quantity must be greater than zero", exception.getMessage());
    }

    @Test
    public void test_add_product_with_negative_quantity_throws_exception() {
        // Arrange
        Integer customerId = testCustomer.getId();
        String productId = testProduct.getProductID();
        Integer quantity = -1;

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            cartService.addToCart(customerId, productId, quantity);
        });

        assertEquals("Quantity must be greater than zero", exception.getMessage());
    }

    @Test
    public void test_update_cart_item_successfully() {
        // Arrange
        Integer customerId = testCustomer.getId();
        String productId = testProduct.getProductID();
        int newQuantity = 3;

        // First add item to cart
        cartService.addToCart(customerId, productId, 1);

        // Act
        CartItemDTO result = cartService.updateCartItem(customerId, productId, newQuantity);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getProductDTO().getProductID());
        assertEquals(newQuantity, result.getQuantity());
        assertEquals("Test Book for Cart", result.getProductDTO().getTitle());
        assertEquals(150000.0, result.getProductDTO().getPrice());
    }

    @Test
    public void test_remove_cart_item_successfully() {
        // Arrange
        Integer customerId = testCustomer.getId();
        String productId = testProduct.getProductID();

        // First add item to cart
        cartService.addToCart(customerId, productId, 1);

        // Act
        cartService.removeFromCart(customerId, productId);

        // Assert - Verify item is removed by trying to get cart items
        List<CartItemDTO> cartItems = cartService.getCartItems(customerId);
        assertTrue(
                cartItems.isEmpty()
                        || cartItems.stream().noneMatch(item -> item.getProductDTO().getProductID().equals(productId)),
                "Cart item should be removed");
    }

    @Test
    public void test_remove_nonexistent_cart_item_throws_exception() {
        // Arrange
        Integer customerId = testCustomer.getId();
        String productId = "NONEXISTENT-PRODUCT-ID";

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            cartService.removeFromCart(customerId, productId);
        });

        assertEquals("Cart item not found", ex.getMessage());
    }

    @Test
    public void test_clear_cart_successfully() {
        // Arrange
        Integer customerId = testCustomer.getId();
        String productId = testProduct.getProductID();

        // Add multiple items to cart
        cartService.addToCart(customerId, productId, 1);

        // Act
        cartService.clearCart(customerId);

        // Assert
        List<CartItemDTO> cartItems = cartService.getCartItems(customerId);
        assertTrue(cartItems.isEmpty(), "Cart should be empty after clearing");
    }

    @Test
    public void test_get_cart_items_returns_correct_items() {
        // Arrange
        Integer customerId = testCustomer.getId();
        String productId = testProduct.getProductID();

        // Add item to cart
        cartService.addToCart(customerId, productId, 2);

        // Act
        List<CartItemDTO> cartItems = cartService.getCartItems(customerId);

        // Assert
        assertNotNull(cartItems);
        assertFalse(cartItems.isEmpty());

        CartItemDTO cartItem = cartItems.stream()
                .filter(item -> item.getProductDTO().getProductID().equals(productId))
                .findFirst()
                .orElse(null);

        assertNotNull(cartItem);
        assertEquals(productId, cartItem.getProductDTO().getProductID());
        assertEquals(2, cartItem.getQuantity());
        assertEquals("Test Book for Cart", cartItem.getProductDTO().getTitle());
    }

    private Users createTestCustomer() {
        Users user = new Users();
        user.setUsername("testcustomer");
        user.setPassword("testpass123");
        user.setGmail("test@example.com");
        user.setType(com.example.aims.common.UserType.CUSTOMER);
        user.setUserStatus(com.example.aims.common.UserStatus.NONE);
        return user;
    }

    private BookDTO createTestProduct() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("Test Book for Cart");
        bookDTO.setCategory("book");
        bookDTO.setPrice(150000.0);
        bookDTO.setValue(120000.0);
        bookDTO.setQuantity(10);
        bookDTO.setDescription("A test book for cart operations");
        bookDTO.setAuthors("Test Author");
        bookDTO.setPublisher("Test Publisher");
        bookDTO.setNumberOfPages(200);
        bookDTO.setLanguage("English");
        bookDTO.setGenre("Test");
        bookDTO.setCoverType("Paperback");
        bookDTO.setPubDate(new Date());
        bookDTO.setWeight(0.5);
        bookDTO.setDimensions("15x20x2 cm");
        bookDTO.setBarcode("1234567890123");
        bookDTO.setWarehouseEntryDate(new Date());
        bookDTO.setImageURL("https://via.placeholder.com/300x400/0066cc/ffffff?text=Test+Book");
        bookDTO.setEligible(true);
        return bookDTO;
    }
}
