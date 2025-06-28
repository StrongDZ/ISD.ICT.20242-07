package com.example.aims.mapper;

import com.example.aims.dto.CartItemDTO;
import com.example.aims.dto.products.BookDTO;
import com.example.aims.dto.products.CdDTO;
import com.example.aims.dto.products.DvdDTO;
import com.example.aims.dto.products.ProductDTO;
import com.example.aims.model.Book;
import com.example.aims.model.CD;
import com.example.aims.model.CartItem;
import com.example.aims.model.DVD;
import com.example.aims.model.Product;
import com.example.aims.model.Users;
import com.example.aims.common.ProductType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CartItemMapperTest {

    @Autowired
    private CartItemMapper cartItemMapper;

    private Users mockCustomer;
    private Book mockBook;
    private CD mockCD;
    private DVD mockDVD;

    @BeforeEach
    void setUp() {
        // Setup mock customer
        mockCustomer = new Users();
        mockCustomer.setId(1);
        mockCustomer.setUsername("testuser");
        mockCustomer.setGmail("test@example.com");

        // Setup mock products
        mockBook = createMockBook();
        mockCD = createMockCD();
        mockDVD = createMockDVD();
    }

    @Test
    void testToDTO_WithBookProduct_ReturnsCorrectCartItemDTO() {
        // Given
        CartItem cartItem = createCartItem(mockBook, 2);

        // When
        CartItemDTO result = cartItemMapper.toDTO(cartItem);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getQuantity());
        assertNotNull(result.getProductDTO());
        assertTrue(result.getProductDTO() instanceof BookDTO);

        BookDTO bookDTO = (BookDTO) result.getProductDTO();
        assertEquals("550e8400-e29b-41d4-a716-446655440000", bookDTO.getProductID());
        assertEquals("Test Book", bookDTO.getTitle());
        assertEquals("book", bookDTO.getCategory());
        assertEquals("Test Author", bookDTO.getAuthors());
    }

    @Test
    void testToDTO_WithCDProduct_ReturnsCorrectCartItemDTO() {
        // Given
        CartItem cartItem = createCartItem(mockCD, 3);

        // When
        CartItemDTO result = cartItemMapper.toDTO(cartItem);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getQuantity());
        assertNotNull(result.getProductDTO());
        assertTrue(result.getProductDTO() instanceof CdDTO);

        CdDTO cdDTO = (CdDTO) result.getProductDTO();
        assertEquals("550e8400-e29b-41d4-a716-446655440001", cdDTO.getProductID());
        assertEquals("Test Album", cdDTO.getTitle());
        assertEquals("cd", cdDTO.getCategory());
        assertEquals("Test Artist", cdDTO.getArtist());
    }

    @Test
    void testToDTO_WithDVDProduct_ReturnsCorrectCartItemDTO() {
        // Given
        CartItem cartItem = createCartItem(mockDVD, 1);

        // When
        CartItemDTO result = cartItemMapper.toDTO(cartItem);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getQuantity());
        assertNotNull(result.getProductDTO());
        assertTrue(result.getProductDTO() instanceof DvdDTO);

        DvdDTO dvdDTO = (DvdDTO) result.getProductDTO();
        assertEquals("550e8400-e29b-41d4-a716-446655440002", dvdDTO.getProductID());
        assertEquals("Test Movie", dvdDTO.getTitle());
        assertEquals("dvd", dvdDTO.getCategory());
        assertEquals("Test Director", dvdDTO.getDirector());
    }

    @Test
    void testToDTO_WithNullProduct_ReturnsNullProductDTO() {
        // Given
        CartItem cartItem = createCartItem(null, 1);

        // When
        CartItemDTO result = cartItemMapper.toDTO(cartItem);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getQuantity());
        assertNull(result.getProductDTO());
    }

    @Test
    void testToEntity_WithBookDTO_ReturnsCorrectCartItem() {
        // Given
        BookDTO bookDTO = createMockBookDTO();
        CartItemDTO cartItemDTO = createCartItemDTO(bookDTO, 2);

        // When
        CartItem result = cartItemMapper.toEntity(cartItemDTO);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getQuantity());
        assertNotNull(result.getProduct());
        assertTrue(result.getProduct() instanceof Book);

        Book book = (Book) result.getProduct();
        assertEquals("550e8400-e29b-41d4-a716-446655440000", book.getProductID());
        assertEquals("Test Book", book.getTitle());
        assertEquals(ProductType.book, book.getCategory());
    }

    @Test
    void testToEntity_WithCDDTO_ReturnsCorrectCartItem() {
        // Given
        CdDTO cdDTO = createMockCdDTO();
        CartItemDTO cartItemDTO = createCartItemDTO(cdDTO, 3);

        // When
        CartItem result = cartItemMapper.toEntity(cartItemDTO);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getQuantity());
        assertNotNull(result.getProduct());
        assertTrue(result.getProduct() instanceof CD);

        CD cd = (CD) result.getProduct();
        assertEquals("550e8400-e29b-41d4-a716-446655440001", cd.getProductID());
        assertEquals("Test Album", cd.getTitle());
        assertEquals(ProductType.cd, cd.getCategory());
    }

    @Test
    void testToEntity_WithDVDDTO_ReturnsCorrectCartItem() {
        // Given
        DvdDTO dvdDTO = createMockDvdDTO();
        CartItemDTO cartItemDTO = createCartItemDTO(dvdDTO, 1);

        // When
        CartItem result = cartItemMapper.toEntity(cartItemDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getQuantity());
        assertNotNull(result.getProduct());
        assertTrue(result.getProduct() instanceof DVD);

        DVD dvd = (DVD) result.getProduct();
        assertEquals("550e8400-e29b-41d4-a716-446655440002", dvd.getProductID());
        assertEquals("Test Movie", dvd.getTitle());
        assertEquals(ProductType.dvd, dvd.getCategory());
    }

    @Test
    void testToEntity_WithNullProductDTO_ReturnsNullProduct() {
        // Given
        CartItemDTO cartItemDTO = createCartItemDTO(null, 1);

        // When
        CartItem result = cartItemMapper.toEntity(cartItemDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getQuantity());
        assertNull(result.getProduct());
    }

    // --- Helper Methods ---
    private CartItem createCartItem(Product product, Integer quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setId(new CartItem.CartItemId(1, product != null ? product.getProductID() : "PROD-001"));
        cartItem.setCustomer(mockCustomer);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        return cartItem;
    }

    private CartItemDTO createCartItemDTO(ProductDTO productDTO, Integer quantity) {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setProductDTO(productDTO);
        cartItemDTO.setQuantity(quantity);
        return cartItemDTO;
    }

    private Book createMockBook() {
        Book book = new Book();
        book.setProductID("550e8400-e29b-41d4-a716-446655440000");
        book.setTitle("Test Book");
        book.setCategory(ProductType.book);
        book.setPrice(150000.0);
        book.setValue(120000.0);
        book.setQuantity(10);
        book.setDescription("A test book");
        book.setAuthors("Test Author");
        book.setPublisher("Test Publisher");
        book.setNumberOfPages(300);
        book.setLanguage("English");
        book.setGenre("Fiction");
        book.setCoverType("Hardcover");
        book.setPubDate(new Date());
        book.setEligible(true);
        return book;
    }

    private CD createMockCD() {
        CD cd = new CD();
        cd.setProductID("550e8400-e29b-41d4-a716-446655440001");
        cd.setTitle("Test Album");
        cd.setCategory(ProductType.cd);
        cd.setPrice(100000.0);
        cd.setValue(80000.0);
        cd.setQuantity(5);
        cd.setDescription("A test album");
        cd.setArtist("Test Artist");
        cd.setRecordLabel("Test Label");
        cd.setMusicType("Rock");
        cd.setTracklist("Track 1, Track 2, Track 3");
        cd.setReleaseDate(new Date());
        cd.setEligible(true);
        return cd;
    }

    private DVD createMockDVD() {
        DVD dvd = new DVD();
        dvd.setProductID("550e8400-e29b-41d4-a716-446655440002");
        dvd.setTitle("Test Movie");
        dvd.setCategory(ProductType.dvd);
        dvd.setPrice(200000.0);
        dvd.setValue(150000.0);
        dvd.setQuantity(8);
        dvd.setDescription("A test movie");
        dvd.setDirector("Test Director");
        dvd.setStudio("Test Studio");
        dvd.setRuntime("120 minutes");
        dvd.setDiscType("Blu-ray");
        dvd.setSubtitle("English, Vietnamese");
        dvd.setReleaseDate(new Date());
        dvd.setLanguage("English");
        dvd.setGenre("Action");
        dvd.setEligible(true);
        return dvd;
    }

    private BookDTO createMockBookDTO() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setProductID("550e8400-e29b-41d4-a716-446655440000");
        bookDTO.setTitle("Test Book");
        bookDTO.setCategory("book");
        bookDTO.setPrice(150000.0);
        bookDTO.setValue(120000.0);
        bookDTO.setQuantity(10);
        bookDTO.setDescription("A test book");
        bookDTO.setAuthors("Test Author");
        bookDTO.setPublisher("Test Publisher");
        bookDTO.setNumberOfPages(300);
        bookDTO.setLanguage("English");
        bookDTO.setGenre("Fiction");
        bookDTO.setCoverType("Hardcover");
        bookDTO.setPubDate(new Date());
        bookDTO.setEligible(true);
        return bookDTO;
    }

    private CdDTO createMockCdDTO() {
        CdDTO cdDTO = new CdDTO();
        cdDTO.setProductID("550e8400-e29b-41d4-a716-446655440001");
        cdDTO.setTitle("Test Album");
        cdDTO.setCategory("cd");
        cdDTO.setPrice(100000.0);
        cdDTO.setValue(80000.0);
        cdDTO.setQuantity(5);
        cdDTO.setDescription("A test album");
        cdDTO.setArtist("Test Artist");
        cdDTO.setRecordLabel("Test Label");
        cdDTO.setMusicType("Rock");
        cdDTO.setTracklist("Track 1, Track 2, Track 3");
        cdDTO.setReleaseDate(new Date());
        cdDTO.setEligible(true);
        return cdDTO;
    }

    private DvdDTO createMockDvdDTO() {
        DvdDTO dvdDTO = new DvdDTO();
        dvdDTO.setProductID("550e8400-e29b-41d4-a716-446655440002");
        dvdDTO.setTitle("Test Movie");
        dvdDTO.setCategory("dvd");
        dvdDTO.setPrice(200000.0);
        dvdDTO.setValue(150000.0);
        dvdDTO.setQuantity(8);
        dvdDTO.setDescription("A test movie");
        dvdDTO.setDirector("Test Director");
        dvdDTO.setStudio("Test Studio");
        dvdDTO.setRuntime("120 minutes");
        dvdDTO.setDiscType("Blu-ray");
        dvdDTO.setSubtitle("English, Vietnamese");
        dvdDTO.setReleaseDate(new Date());
        dvdDTO.setLanguage("English");
        dvdDTO.setGenre("Action");
        dvdDTO.setEligible(true);
        return dvdDTO;
    }
}