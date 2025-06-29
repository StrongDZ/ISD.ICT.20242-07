package com.example.aims.service;

import com.example.aims.dto.products.BookDTO;
import com.example.aims.dto.products.CdDTO;
import com.example.aims.dto.products.DvdDTO;
import com.example.aims.dto.products.ProductDTO;
import com.example.aims.exception.BadRequestException;
import com.example.aims.exception.ResourceNotFoundException;
import com.example.aims.factory.ProductFactory;
import com.example.aims.service.products.ProductServiceImpl;
import com.example.aims.strategy.ProductStrategy;
import com.example.aims.repository.ProductRepository;
import com.example.aims.model.Book;
import com.example.aims.model.CD;
import com.example.aims.model.DVD;
import com.example.aims.model.Product;
import com.example.aims.common.ProductType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddUpdateProductToStoreTest {

    @Mock
    private ProductFactory productFactory;

    @Mock
    private ProductStrategy bookStrategy;

    @Mock
    private ProductStrategy cdStrategy;

    @Mock
    private ProductStrategy dvdStrategy;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    public void setUp() {
        // Dùng lenient() để tránh lỗi unnecessary stubbing khi stub này không được dùng
        // ở mọi test
        lenient().when(productFactory.getStrategy("book")).thenReturn(bookStrategy);
        lenient().when(productFactory.getStrategy("cd")).thenReturn(cdStrategy);
        lenient().when(productFactory.getStrategy("dvd")).thenReturn(dvdStrategy);
    }

    private static Stream<Arguments> productTestData() {
        return Stream.of(
            Arguments.of("book", ProductType.book, createTestBookDTO(), createTestBook()),
            Arguments.of("cd", ProductType.cd, createTestCdDTO(), createTestCD()),
            Arguments.of("dvd", ProductType.dvd, createTestDvdDTO(), createTestDVD())
        );
    }

    @ParameterizedTest
    @MethodSource("productTestData")
    public void testCreateProductSuccess(String type, ProductType productType, ProductDTO inputDTO, Product expectedProduct) {
        // Arrange
        ProductDTO expectedResult = inputDTO;
        when(productFactory.getStrategy(type)).thenReturn(getStrategyForType(type));
        when(getStrategyForType(type).createProduct(any(ProductDTO.class))).thenReturn(expectedResult);

        // Act
        ProductDTO result = productService.createProduct(inputDTO, 1);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult.getTitle(), result.getTitle());
        assertEquals(expectedResult.getCategory(), result.getCategory());
        verify(productFactory, times(1)).getStrategy(type);
        verify(getStrategyForType(type), times(1)).createProduct(inputDTO);
    }

    @ParameterizedTest
    @MethodSource("productTestData")
    public void testUpdateProductSuccess(String type, ProductType productType, ProductDTO inputDTO, Product expectedProduct) {
        // Arrange
        String productId = expectedProduct.getProductID();
        ProductDTO expectedResult = inputDTO;
        when(productFactory.getStrategy(type)).thenReturn(getStrategyForType(type));
        when(getStrategyForType(type).updateProduct(eq(productId), any(ProductDTO.class))).thenReturn(expectedResult);

        // Act
        ProductDTO result = productService.updateProduct(productId, inputDTO);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult.getTitle(), result.getTitle());
        assertEquals(expectedResult.getCategory(), result.getCategory());
        verify(productFactory, times(1)).getStrategy(type);
        verify(getStrategyForType(type), times(1)).updateProduct(productId, inputDTO);
    }

    @ParameterizedTest
    @MethodSource("productTestData")
    public void testDeleteProductSuccess(String type, ProductType productType, ProductDTO inputDTO, Product expectedProduct) {
        // Arrange
        String productId = expectedProduct.getProductID();
        when(productRepository.findById(productId)).thenReturn(Optional.of(expectedProduct));
        doNothing().when(getStrategyForType(type)).deleteProduct(productId);

        // Act & Assert
        assertDoesNotThrow(() -> productService.deleteProduct(productId));
        
        verify(productFactory, times(1)).getStrategy(type);
        verify(getStrategyForType(type), times(1)).deleteProduct(productId);
    }

    // --- Test createProduct (Add Product) ---
    @Test
    public void testCreateProductInvalidCategory() {
        // Arrange
        BookDTO bookDTO = createTestBookDTO();
        bookDTO.setCategory("invalid_category");

        when(productFactory.getStrategy("invalid_category"))
                .thenThrow(new BadRequestException("Unsupported product type: invalid_category"));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> productService.createProduct(bookDTO, 1));

        assertTrue(exception.getMessage().contains("Unsupported product type"));
        verify(productFactory, times(1)).getStrategy("invalid_category");
    }

    @Test
    public void testCreateProductStrategyThrowsException() {
        // Arrange
        BookDTO bookDTO = createTestBookDTO();

        when(bookStrategy.createProduct(any(ProductDTO.class)))
                .thenThrow(new RuntimeException("Product with this ID already exists"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.createProduct(bookDTO, 1));

        assertEquals("Product with this ID already exists", exception.getMessage());
        verify(productFactory, times(1)).getStrategy("book");
        verify(bookStrategy, times(1)).createProduct(bookDTO);
    }

    // --- Test updateProduct ---
    @Test
    public void testUpdateProductNotFound() {
        // Arrange
        String productId = "NONEXISTENT-123";
        BookDTO bookDTO = createTestBookDTO();

        when(bookStrategy.updateProduct(eq(productId), any(ProductDTO.class)))
                .thenThrow(new ResourceNotFoundException("Product", "id", productId));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(productId, bookDTO));

        assertTrue(exception.getMessage().contains("not found"));
        assertTrue(exception.getMessage().contains(productId));
        verify(productFactory, times(1)).getStrategy("book");
        verify(bookStrategy, times(1)).updateProduct(productId, bookDTO);
    }

    @Test
    public void testUpdateProductInvalidCategory() {
        // Arrange
        String productId = "INVALID-123";
        BookDTO bookDTO = createTestBookDTO();
        bookDTO.setCategory("invalid_category");

        when(productFactory.getStrategy("invalid_category"))
                .thenThrow(new BadRequestException("Unsupported product type: invalid_category"));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> productService.updateProduct(productId, bookDTO));

        assertTrue(exception.getMessage().contains("Unsupported product type"));
        verify(productFactory, times(1)).getStrategy("invalid_category");
    }

    @Test
    public void testUpdateProductStrategyError() {
        // Arrange
        String productId = "BK-123456";
        BookDTO bookDTO = createTestBookDTO();

        when(bookStrategy.updateProduct(eq(productId), any(ProductDTO.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.updateProduct(productId, bookDTO));

        assertEquals("Database connection error", exception.getMessage());
        verify(productFactory, times(1)).getStrategy("book");
        verify(bookStrategy, times(1)).updateProduct(productId, bookDTO);
    }

    // --- Additional Create Product Tests ---
    @Test
    public void testCreateProductWithNullInput() {
        // Act & Assert
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> productService.createProduct(null, 1));

        verify(productFactory, never()).getStrategy(any());
    }

    @Test
    public void testCreateBookWithMissingRequiredFields() {
        // Arrange
        BookDTO bookDTO = new BookDTO();
        bookDTO.setCategory("book");
        // Missing title and other required fields

        when(bookStrategy.createProduct(any(ProductDTO.class)))
                .thenThrow(new BadRequestException("Title is required"));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> productService.createProduct(bookDTO, 1));

        assertTrue(exception.getMessage().contains("Title is required"));
        verify(productFactory, times(1)).getStrategy("book");
        verify(bookStrategy, times(1)).createProduct(bookDTO);
    }

    @Test
    public void testCreateProductWithNegativePrice() {
        // Arrange
        BookDTO bookDTO = createTestBookDTO();
        bookDTO.setPrice(-100.0);

        when(bookStrategy.createProduct(any(ProductDTO.class)))
                .thenThrow(new BadRequestException("Price cannot be negative"));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> productService.createProduct(bookDTO, 1));

        assertTrue(exception.getMessage().contains("Price cannot be negative"));
        verify(productFactory, times(1)).getStrategy("book");
        verify(bookStrategy, times(1)).createProduct(bookDTO);
    }

    @Test
    public void testCreateProductWithNegativeQuantity() {
        // Arrange
        BookDTO bookDTO = createTestBookDTO();
        bookDTO.setQuantity(-5);

        when(bookStrategy.createProduct(any(ProductDTO.class)))
                .thenThrow(new BadRequestException("Quantity cannot be negative"));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> productService.createProduct(bookDTO, 1));

        assertTrue(exception.getMessage().contains("Quantity cannot be negative"));
        verify(productFactory, times(1)).getStrategy("book");
        verify(bookStrategy, times(1)).createProduct(bookDTO);
    }

    @Test
    public void testCreateProductWithDuplicateId() {
        // Arrange
        BookDTO bookDTO = createTestBookDTO();
        bookDTO.setProductID("BK-123456"); // Trying to set ID manually

        when(bookStrategy.createProduct(any(ProductDTO.class)))
                .thenThrow(new BadRequestException("Product with ID BK-123456 already exists"));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> productService.createProduct(bookDTO, 1));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(productFactory, times(1)).getStrategy("book");
        verify(bookStrategy, times(1)).createProduct(bookDTO);
    }

    // --- Additional Update Product Tests ---
    @Test
    public void testUpdateProductWithNullInput() {
        // Arrange
        String productId = "BK-123456";

        // Act & Assert
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> productService.updateProduct(productId, null));

        verify(productFactory, never()).getStrategy(any());
    }

    @Test
    public void testUpdateBookWithMissingRequiredFields() {
        // Arrange
        String productId = "BK-123456";
        BookDTO bookDTO = new BookDTO();
        bookDTO.setCategory("book");
        // Missing title and other required fields

        when(bookStrategy.updateProduct(eq(productId), any(ProductDTO.class)))
                .thenThrow(new BadRequestException("Title is required"));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> productService.updateProduct(productId, bookDTO));

        assertTrue(exception.getMessage().contains("Title is required"));
        verify(productFactory, times(1)).getStrategy("book");
        verify(bookStrategy, times(1)).updateProduct(productId, bookDTO);
    }

    @Test
    public void testUpdateProductWithNegativePrice() {
        // Arrange
        String productId = "BK-123456";
        BookDTO bookDTO = createTestBookDTO();
        bookDTO.setPrice(-100.0);

        when(bookStrategy.updateProduct(eq(productId), any(ProductDTO.class)))
                .thenThrow(new BadRequestException("Price cannot be negative"));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> productService.updateProduct(productId, bookDTO));

        assertTrue(exception.getMessage().contains("Price cannot be negative"));
        verify(productFactory, times(1)).getStrategy("book");
        verify(bookStrategy, times(1)).updateProduct(productId, bookDTO);
    }

    @Test
    public void testUpdateProductWithNegativeQuantity() {
        // Arrange
        String productId = "BK-123456";
        BookDTO bookDTO = createTestBookDTO();
        bookDTO.setQuantity(-5);

        when(bookStrategy.updateProduct(eq(productId), any(ProductDTO.class)))
                .thenThrow(new BadRequestException("Quantity cannot be negative"));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> productService.updateProduct(productId, bookDTO));

        assertTrue(exception.getMessage().contains("Quantity cannot be negative"));
        verify(productFactory, times(1)).getStrategy("book");
        verify(bookStrategy, times(1)).updateProduct(productId, bookDTO);
    }

    @Test
    public void testUpdateProductWithDifferentCategory() {
        // Arrange
        String productId = "BK-123456";
        BookDTO bookDTO = createTestBookDTO();
        bookDTO.setCategory("cd"); // Try to change from book to cd

        when(productFactory.getStrategy("cd"))
                .thenReturn(cdStrategy);
        
        when(cdStrategy.updateProduct(eq(productId), any(ProductDTO.class)))
                .thenThrow(new BadRequestException("Cannot change product category from book to cd"));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> productService.updateProduct(productId, bookDTO));

        assertTrue(exception.getMessage().contains("Cannot change product category"));
        verify(productFactory, times(1)).getStrategy("cd");
        verify(cdStrategy, times(1)).updateProduct(eq(productId), any(ProductDTO.class));
    }

    @Test
    public void testUpdateProductWithDifferentId() {
        // Arrange
        String productId = "BK-123456";
        BookDTO bookDTO = createTestBookDTO();
        bookDTO.setProductID("BK-789012"); // Try to change product ID

        when(bookStrategy.updateProduct(eq(productId), any(ProductDTO.class)))
                .thenThrow(new BadRequestException("Cannot change product ID"));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> productService.updateProduct(productId, bookDTO));

        assertTrue(exception.getMessage().contains("Cannot change product ID"));
        verify(productFactory, times(1)).getStrategy("book");
        verify(bookStrategy, times(1)).updateProduct(productId, bookDTO);
    }

    // --- Delete Product Tests ---
    @Test
    public void testDeleteProductNotFound() {
        // Arrange
        String productId = "BK-NONEXIST";
        
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.deleteProduct(productId));

        assertTrue(exception.getMessage().contains("Product not found"));
        verify(productFactory, never()).getStrategy(any());
        verify(bookStrategy, never()).deleteProduct(any());
    }

    @Test
    public void testDeleteProductWithInvalidId() {
        // Arrange
        String invalidId = "INVALID-123";
        when(productRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.deleteProduct(invalidId));

        assertTrue(exception.getMessage().contains("Product not found"));
        verify(productFactory, never()).getStrategy(any());
    }

    @Test
    public void testDeleteProductWithNullId() {
        // Arrange
        when(productRepository.findById(null)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.deleteProduct(null));

        assertTrue(exception.getMessage().contains("Product not found"));
        verify(productFactory, never()).getStrategy(any());
    }

    @Test
    public void testDeleteProductInUse() {
        // Arrange
        String productId = "BK-123456";
        Book book = createTestBook();
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(book));
        doThrow(new BadRequestException("Product is currently in use and cannot be deleted"))
            .when(bookStrategy).deleteProduct(productId);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> productService.deleteProduct(productId));

        assertTrue(exception.getMessage().contains("cannot be deleted"));
        verify(productFactory, times(1)).getStrategy("book");
        verify(bookStrategy, times(1)).deleteProduct(productId);
    }

    // --- Helper Methods ---
    private static BookDTO createTestBookDTO() {
        BookDTO bookDTO = new BookDTO();

        bookDTO.setTitle("Java Programming");
        bookDTO.setCategory("book");
        bookDTO.setPrice(150000.0);
        bookDTO.setValue(120000.0);
        bookDTO.setQuantity(10);
        bookDTO.setDescription("A comprehensive Java programming book");

        bookDTO.setAuthors("Robert Martin");
        bookDTO.setPublisher("Prentice Hall");
        bookDTO.setNumberOfPages(500);
        bookDTO.setLanguage("English");
        bookDTO.setGenre("Programming");
        bookDTO.setCoverType("Hardcover");
        bookDTO.setPubDate(new Date());

        return bookDTO;
    }

    private static CdDTO createTestCdDTO() {
        CdDTO cdDTO = new CdDTO();
        cdDTO.setTitle("Greatest Hits");
        cdDTO.setCategory("cd");
        cdDTO.setPrice(100000.0);
        cdDTO.setValue(80000.0);
        cdDTO.setQuantity(5);
        cdDTO.setDescription("Best of Queen");

        cdDTO.setArtist("Queen");
        cdDTO.setRecordLabel("EMI");
        cdDTO.setMusicType("Rock");
        cdDTO.setTracklist("Bohemian Rhapsody, Don't Stop Me Now");
        cdDTO.setReleaseDate(new Date());

        return cdDTO;
    }

    private static DvdDTO createTestDvdDTO() {
        DvdDTO dvdDTO = new DvdDTO();
        dvdDTO.setTitle("Avengers");
        dvdDTO.setCategory("dvd");
        dvdDTO.setPrice(200000.0);
        dvdDTO.setValue(150000.0);
        dvdDTO.setQuantity(3);
        dvdDTO.setDescription("Marvel superhero movie");

        dvdDTO.setDirector("Russo Brothers");
        dvdDTO.setStudio("Marvel Studios");
        dvdDTO.setRuntime("120 minutes");
        dvdDTO.setDiscType("Blu-ray");
        dvdDTO.setSubtitle("English, Vietnamese");

        return dvdDTO;
    }

    private static Book createTestBook() {
        Book book = new Book();
        book.setProductID("BK-123456");
        book.setTitle("Java Programming");
        book.setCategory(ProductType.book);
        book.setPrice(150000.0);
        book.setValue(120000.0);
        book.setQuantity(10);
        book.setDescription("A comprehensive Java programming book");
        book.setAuthors("Robert Martin");
        book.setPublisher("Prentice Hall");
        book.setNumberOfPages(500);
        book.setLanguage("English");
        book.setGenre("Programming");
        book.setCoverType("Hardcover");
        book.setPubDate(new Date());
        return book;
    }

    private static CD createTestCD() {
        CD cd = new CD();
        cd.setProductID("CD-123456");
        cd.setTitle("Greatest Hits");
        cd.setCategory(ProductType.cd);
        cd.setPrice(100000.0);
        cd.setValue(80000.0);
        cd.setQuantity(5);
        cd.setDescription("Best of Queen");
        cd.setArtist("Queen");
        cd.setRecordLabel("EMI");
        cd.setMusicType("Rock");
        cd.setTracklist("Bohemian Rhapsody, Don't Stop Me Now");
        cd.setReleaseDate(new Date());
        return cd;
    }

    private static DVD createTestDVD() {
        DVD dvd = new DVD();
        dvd.setProductID("DVD-123456");
        dvd.setTitle("Avengers");
        dvd.setCategory(ProductType.dvd);
        dvd.setPrice(200000.0);
        dvd.setValue(150000.0);
        dvd.setQuantity(3);
        dvd.setDescription("Marvel superhero movie");
        dvd.setDirector("Russo Brothers");
        dvd.setStudio("Marvel Studios");
        dvd.setRuntime("120 minutes");
        dvd.setDiscType("Blu-ray");
        dvd.setSubtitle("English, Vietnamese");
        return dvd;
    }

    private ProductStrategy getStrategyForType(String type) {
        switch(type) {
            case "book": return bookStrategy;
            case "cd": return cdStrategy;
            case "dvd": return dvdStrategy;
            default: throw new IllegalArgumentException("Unknown type: " + type);
        }
    }
}