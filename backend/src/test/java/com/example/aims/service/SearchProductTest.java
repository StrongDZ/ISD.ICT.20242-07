package com.example.aims.service;

import com.example.aims.dto.products.BookDTO;
import com.example.aims.dto.products.CdDTO;
import com.example.aims.dto.products.DvdDTO;
import com.example.aims.service.products.ProductServiceImpl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SearchProductTest {

    @Autowired
    private ProductServiceImpl productService;

    @Test
    void testSearchProducts_ReturnsMatchingProducts() {
        // Given - Create and save test products to database
        BookDTO testBook = createTestBookDTO();
        testBook.setProductID("BK-TEST-001");
        testBook.setTitle("Test Book for Search");

        CdDTO testCD = createTestCdDTO();
        testCD.setProductID("CD-TEST-001");
        testCD.setTitle("Test Album for Search");

        try {
            productService.createProduct(testBook, 1);
            productService.createProduct(testCD, 1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // When - Search for products
        var result = productService.getFilteredProducts("Test", null, null, null, null, 0, 20);

        // Then - Verify results contain our test products
        assertNotNull(result);
        assertTrue(result.getContent().size() >= 2, "Should return at least 2 test products");

        // Check that our test products are in the results
        boolean foundBook = result.getContent().stream()
                .anyMatch(p -> p.getTitle().contains("Test Book"));
        boolean foundCD = result.getContent().stream()
                .anyMatch(p -> p.getTitle().contains("Test Album"));

        assertTrue(foundBook, "Test book should be found in search results");
        assertTrue(foundCD, "Test CD should be found in search results");
    }

    @Test
    void testSearchProducts_NoMatchingProducts_ReturnsEmptyList() {
        // Given - Search for non-existent keyword
        String keyword = "NONEXISTENTPRODUCTKEYWORD12345";

        // When
        var result = productService.getFilteredProducts(keyword, null, null, null, null, 0, 20);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty(), "Should return empty list for non-existent keyword");
    }

    @Test
    void testSearchProducts_WithPagination_ReturnsPagedResults() {
        // Given - Create multiple test products
        for (int i = 1; i <= 5; i++) {
            BookDTO testBook = createTestBookDTO();
            testBook.setProductID("BK-PAGE-" + String.format("%03d", i));
            testBook.setTitle("Pagination Test Book " + i);

            try {
                productService.createProduct(testBook, 1);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        // When - Search with pagination
        var result = productService.getFilteredProducts("Pagination", null, null, null, null, 0, 3);

        // Then - Verify pagination works
        assertNotNull(result);
        assertEquals(3, result.getSize(), "Page size should be 3");
        assertEquals(0, result.getPage(), "Should be first page");
        assertTrue(result.getTotalElements() >= 5, "Should have at least 5 total elements");
        assertTrue(result.getContent().size() <= 3, "Should return at most 3 items per page");
    }

    @Test
    void testSearchProducts_MixedProductTypes_ReturnsCorrectDTOs() {
        // Given - Create products of different types
        BookDTO testBook = createTestBookDTO();
        testBook.setProductID("BK-MIXED-001");
        testBook.setTitle("Programming Book Mixed Test");

        DvdDTO testDVD = createTestDvdDTO();
        testDVD.setProductID("DVD-MIXED-001");
        testDVD.setTitle("Programming Tutorial Mixed Test");

        try {
            productService.createProduct(testBook, 1);
            productService.createProduct(testDVD, 1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // When - Search for programming products
        var result = productService.getFilteredProducts("Programming", null, null, null, null, 0, 20);

        // Then - Verify we get mixed product types
        assertNotNull(result);
        assertTrue(result.getContent().size() >= 2, "Should return at least 2 products");

        // Check that we have both book and DVD types
        boolean hasBook = result.getContent().stream()
                .anyMatch(p -> p instanceof BookDTO && p.getTitle().contains("Programming"));
        boolean hasDVD = result.getContent().stream()
                .anyMatch(p -> p instanceof DvdDTO && p.getTitle().contains("Programming"));

        assertTrue(hasBook, "Should contain BookDTO");
        assertTrue(hasDVD, "Should contain DvdDTO");
    }

    @Test
    void testSearchProducts_CaseInsensitiveSearch() {
        // Given - Create test product
        BookDTO testBook = createTestBookDTO();
        testBook.setProductID("BK-CASE-001");
        testBook.setTitle("Case Insensitive Test Book");
        System.out.println("Creating test book: " + testBook.getTitle());
        try {
            productService.createProduct(testBook, 1);
            System.out.println("Successfully created test book");
        } catch (Exception e) {
            System.out.println("Error creating test book: " + e.getMessage());
            e.printStackTrace();
            fail("Error creating test book: " + e.getMessage());
        }

        // When - Search with different cases
        var result1 = productService.getFilteredProducts("CASE", null, null, null, null, 0, 20);
        var result2 = productService.getFilteredProducts("case", null, null, null, null, 0, 20);
        var result3 = productService.getFilteredProducts("Case", null, null, null, null, 0, 20);

        // Debug: Print results
        System.out.println("Result1 size: " + result1.getContent().size());
        result1.getContent().forEach(p -> System.out.println("Result1 product: " + p.getProductID() + " - " + p.getTitle()));
        
        System.out.println("Result2 size: " + result2.getContent().size());
        result2.getContent().forEach(p -> System.out.println("Result2 product: " + p.getProductID() + " - " + p.getTitle()));
        
        System.out.println("Result3 size: " + result3.getContent().size());
        result3.getContent().forEach(p -> System.out.println("Result3 product: " + p.getProductID() + " - " + p.getTitle()));

        // Then - Verify case insensitive search works
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);

        // All searches should return the same product
        boolean foundInResult1 = result1.getContent().stream()
                .anyMatch(p -> p.getTitle().equals("Case Insensitive Test Book"));
        boolean foundInResult2 = result2.getContent().stream()
                .anyMatch(p -> p.getTitle().equals("Case Insensitive Test Book"));
        boolean foundInResult3 = result3.getContent().stream()
                .anyMatch(p -> p.getTitle().equals("Case Insensitive Test Book"));

        assertTrue(foundInResult1, "Should find product with uppercase search");
        assertTrue(foundInResult2, "Should find product with lowercase search");
        assertTrue(foundInResult3, "Should find product with mixed case search");
    }

    @Test
    void testSearchProducts_WithCategoryFilter() {
        // Given - Create products of different categories
        BookDTO testBook = createTestBookDTO();
        testBook.setProductID("BK-CAT-001");
        testBook.setTitle("Category Test Book");

        CdDTO testCD = createTestCdDTO();
        testCD.setProductID("CD-CAT-001");
        testCD.setTitle("Category Test CD");

        try {
            productService.createProduct(testBook, 1);
            productService.createProduct(testCD, 1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // When - Search with category filter
        var bookResult = productService.getFilteredProducts("Category", "book", null, null, null, 0, 20);
        var cdResult = productService.getFilteredProducts("Category", "cd", null, null, null, 0, 20);

        // Then - Verify category filtering works
        assertNotNull(bookResult);
        assertNotNull(cdResult);

        // Book result should only contain books
        boolean bookResultHasOnlyBooks = bookResult.getContent().stream()
                .allMatch(p -> p instanceof BookDTO);
        assertTrue(bookResultHasOnlyBooks, "Book filter should only return books");

        // CD result should only contain CDs
        boolean cdResultHasOnlyCDs = cdResult.getContent().stream()
                .allMatch(p -> p instanceof CdDTO);
        assertTrue(cdResultHasOnlyCDs, "CD filter should only return CDs");
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