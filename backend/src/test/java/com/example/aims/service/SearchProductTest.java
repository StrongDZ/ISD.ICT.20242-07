package com.example.aims.service;

import com.example.aims.dto.products.BookDTO;
import com.example.aims.dto.products.CdDTO;
import com.example.aims.dto.products.DvdDTO;
import com.example.aims.dto.products.ProductDTO;
import com.example.aims.factory.ProductFactory;
import com.example.aims.service.products.ProductServiceImpl;
import com.example.aims.strategy.impl.BookStrategy;
import com.example.aims.strategy.impl.CdStrategy;
import com.example.aims.strategy.impl.DvdStrategy;
import com.example.aims.repository.ProductRepository;
import com.example.aims.model.Book;
import com.example.aims.model.CD;
import com.example.aims.model.DVD;
import com.example.aims.model.Product;
import com.example.aims.common.ProductType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SearchProductTest {

    @Mock
    private ProductFactory productFactory;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BookStrategy bookStrategy;

    @Mock
    private CdStrategy cdStrategy;

    @Mock
    private DvdStrategy dvdStrategy;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    public void setUp() {
        // Setup default behavior for ProductFactory
        lenient().when(productFactory.getStrategy("book")).thenReturn(bookStrategy);
        lenient().when(productFactory.getStrategy("cd")).thenReturn(cdStrategy);
        lenient().when(productFactory.getStrategy("dvd")).thenReturn(dvdStrategy);
    }

    @Test
    void testSearchProducts_ReturnsMatchingProducts() {
        // Given
        String keyword = "test";

        // Create mock products that would be returned by repository
        Book mockBook = new Book();
        mockBook.setProductID("BK-001");
        mockBook.setCategory(ProductType.book);
        mockBook.setTitle("Test Book");

        CD mockCD = new CD();
        mockCD.setProductID("CD-001");
        mockCD.setCategory(ProductType.cd);
        mockCD.setTitle("Test Album");

        // Mock repository to return products matching the search
        when(productRepository.findByTitleContainingIgnoreCase(keyword))
                .thenReturn(Arrays.asList(mockBook, mockCD));

        // Mock strategies to return DTOs
        BookDTO bookDTO = createTestBookDTO();
        bookDTO.setProductID("BK-001");
        bookDTO.setTitle("Test Book");

        CdDTO cdDTO = createTestCdDTO();
        cdDTO.setProductID("CD-001");
        cdDTO.setTitle("Test Album");

        when(bookStrategy.getProductById("BK-001")).thenReturn(bookDTO);
        when(cdStrategy.getProductById("CD-001")).thenReturn(cdDTO);

        // When
        List<ProductDTO> result = productService.searchProducts(keyword);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(productRepository).findByTitleContainingIgnoreCase(keyword);
        verify(bookStrategy).getProductById("BK-001");
        verify(cdStrategy).getProductById("CD-001");
    }

    @Test
    void testSearchProducts_NoMatchingProducts_ReturnsEmptyList() {
        // Given
        String keyword = "nonexistent";

        // Mock repository to return empty list
        when(productRepository.findByTitleContainingIgnoreCase(keyword))
                .thenReturn(Arrays.asList());

        // When
        List<ProductDTO> result = productService.searchProducts(keyword);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(productRepository).findByTitleContainingIgnoreCase(keyword);
        verify(bookStrategy, never()).getProductById(anyString());
        verify(cdStrategy, never()).getProductById(anyString());
        verify(dvdStrategy, never()).getProductById(anyString());
    }

    @Test
    void testSearchProducts_WithPagination_ReturnsPagedResults() {
        // Given
        String keyword = "test";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        // Create mock products
        Book mockBook = new Book();
        mockBook.setProductID("BK-001");
        mockBook.setCategory(ProductType.book);
        mockBook.setTitle("Test Book");

        CD mockCD = new CD();
        mockCD.setProductID("CD-001");
        mockCD.setCategory(ProductType.cd);
        mockCD.setTitle("Test Album");

        // Create page with products
        Page<Product> productPage = new PageImpl<>(
                Arrays.asList(mockBook, mockCD),
                pageable,
                2);

        // Mock repository to return paged results
        when(productRepository.findByTitleContainingIgnoreCase(keyword, pageable))
                .thenReturn(productPage);

        // Mock strategies to return DTOs
        BookDTO bookDTO = createTestBookDTO();
        bookDTO.setProductID("BK-001");
        bookDTO.setTitle("Test Book");

        CdDTO cdDTO = createTestCdDTO();
        cdDTO.setProductID("CD-001");
        cdDTO.setTitle("Test Album");

        when(bookStrategy.getProductById("BK-001")).thenReturn(bookDTO);
        when(cdStrategy.getProductById("CD-001")).thenReturn(cdDTO);

        // When
        var result = productService.searchProducts(keyword, page, size);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(2, result.getTotalElements());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());

        verify(productRepository).findByTitleContainingIgnoreCase(keyword, pageable);
        verify(bookStrategy).getProductById("BK-001");
        verify(cdStrategy).getProductById("CD-001");
    }

    @Test
    void testSearchProducts_MixedProductTypes_ReturnsCorrectDTOs() {
        // Given
        String keyword = "programming";

        // Create mock products of different types
        Book mockBook = new Book();
        mockBook.setProductID("BK-001");
        mockBook.setCategory(ProductType.book);
        mockBook.setTitle("Programming Book");

        DVD mockDVD = new DVD();
        mockDVD.setProductID("DVD-001");
        mockDVD.setCategory(ProductType.dvd);
        mockDVD.setTitle("Programming Tutorial");

        // Mock repository to return mixed products
        when(productRepository.findByTitleContainingIgnoreCase(keyword))
                .thenReturn(Arrays.asList(mockBook, mockDVD));

        // Mock strategies to return appropriate DTOs
        BookDTO bookDTO = createTestBookDTO();
        bookDTO.setProductID("BK-001");
        bookDTO.setTitle("Programming Book");

        DvdDTO dvdDTO = createTestDvdDTO();
        dvdDTO.setProductID("DVD-001");
        dvdDTO.setTitle("Programming Tutorial");

        when(bookStrategy.getProductById("BK-001")).thenReturn(bookDTO);
        when(dvdStrategy.getProductById("DVD-001")).thenReturn(dvdDTO);

        // When
        List<ProductDTO> result = productService.searchProducts(keyword);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify we got the correct types
        assertTrue(result.stream().anyMatch(p -> p instanceof BookDTO));
        assertTrue(result.stream().anyMatch(p -> p instanceof DvdDTO));

        // Verify the titles match
        assertTrue(result.stream().anyMatch(p -> p.getTitle().contains("Programming")));

        verify(productRepository).findByTitleContainingIgnoreCase(keyword);
        verify(bookStrategy).getProductById("BK-001");
        verify(dvdStrategy).getProductById("DVD-001");
        verify(cdStrategy, never()).getProductById(anyString());
    }

    @Test
    void testSearchProducts_CaseInsensitiveSearch() {
        // Given
        String keyword = "TEST";

        // Create mock product
        Book mockBook = new Book();
        mockBook.setProductID("BK-001");
        mockBook.setCategory(ProductType.book);
        mockBook.setTitle("Test Book");

        // Mock repository to return product (case insensitive search)
        when(productRepository.findByTitleContainingIgnoreCase(keyword))
                .thenReturn(Arrays.asList(mockBook));

        // Mock strategy to return DTO
        BookDTO bookDTO = createTestBookDTO();
        bookDTO.setProductID("BK-001");
        bookDTO.setTitle("Test Book");

        when(bookStrategy.getProductById("BK-001")).thenReturn(bookDTO);

        // When
        List<ProductDTO> result = productService.searchProducts(keyword);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());

        verify(productRepository).findByTitleContainingIgnoreCase(keyword);
        verify(bookStrategy).getProductById("BK-001");
    }

    // --- Helper Methods ---
    private BookDTO createTestBookDTO() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("Java Programming");
        bookDTO.setCategory("book");
        bookDTO.setPrice(150000.0);
        bookDTO.setValue(120000.0);
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
        dvdDTO.setDescription("Marvel superhero movie");
        dvdDTO.setDirector("Russo Brothers");
        dvdDTO.setStudio("Marvel Studios");
        dvdDTO.setRuntime("120 minutes");
        dvdDTO.setDiscType("Blu-ray");
        dvdDTO.setSubtitle("English, Vietnamese");
        return dvdDTO;
    }

    private Product createProduct(String title, double price) {
        Book p = new Book();
        p.setTitle(title);
        p.setPrice(price);
        return p;
    }
}