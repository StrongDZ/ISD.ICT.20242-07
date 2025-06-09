 package com.example.aims.service;

 import com.example.aims.dto.ProductDTO;
 import com.example.aims.repository.*;
 import com.example.aims.service.ProductService;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.Test;
 import org.mockito.ArgumentCaptor;
 import org.mockito.Mockito;

 import com.example.aims.model.Book;
 import com.example.aims.model.CD;
 import com.example.aims.model.DVD;
 import com.example.aims.model.Product;
 import com.example.aims.model.ShopItem;
 import com.example.aims.model.Users;

 import java.time.LocalDate;
 import java.time.ZoneId;
 import java.util.Date;
 import java.util.Optional;

 import static org.junit.jupiter.api.Assertions.*;
 import static org.mockito.ArgumentMatchers.any;
 import static org.mockito.ArgumentMatchers.anyString;
 import static org.mockito.Mockito.*;

 public class AddUpdateProductToStoreTest {
     private ProductRepository productRepo;
     private BookRepository bookRepo;
     private CDRepository cdRepo;
     private DVDRepository dvdRepo;
     private ProductService productService;
     private ShopItemRepository shopItemRepository;
     private UsersRepository userRepository;

     @BeforeEach
     public void setUp() {
         productRepo = mock(ProductRepository.class);
         bookRepo = mock(BookRepository.class);
         cdRepo = mock(CDRepository.class);
         dvdRepo = mock(DVDRepository.class);
         shopItemRepository = mock(ShopItemRepository.class);
         userRepository = mock(UsersRepository.class);

         productService = new ProductService(productRepo, bookRepo, cdRepo, dvdRepo, shopItemRepository, userRepository);
     }

     // --- Test addProductToStore ---
     // public void testAddProductSuccess() {
     //     // Test adding a book product
     //     ProductDTO dto = new ProductDTO("P001", "Book", "Java Basics", 5, 120000, 150000);
     //     dto.setAuthors("John Doe");
     //     dto.setPublisher("TechBooks");
     //     dto.setGenre("Programming");

     //     when(productRepo.existsById("P001")).thenReturn(false);
     //     Product added = productService.addProductToStore(dto);
     //     assertEquals("P001", added.getId());
     //     verify(productRepo, times(1)).save(any(Product.class));
     //     verify(bookRepo, times(1)).save(any(Book.class));
     // }

     @Test
     public void testAddProductSuccess() {
         // Setup DTO dữ liệu mẫu cho Book
         ProductDTO dto = new ProductDTO();
         dto.setProductID("P001");
         dto.setCategory("book");
         dto.setTitle("Java Basics");
         dto.setQuantity(5);
         dto.setValue(120000d);
         dto.setPrice(150000d);
         dto.setAuthors("John Doe");
         dto.setPublisher("TechBooks");
         dto.setGenre("Programming");
    
         // Mock: người quản lý đã tồn tại trong hệ thống
         Users mockManager = new Users(1, "manager_user", "manager", "password123");
         when(userRepository.findById(1)).thenReturn(Optional.of(mockManager));
    
         // Mock các thao tác lưu
         when(productRepo.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
         when(bookRepo.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
         when(shopItemRepository.save(any(ShopItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
    
         // Gọi hàm createProduct
         ProductDTO result = productService.createProduct(dto, 1);
    
         // Kiểm tra kết quả trả về
         assertEquals("P001", result.getProductID());
         assertEquals("book", result.getCategory());
         assertEquals("Java Basics", result.getTitle());
    
         // Xác minh các phương thức lưu được gọi
         verify(productRepo, times(1)).save(any(Product.class));
         verify(bookRepo, times(1)).save(any(Book.class));
         verify(shopItemRepository, times(1)).save(any(ShopItem.class));
     }
    

     @Test
     public void testAddProductDuplicateId() {
         // Setup DTO với ID trùng lặp
         ProductDTO dto = new ProductDTO();
         dto.setProductID("P001");
         dto.setCategory("book");
         dto.setTitle("Java Basics");
         dto.setQuantity(5);
         dto.setValue(120000d);
         dto.setPrice(150000d);
         dto.setAuthors("John Doe");
         dto.setPublisher("TechBooks");
         dto.setGenre("Programming");

         // Giả lập product đã tồn tại
         when(productRepo.existsById("P001")).thenReturn(true);

         // Gọi phương thức và kiểm tra ngoại lệ
         Exception ex = assertThrows(RuntimeException.class, () ->
             productService.createProduct(dto, 1)
         );

         // Kiểm tra thông báo lỗi
         assertTrue(ex.getMessage().toLowerCase().contains("already exists"));
     }
    

     @Test
     public void testAddProductInvalidCategory() {
         // Tạo ProductDTO với category không hợp lệ
         ProductDTO dto = new ProductDTO();
         dto.setProductID("P002");
         dto.setCategory("toy");  // Không nằm trong "book", "cd", "dvd"
         dto.setTitle("Rubik");
         dto.setQuantity(5);
         dto.setValue(20000d);
         dto.setPrice(30000d);

         // Giả lập: chưa tồn tại product
         when(productRepo.existsById("P002")).thenReturn(false);
         when(userRepository.findById(1)).thenReturn(Optional.of(new Users()));

         // Gọi phương thức và bắt lỗi
         Exception ex = assertThrows(RuntimeException.class, () ->
             productService.createProduct(dto, 1)
         );

         // Kiểm tra thông báo lỗi
         assertTrue(ex.getMessage().toLowerCase().contains("invalid category"));
     }
    
     // --- Test updateProductToStore ---
     @Test
     public void testUpdateProductSuccess() {
         // Tạo DTO đầu vào với thông tin cập nhật
         ProductDTO dto = new ProductDTO();
         dto.setProductID("P001");
         dto.setCategory("DVD");
         dto.setTitle("Avengers");
         dto.setQuantity(8);
         dto.setValue(150000d);
         dto.setPrice(200000d);
         dto.setDirector("Russo");
         dto.setGenre("Action");
         dto.setRuntime("120 minutes");
         dto.setStudio("Marvel Studios");
    
         // Giả lập dữ liệu hiện có trong kho
         Product existing = new Product();
         existing.setProductID("P001");
         existing.setCategory("DVD");
         existing.setTitle("Avengers");
         existing.setQuantity(5);
         existing.setValue(100000d);
         existing.setPrice(180000d);
    
         when(productRepo.findById("P001")).thenReturn(Optional.of(existing));
    
         // Giả lập việc tìm kiếm DVD
         DVD existingDVD = new DVD();
         existingDVD.setProductID("P001");
         existingDVD.setProduct(existing); // liên kết ngược lại với Product
    
         when(dvdRepo.findById("P001")).thenReturn(Optional.of(existingDVD));
    
         // Gọi phương thức cập nhật
         ProductDTO updated = productService.updateProduct("P001", dto);
    
         // Kiểm tra kết quả cập nhật Product
         assertEquals(8, updated.getQuantity());
         assertEquals(150000d, updated.getValue());
         assertEquals(200000d, updated.getPrice());
         assertEquals("Avengers", updated.getTitle());
    
         // Kiểm tra thông tin DVD đã được cập nhật đúng
         assertEquals("Russo", existingDVD.getDirector());
         assertEquals("Action", existingDVD.getGenre());
         assertEquals("120 minutes", existingDVD.getRuntime());
         assertEquals("Marvel Studios", existingDVD.getStudio());
    
         // Xác minh thao tác save được gọi đúng
         verify(productRepo, times(1)).save(any(Product.class));
         verify(dvdRepo, times(1)).save(any(DVD.class));
     }
    
     @Test
     public void testUpdateCDProductSuccess() {
         // Tạo DTO đầu vào với thông tin cập nhật cho CD
         ProductDTO dto = new ProductDTO();
         dto.setProductID("C001");
         dto.setCategory("CD");
         dto.setTitle("Greatest Hits");
         dto.setQuantity(7);
         dto.setValue(80000d);
         dto.setPrice(100000d);
         dto.setArtist("Queen");
         dto.setRecordLabel("EMI");
         dto.setTracklist("Bohemian Rhapsody, Don't Stop Me Now");
         dto.setMusicType("Rock");
         dto.setReleaseDate(new Date());

         // Giả lập dữ liệu hiện có
         Product existing = new Product();
         existing.setProductID("C001");
         existing.setCategory("CD");
         existing.setTitle("Old CD");
         existing.setQuantity(3);
         existing.setValue(50000d);
         existing.setPrice(70000d);

         when(productRepo.findById("C001")).thenReturn(Optional.of(existing));

         // Giả lập CD tương ứng
         CD existingCD = new CD();
         existingCD.setProductID("C001");
         existingCD.setProduct(existing);

         when(cdRepo.findById("C001")).thenReturn(Optional.of(existingCD));

         // Gọi phương thức cập nhật
         ProductDTO updated = productService.updateProduct("C001", dto);

         // Kiểm tra kết quả cập nhật Product
         assertEquals("Greatest Hits", updated.getTitle());
         assertEquals(7, updated.getQuantity());
         assertEquals(80000d, updated.getValue());
         assertEquals(100000d, updated.getPrice());

         // Kiểm tra thông tin CD cập nhật đúng
         assertEquals("Queen", existingCD.getArtist());
         assertEquals("EMI", existingCD.getRecordLabel());
         assertEquals("Bohemian Rhapsody, Don't Stop Me Now", existingCD.getTracklist());
         assertEquals("Rock", existingCD.getMusicType());
         assertEquals(dto.getReleaseDate(), existingCD.getReleaseDate());

         // Xác minh thao tác save được gọi đúng
         verify(productRepo, times(1)).save(any(Product.class));
         verify(cdRepo, times(1)).save(any(CD.class));
     }

     @Test
     public void testUpdateBookProductSuccess() {
         ProductDTO dto = new ProductDTO();
         dto.setProductID("B001");
         dto.setCategory("Book");
         dto.setTitle("Clean Code");
         dto.setQuantity(10);
         dto.setValue(120000d);
         dto.setPrice(150000d);
         dto.setCoverType("Hardcover");
         dto.setAuthors("Robert C. Martin");
         dto.setPublisher("Prentice Hall");
         dto.setNumberOfPages(464);
         dto.setLanguage("English");
         dto.setGenre("Software Engineering");
         dto.setPubDate(new Date());

         Product existing = new Product();
         existing.setProductID("B001");
         existing.setCategory("Book");

         Book existingBook = new Book();
         existingBook.setProductID("B001");
         existingBook.setProduct(existing);

         when(productRepo.findById("B001")).thenReturn(Optional.of(existing));
         when(bookRepo.findById("B001")).thenReturn(Optional.of(existingBook));

         ProductDTO updated = productService.updateProduct("B001", dto);

         assertEquals("Clean Code", updated.getTitle());
         assertEquals(10, updated.getQuantity());
         assertEquals("Robert C. Martin", existingBook.getAuthors());
         assertEquals("Hardcover", existingBook.getCoverType());
         assertEquals("Prentice Hall", existingBook.getPublisher());
         assertEquals(464, existingBook.getNumberOfPages());
         assertEquals("English", existingBook.getLanguage());
         assertEquals("Software Engineering", existingBook.getGenre());
         assertEquals(dto.getPubDate(), existingBook.getPubDate());

         verify(productRepo, times(1)).save(any(Product.class));
         verify(bookRepo, times(1)).save(any(Book.class));
     }


        
     @Test
     public void testUpdateProductNotFound() {
         // Tạo DTO với thông tin cập nhật
         ProductDTO dto = new ProductDTO();
         dto.setProductID("P404");
         dto.setCategory("DVD");
         dto.setTitle("Ghost");
         dto.setQuantity(1);
         dto.setValue(10000d);
         dto.setPrice(12000d);
    
         // Giả lập không tìm thấy sản phẩm trong kho
         when(productRepo.findById("P404")).thenReturn(Optional.empty());
    
         // Gọi hàm và kiểm tra ngoại lệ được ném ra
         Exception ex = assertThrows(RuntimeException.class, () -> productService.updateProduct("P404", dto));
         assertTrue(ex.getMessage().contains("not found"));
     }


    

     @Test
     public void testCreateProduct_ManagerNotFound() {
         ProductDTO dto = new ProductDTO();
         dto.setProductID("P010");
         dto.setCategory("book");

         when(productRepo.existsById("P010")).thenReturn(false);
         when(userRepository.findById(1)).thenReturn(Optional.empty());

         Exception ex = assertThrows(RuntimeException.class, () -> productService.createProduct(dto, 1));
         assertTrue(ex.getMessage().toLowerCase().contains("manager not found"));
     }

     @Test
     public void testUpdateProduct_SaveError() {
         ProductDTO dto = new ProductDTO();
         dto.setCategory("book");

         Product existing = new Product();
         existing.setProductID("P001");
         existing.setCategory("book");

         when(productRepo.findById("P001")).thenReturn(Optional.of(existing));
         when(bookRepo.findById("P001")).thenReturn(Optional.of(new Book()));

         // Giả lập lỗi khi save
         when(bookRepo.save(any(Book.class))).thenThrow(new RuntimeException("Database error"));

         Exception ex = assertThrows(RuntimeException.class, () -> productService.updateProduct("P001", dto));

         assertTrue(ex.getMessage().contains("Database error"));
     }
     @Test
     public void testUpdateProduct_InvalidCategory() {
         ProductDTO dto = new ProductDTO();
         dto.setCategory("toy");  // category không hợp lệ

         when(productRepo.findById("P001")).thenReturn(Optional.of(new Product()));

         Exception ex = assertThrows(RuntimeException.class, () -> productService.updateProduct("P001", dto));

         assertTrue(ex.getMessage().contains("Invalid category"));
     }



    
 }