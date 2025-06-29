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
import com.example.aims.common.ProductType;
import com.example.aims.exception.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ViewProductDetailsTest {

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
        lenient().when(productFactory.getSupportedTypes()).thenReturn(Arrays.asList("book", "cd", "dvd"));
    }

    @Test
    void testGetProductById_BookFound_ReturnsCorrectDTO() {
        // Given
        String productId = "BK-123456";
        BookDTO expectedBookDTO = createTestBookDTO();
        expectedBookDTO.setProductID(productId);

        // Mock the repository to return a product
        Book mockBook = new Book();
        mockBook.setProductID(productId);
        mockBook.setCategory(ProductType.book);
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockBook));

        // Mock: book strategy succeeds
        when(bookStrategy.getProductById(productId)).thenReturn(expectedBookDTO);

        // When
        ProductDTO result = productService.getProductById(productId);

        // Then
        assertNotNull(result);
        assertTrue(result instanceof BookDTO);
        BookDTO bookResult = (BookDTO) result;
        assertEquals(productId, bookResult.getProductID());
        assertEquals("Java Programming", bookResult.getTitle());
        assertEquals("book", bookResult.getCategory());
        assertEquals("Robert Martin", bookResult.getAuthors());

        verify(productRepository).findById(productId);
        verify(bookStrategy, times(1)).getProductById(productId);
    }

    @Test
    void testGetProductById_CDFound_ReturnsCorrectDTO() {
        // Given
        String productId = "CD-123456";
        CdDTO expectedCdDTO = createTestCdDTO();
        expectedCdDTO.setProductID(productId);

        // Mock the repository to return a product
        CD mockCD = new CD();
        mockCD.setProductID(productId);
        mockCD.setCategory(ProductType.cd);
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockCD));

        // Mock: cd strategy succeeds
        when(cdStrategy.getProductById(productId)).thenReturn(expectedCdDTO);

        // When
        ProductDTO result = productService.getProductById(productId);

        // Then
        assertNotNull(result);
        assertTrue(result instanceof CdDTO);
        CdDTO cdResult = (CdDTO) result;
        assertEquals(productId, cdResult.getProductID());
        assertEquals("Greatest Hits", cdResult.getTitle());
        assertEquals("cd", cdResult.getCategory());
        assertEquals("Queen", cdResult.getArtist());

        verify(productRepository).findById(productId);
        verify(cdStrategy, times(1)).getProductById(productId);
    }

    @Test
    void testGetProductById_DVDFound_ReturnsCorrectDTO() {
        // Given
        String productId = "DVD-123456";
        DvdDTO expectedDvdDTO = createTestDvdDTO();
        expectedDvdDTO.setProductID(productId);

        // Mock the repository to return a product
        DVD mockDVD = new DVD();
        mockDVD.setProductID(productId);
        mockDVD.setCategory(ProductType.dvd);
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockDVD));

        // Mock: dvd strategy succeeds
        when(dvdStrategy.getProductById(productId)).thenReturn(expectedDvdDTO);

        // When
        ProductDTO result = productService.getProductById(productId);

        // Then
        assertNotNull(result);
        assertTrue(result instanceof DvdDTO);
        DvdDTO dvdResult = (DvdDTO) result;
        assertEquals(productId, dvdResult.getProductID());
        assertEquals("Avengers", dvdResult.getTitle());
        assertEquals("dvd", dvdResult.getCategory());
        assertEquals("Russo Brothers", dvdResult.getDirector());

        verify(productRepository).findById(productId);
        verify(dvdStrategy, times(1)).getProductById(productId);
    }

    @Test
    void testGetProductById_ProductNotFound_ThrowsException() {
        // Given
        String productId = "NONEXISTENT-123";

        // Mock the repository to return empty
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductById(productId));

        assertEquals("Product not found with id: " + productId, exception.getMessage());

        verify(productRepository).findById(productId);
        // No strategies should be called since product is not found in repository
        verify(bookStrategy, never()).getProductById(anyString());
        verify(cdStrategy, never()).getProductById(anyString());
        verify(dvdStrategy, never()).getProductById(anyString());
    }

    @Test
    void testGetAllProducts_ReturnsAllProductTypes() {
        // Given
        BookDTO bookDTO = createTestBookDTO();
        bookDTO.setProductID("BK-001");

        CdDTO cdDTO = createTestCdDTO();
        cdDTO.setProductID("CD-001");

        DvdDTO dvdDTO = createTestDvdDTO();
        dvdDTO.setProductID("DVD-001");

        when(bookStrategy.getAllProducts()).thenReturn(Arrays.asList(bookDTO));
        when(cdStrategy.getAllProducts()).thenReturn(Arrays.asList(cdDTO));
        when(dvdStrategy.getAllProducts()).thenReturn(Arrays.asList(dvdDTO));

        // When
        List<ProductDTO> result = productService.getAllProducts();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());

        // Verify we got one of each type
        assertTrue(result.stream().anyMatch(p -> p instanceof BookDTO));
        assertTrue(result.stream().anyMatch(p -> p instanceof CdDTO));
        assertTrue(result.stream().anyMatch(p -> p instanceof DvdDTO));

        verify(bookStrategy, times(1)).getAllProducts();
        verify(cdStrategy, times(1)).getAllProducts();
        verify(dvdStrategy, times(1)).getAllProducts();
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
}
