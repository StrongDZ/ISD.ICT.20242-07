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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

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

    // --- Test createProduct (Add Product) ---
    @Test
    public void testCreateBookProductSuccess() {
        // Arrange
        BookDTO inputBookDTO = createTestBookDTO();
        BookDTO expectedResult = createTestBookDTO();
        expectedResult.setProductID("BK-123456");

        when(bookStrategy.createProduct(any(ProductDTO.class))).thenReturn(expectedResult);

        // Act
        ProductDTO result = productService.createProduct(inputBookDTO, 1);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof BookDTO);
        BookDTO bookResult = (BookDTO) result;
        assertEquals("BK-123456", bookResult.getProductID());
        assertEquals("Java Programming", bookResult.getTitle());
        assertEquals("book", bookResult.getCategory());
        assertEquals("Robert Martin", bookResult.getAuthors());

        verify(productFactory, times(1)).getStrategy("book");
        verify(bookStrategy, times(1)).createProduct(inputBookDTO);
    }

    @Test
    public void testCreateCDProductSuccess() {
        // Arrange
        CdDTO inputCdDTO = createTestCdDTO();
        CdDTO expectedResult = createTestCdDTO();
        expectedResult.setProductID("CD-123456");

        when(cdStrategy.createProduct(any(ProductDTO.class))).thenReturn(expectedResult);

        // Act
        ProductDTO result = productService.createProduct(inputCdDTO, 1);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof CdDTO);
        CdDTO cdResult = (CdDTO) result;
        assertEquals("CD-123456", cdResult.getProductID());
        assertEquals("Greatest Hits", cdResult.getTitle());
        assertEquals("cd", cdResult.getCategory());
        assertEquals("Queen", cdResult.getArtist());

        verify(productFactory, times(1)).getStrategy("cd");
        verify(cdStrategy, times(1)).createProduct(inputCdDTO);
    }

    @Test
    public void testCreateDVDProductSuccess() {
        // Arrange
        DvdDTO inputDvdDTO = createTestDvdDTO();
        DvdDTO expectedResult = createTestDvdDTO();
        expectedResult.setProductID("DVD-123456");

        when(dvdStrategy.createProduct(any(ProductDTO.class))).thenReturn(expectedResult);

        // Act
        ProductDTO result = productService.createProduct(inputDvdDTO, 1);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof DvdDTO);
        DvdDTO dvdResult = (DvdDTO) result;
        assertEquals("DVD-123456", dvdResult.getProductID());
        assertEquals("Avengers", dvdResult.getTitle());
        assertEquals("dvd", dvdResult.getCategory());
        assertEquals("Russo Brothers", dvdResult.getDirector());

        verify(productFactory, times(1)).getStrategy("dvd");
        verify(dvdStrategy, times(1)).createProduct(inputDvdDTO);
    }

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
    public void testUpdateBookProductSuccess() {
        // Arrange
        String productId = "BK-123456";
        BookDTO inputBookDTO = createTestBookDTO();
        inputBookDTO.setTitle("Updated Java Programming");
        inputBookDTO.setPrice(200000.0);

        BookDTO expectedResult = createTestBookDTO();
        expectedResult.setProductID(productId);
        expectedResult.setTitle("Updated Java Programming");
        expectedResult.setPrice(200000.0);

        when(bookStrategy.updateProduct(eq(productId), any(ProductDTO.class))).thenReturn(expectedResult);

        // Act
        ProductDTO result = productService.updateProduct(productId, inputBookDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof BookDTO);
        BookDTO bookResult = (BookDTO) result;
        assertEquals(productId, bookResult.getProductID());
        assertEquals("Updated Java Programming", bookResult.getTitle());
        assertEquals(200000.0, bookResult.getPrice());

        verify(productFactory, times(1)).getStrategy("book");
        verify(bookStrategy, times(1)).updateProduct(productId, inputBookDTO);
    }

    @Test
    public void testUpdateCDProductSuccess() {
        // Arrange
        String productId = "CD-123456";
        CdDTO inputCdDTO = createTestCdDTO();
        inputCdDTO.setTitle("Updated Greatest Hits");
        inputCdDTO.setQuantity(15);

        CdDTO expectedResult = createTestCdDTO();
        expectedResult.setProductID(productId);
        expectedResult.setTitle("Updated Greatest Hits");
        expectedResult.setQuantity(15);

        when(cdStrategy.updateProduct(eq(productId), any(ProductDTO.class))).thenReturn(expectedResult);

        // Act
        ProductDTO result = productService.updateProduct(productId, inputCdDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof CdDTO);
        CdDTO cdResult = (CdDTO) result;
        assertEquals(productId, cdResult.getProductID());
        assertEquals("Updated Greatest Hits", cdResult.getTitle());
        assertEquals(15, cdResult.getQuantity());

        verify(productFactory, times(1)).getStrategy("cd");
        verify(cdStrategy, times(1)).updateProduct(productId, inputCdDTO);
    }

    @Test
    public void testUpdateDVDProductSuccess() {
        // Arrange
        String productId = "DVD-123456";
        DvdDTO inputDvdDTO = createTestDvdDTO();
        inputDvdDTO.setTitle("Updated Avengers");
        inputDvdDTO.setValue(180000.0);

        DvdDTO expectedResult = createTestDvdDTO();
        expectedResult.setProductID(productId);
        expectedResult.setTitle("Updated Avengers");
        expectedResult.setValue(180000.0);

        when(dvdStrategy.updateProduct(eq(productId), any(ProductDTO.class))).thenReturn(expectedResult);

        // Act
        ProductDTO result = productService.updateProduct(productId, inputDvdDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof DvdDTO);
        DvdDTO dvdResult = (DvdDTO) result;
        assertEquals(productId, dvdResult.getProductID());
        assertEquals("Updated Avengers", dvdResult.getTitle());
        assertEquals(180000.0, dvdResult.getValue());

        verify(productFactory, times(1)).getStrategy("dvd");
        verify(dvdStrategy, times(1)).updateProduct(productId, inputDvdDTO);
    }

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

    // --- Helper Methods ---
    private BookDTO createTestBookDTO() {
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

    private CdDTO createTestCdDTO() {
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

    private DvdDTO createTestDvdDTO() {
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
}