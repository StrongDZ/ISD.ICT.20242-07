package com.example.aims.service;

import com.example.aims.dto.products.BookDTO;
import com.example.aims.dto.products.CdDTO;
import com.example.aims.dto.products.DvdDTO;
import com.example.aims.dto.products.ProductDTO;
import com.example.aims.service.products.ProductServiceImpl;
import com.example.aims.exception.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ViewProductDetailsTest {

    @Autowired
    private ProductServiceImpl productService;

    @Test
    void testGetProductById_BookFound_ReturnsCorrectDTO() {
        // Given - Create and save a test book
        BookDTO testBook = createTestBookDTO();
        testBook.setTitle("Java Programming Details Test");

        BookDTO created = (BookDTO) productService.createProduct(testBook, 1);
        String id = created.getProductID();

        // When
        ProductDTO result = productService.getProductById(id);

        // Then
        assertNotNull(result);
        assertTrue(result instanceof BookDTO);
        BookDTO bookResult = (BookDTO) result;
        assertEquals(id, bookResult.getProductID());
        assertEquals("Java Programming Details Test", bookResult.getTitle());
        assertEquals("book", bookResult.getCategory());
        assertEquals("Robert Martin", bookResult.getAuthors());
        assertEquals("Prentice Hall", bookResult.getPublisher());
        assertEquals(500, bookResult.getNumberOfPages());
        assertEquals("English", bookResult.getLanguage());
        assertEquals("Programming", bookResult.getGenre());
        assertEquals("Hardcover", bookResult.getCoverType());
        assertEquals(150000.0, bookResult.getPrice());
        assertEquals(120000.0, bookResult.getValue());
        assertEquals(10, bookResult.getQuantity());
    }

    @Test
    void testGetProductById_CDFound_ReturnsCorrectDTO() {
        // Given - Create and save a test CD
        CdDTO testCD = createTestCdDTO();
        testCD.setTitle("Greatest Hits Details Test");

        CdDTO created = (CdDTO) productService.createProduct(testCD, 1);
        String id = created.getProductID();

        // When
        ProductDTO result = productService.getProductById(id);

        // Then
        assertNotNull(result);
        assertTrue(result instanceof CdDTO);
        CdDTO cdResult = (CdDTO) result;
        assertEquals(id, cdResult.getProductID());
        assertEquals("Greatest Hits Details Test", cdResult.getTitle());
        assertEquals("cd", cdResult.getCategory());
        assertEquals("Queen", cdResult.getArtist());
        assertEquals("EMI", cdResult.getRecordLabel());
        assertEquals("Rock", cdResult.getMusicType());
        assertEquals("Bohemian Rhapsody, Don't Stop Me Now", cdResult.getTracklist());
        assertEquals(100000.0, cdResult.getPrice());
        assertEquals(80000.0, cdResult.getValue());
        assertEquals(15, cdResult.getQuantity());
    }

    @Test
    void testGetProductById_DVDFound_ReturnsCorrectDTO() {
        // Given - Create and save a test DVD
        DvdDTO testDVD = createTestDvdDTO();
        testDVD.setTitle("Avengers Details Test");

        DvdDTO created = (DvdDTO) productService.createProduct(testDVD, 1);
        String id = created.getProductID();

        // When
        ProductDTO result = productService.getProductById(id);

        // Then
        assertNotNull(result);
        assertTrue(result instanceof DvdDTO);
        DvdDTO dvdResult = (DvdDTO) result;
        assertEquals(id, dvdResult.getProductID());
        assertEquals("Avengers Details Test", dvdResult.getTitle());
        assertEquals("dvd", dvdResult.getCategory());
        assertEquals("Russo Brothers", dvdResult.getDirector());
        assertEquals("Marvel Studios", dvdResult.getStudio());
        assertEquals("120 minutes", dvdResult.getRuntime());
        assertEquals("Blu-ray", dvdResult.getDiscType());
        assertEquals("English, Vietnamese", dvdResult.getSubtitle());
        assertEquals("English", dvdResult.getLanguage());
        assertEquals("Action", dvdResult.getGenre());
        assertEquals(200000.0, dvdResult.getPrice());
        assertEquals(150000.0, dvdResult.getValue());
        assertEquals(8, dvdResult.getQuantity());
    }

    @Test
    void testGetProductById_ProductNotFound_ThrowsException() {
        // Given - Use a non-existent product ID
        String productId = "NONEXISTENT-DETAILS-123";

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductById(productId));

        assertEquals("Product not found with id: " + productId, exception.getMessage());
    }

    @Test
    void testGetAllProducts_ReturnsAllProductTypes() {
        // Given - Create products of different types
        BookDTO testBook = createTestBookDTO();
        testBook.setTitle("Book for All Products Test");
        BookDTO createdBook = (BookDTO) productService.createProduct(testBook, 1);

        CdDTO testCD = createTestCdDTO();
        testCD.setTitle("CD for All Products Test");
        CdDTO createdCD = (CdDTO) productService.createProduct(testCD, 1);

        DvdDTO testDVD = createTestDvdDTO();
        testDVD.setTitle("DVD for All Products Test");
        DvdDTO createdDVD = (DvdDTO) productService.createProduct(testDVD, 1);

        // When
        List<ProductDTO> result = productService.getAllProducts();

        // Then
        assertNotNull(result);
        assertTrue(result.size() >= 3, "Should return at least 3 products (our test products plus any existing ones)");

        // Verify we can find our test products
        boolean foundBook = result.stream()
                .anyMatch(p -> p.getProductID().equals(createdBook.getProductID()) && p instanceof BookDTO);
        boolean foundCD = result.stream()
                .anyMatch(p -> p.getProductID().equals(createdCD.getProductID()) && p instanceof CdDTO);
        boolean foundDVD = result.stream()
                .anyMatch(p -> p.getProductID().equals(createdDVD.getProductID()) && p instanceof DvdDTO);

        assertTrue(foundBook, "Should find our test book");
        assertTrue(foundCD, "Should find our test CD");
        assertTrue(foundDVD, "Should find our test DVD");
    }

    @Test
    void testGetProductById_WithNullId_ThrowsException() {
        // When & Then
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> productService.getProductById(null));
    }

    @Test
    void testGetProductById_WithEmptyId_ThrowsException() {
        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductById(""));
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
        bookDTO.setWeight(0.8);
        bookDTO.setDimensions("18.5 x 23.4 x 2.8 cm");
        bookDTO.setBarcode("9780132350884");
        bookDTO.setWarehouseEntryDate(new Date());
        bookDTO.setImageURL("https://via.placeholder.com/300x400/0066cc/ffffff?text=Test+Book");
        bookDTO.setEligible(true);
        return bookDTO;
    }

    private CdDTO createTestCdDTO() {
        CdDTO cdDTO = new CdDTO();
        cdDTO.setTitle("Greatest Hits");
        cdDTO.setCategory("cd");
        cdDTO.setPrice(100000.0);
        cdDTO.setValue(80000.0);
        cdDTO.setQuantity(15);
        cdDTO.setDescription("Best of Queen");
        cdDTO.setArtist("Queen");
        cdDTO.setRecordLabel("EMI");
        cdDTO.setMusicType("Rock");
        cdDTO.setTracklist("Bohemian Rhapsody, Don't Stop Me Now");
        cdDTO.setReleaseDate(new Date());
        cdDTO.setWeight(0.1);
        cdDTO.setDimensions("12.5 x 12.5 x 0.1 cm");
        cdDTO.setBarcode("1234567890123");
        cdDTO.setWarehouseEntryDate(new Date());
        cdDTO.setImageURL("https://via.placeholder.com/300x300/cc6600/ffffff?text=Test+CD");
        cdDTO.setEligible(true);
        return cdDTO;
    }

    private DvdDTO createTestDvdDTO() {
        DvdDTO dvdDTO = new DvdDTO();
        dvdDTO.setTitle("Avengers");
        dvdDTO.setCategory("dvd");
        dvdDTO.setPrice(200000.0);
        dvdDTO.setValue(150000.0);
        dvdDTO.setQuantity(8);
        dvdDTO.setDescription("Marvel superhero movie");
        dvdDTO.setDirector("Russo Brothers");
        dvdDTO.setStudio("Marvel Studios");
        dvdDTO.setRuntime("120 minutes");
        dvdDTO.setLanguage("English");
        dvdDTO.setGenre("Action");
        dvdDTO.setDiscType("Blu-ray");
        dvdDTO.setSubtitle("English, Vietnamese");
        dvdDTO.setReleaseDate(new Date());
        dvdDTO.setWeight(0.2);
        dvdDTO.setDimensions("13.5 x 19.0 x 1.5 cm");
        dvdDTO.setBarcode("9876543210987");
        dvdDTO.setWarehouseEntryDate(new Date());
        dvdDTO.setImageURL("https://via.placeholder.com/300x400/FF6347/ffffff?text=Test+DVD");
        dvdDTO.setEligible(true);
        return dvdDTO;
    }
}
