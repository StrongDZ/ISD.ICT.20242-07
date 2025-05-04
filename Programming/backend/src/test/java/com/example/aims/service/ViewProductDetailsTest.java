package com.example.aims.service;

import com.example.aims.dto.ProductDTO;
import com.example.aims.model.Product;
import com.example.aims.repository.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ViewProductDetailsTest {
    
    @Mock
    private ProductRepository productRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CDRepository cdRepository;

    @Mock
    private DVDRepository dvdRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void testGetProductById_ReturnsCorrectDTO() {
        // Given
        String productId = "abc123";
        Product mockProduct = new Product();
        mockProduct.setProductID(productId);
        mockProduct.setTitle("Test Product");
        mockProduct.setCategory("book");
        mockProduct.setPrice(100.0);
        mockProduct.setQuantity(5);

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));

        // When
        ProductDTO result = productService.getProductById(productId);

        // Then
        assertNotNull(result);
        assertEquals(productId, result.getProductID());
        assertEquals("Test Product", result.getTitle());
        assertEquals("book", result.getCategory());
        assertEquals(100.0, result.getPrice());
        assertEquals(5, result.getQuantity());

        verify(productRepository, times(1)).findById(productId);
    }
@Test
    void testGetProductById_ThrowsExceptionWhenNotFound() {
        // Given
        String productId = "nonexistent";
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                productService.getProductById(productId));

        assertEquals("Product not found with id: nonexistent", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
    }
}
