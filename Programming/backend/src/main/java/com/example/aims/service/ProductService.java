package com.example.aims.service;

import com.example.aims.dto.ProductDTO;
import com.example.aims.model.*;
import com.example.aims.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class ProductService {

    // COHESION: Logical Cohesion
    // This class groups together operations related to products (retrieving, creating, updating, deleting),
    // but each method performs different and independent operations.
    // For example: 
    // - getAllProducts(), getProductById(), and searchProducts() perform data retrieval,
    // - createProduct() and updateProduct() perform data mutation,
    // - convertToDTO() performs transformation.
    //
    // SRP VIOLATION: This class mixes multiple responsibilities:
    // 1. Product CRUD operations
    // 2. Category/type-specific logic (Book, CD, DVD creation/update)
    // 3. Validation logic (category validation, product existence check)
    // 4. DTO transformation logic
    // These responsibilities should be split into dedicated components or helper classes.

    private final ProductRepository productRepository;
    private final BookRepository bookRepository;
    private final CDRepository cdRepository;
    private final DVDRepository dvdRepository;
    private final ShopItemRepository shopItemRepository;
    private final UsersRepository userRepository;

    public List<ProductDTO> getAllProducts() {
        // This method performs product list retrieval and DTO transformation.
        // SRP: It violates SRP by including DTO transformation logic inside service.
        // SOLUTION: Extract DTO mapping to a separate ProductDTOMapper class.

        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOs = new ArrayList<>();
        
        for (Product product : products) {
            ProductDTO dto = convertToDTO(product);
            productDTOs.add(dto);
        }
        
        return productDTOs;
    }

    public ProductDTO getProductById(String id) {
        // SRP: This method is acceptable in terms of responsibility,
        // but still depends on convertToDTO(), which mixes mapping logic into service.
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        return convertToDTO(product);
    }

    public List<ProductDTO> getProductsByCategory(String category) {
        // Same issue as getAllProducts: combines data retrieval and DTO transformation.
        
        List<Product> products = productRepository.findByCategory(category);
        List<ProductDTO> productDTOs = new ArrayList<>();
        
        for (Product product : products) {
            ProductDTO dto = convertToDTO(product);
            productDTOs.add(dto);
        }
        
        return productDTOs;
    }

    public List<ProductDTO> searchProducts(String keyword) {
        // Again, mixes querying and DTO transformation.

        List<Product> products = productRepository.findByTitleContainingIgnoreCase(keyword);
        List<ProductDTO> productDTOs = new ArrayList<>();
        
        for (Product product : products) {
            ProductDTO dto = convertToDTO(product);
            productDTOs.add(dto);
        }
        
        return productDTOs;
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO, String managerID) {
        // This method handles:
        // 1. Product ID generation
        // 2. Category validation
        // 3. Base product creation
        // 4. Type-specific entity creation (Book/CD/DVD)
        // 5. ShopItem linking
        // 6. DTO transformation
        //
        // SRP VIOLATION: Too many responsibilities handled in a single method.
        // SOLUTION: 
        // - Extract category validation to ProductValidator
        // - Move Book/CD/DVD creation to respective services or use a ProductFactory
        // - Extract DTO mapping

        if (productDTO.getProductID() == null || productDTO.getProductID().isEmpty()) {
            productDTO.setProductID(UUID.randomUUID().toString());
        }
        if (productDTO.getProductID() != null && productRepository.existsById(productDTO.getProductID())) {
            throw new RuntimeException("Product with ID " + productDTO.getProductID() + " already exists.");
        }
        String category = productDTO.getCategory().toLowerCase();
        if (!category.equals("book") && !category.equals("cd") && !category.equals("dvd")) {
            throw new RuntimeException("Invalid category: " + category);
        }
        
        Product product = new Product();
        // Setting base product attributes
        product.setProductID(productDTO.getProductID());
        product.setCategory(productDTO.getCategory());
        product.setTitle(productDTO.getTitle());
        product.setValue(productDTO.getValue());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setDescription(productDTO.getDescription());
        product.setBarcode(productDTO.getBarcode());
        product.setWarehouseEntryDate(productDTO.getWarehouseEntryDate());
        product.setDimensions(productDTO.getDimensions());
        product.setWeight(productDTO.getWeight());
        product.setImageURL(productDTO.getImageURL());
        
        productRepository.save(product);
        
        // Type-specific product creation logic (should be extracted)
        if ("book".equalsIgnoreCase(productDTO.getCategory())) {
            Book book = new Book();
            book.setProductID(product.getProductID());
            book.setProduct(product);
            book.setCoverType(productDTO.getCoverType());
            book.setAuthors(productDTO.getAuthors());
            book.setPublisher(productDTO.getPublisher());
            book.setNumberOfPages(productDTO.getNumberOfPages());
            book.setLanguage(productDTO.getLanguage());
            book.setGenre(productDTO.getGenre());
            book.setPubDate(productDTO.getPubDate());
            
            bookRepository.save(book);
        } else if ("cd".equalsIgnoreCase(productDTO.getCategory())) {
            CD cd = new CD();
            cd.setProductID(product.getProductID());
            cd.setProduct(product);
            cd.setTracklist(productDTO.getTracklist());
            cd.setArtist(productDTO.getArtist());
            cd.setReleaseDate(productDTO.getReleaseDate());
            cd.setRecordLabel(productDTO.getRecordLabel());
            cd.setMusicType(productDTO.getMusicType());
            
            cdRepository.save(cd);
        } else if ("dvd".equalsIgnoreCase(productDTO.getCategory())) {
            DVD dvd = new DVD();
            dvd.setProductID(product.getProductID());
            dvd.setProduct(product);
            dvd.setDiscType(productDTO.getDiscType());
            dvd.setRuntime(productDTO.getRuntime());
            dvd.setStudio(productDTO.getStudio());
            dvd.setDirector(productDTO.getDirector());
            dvd.setSubtitle(productDTO.getSubtitle());
            dvd.setReleaseDate(productDTO.getReleaseDate());
            dvd.setLanguage(productDTO.getLanguage());
            dvd.setGenre(productDTO.getGenre());
            
            dvdRepository.save(dvd);
        }
        
        Users manager = userRepository.findById(managerID)
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + managerID));
        
        ShopItem shopItem = new ShopItem();
        ShopItem.ShopItemId shopItemId = new ShopItem.ShopItemId(product.getProductID(), manager.getId());
        shopItem.setId(shopItemId);
        shopItem.setProduct(product);
        shopItem.setManager(manager);
        
        shopItemRepository.save(shopItem);
        
        return convertToDTO(product);
    }

    @Transactional
    public ProductDTO updateProduct(String id, ProductDTO productDTO) {
        // Similar to createProduct:
        // - Performs category validation
        // - Updates base product
        // - Updates or creates type-specific entity
        //
        // SRP VIOLATION: Performs multiple unrelated tasks in one method.
        // SOLUTION: Extract category logic, use type-specific services.

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (!"book".equalsIgnoreCase(productDTO.getCategory()) &&
            !"cd".equalsIgnoreCase(productDTO.getCategory()) &&
            !"dvd".equalsIgnoreCase(productDTO.getCategory())) {
            throw new RuntimeException("Invalid category: " + productDTO.getCategory());
        }

        product.setCategory(productDTO.getCategory());
        product.setTitle(productDTO.getTitle());
        product.setValue(productDTO.getValue());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setDescription(productDTO.getDescription());
        product.setBarcode(productDTO.getBarcode());
        product.setWarehouseEntryDate(productDTO.getWarehouseEntryDate());
        product.setDimensions(productDTO.getDimensions());
        product.setWeight(productDTO.getWeight());
        product.setImageURL(productDTO.getImageURL());
        
        productRepository.save(product);
        
        // Type-specific update (should be handled by respective service)
        if ("book".equalsIgnoreCase(product.getCategory())) {
            Optional<Book> bookOpt = bookRepository.findById(id);
            Book book = bookOpt.orElseGet(() -> {
                Book newBook = new Book();
                newBook.setProductID(id);
                newBook.setProduct(product);
                return newBook;
            });

            book.setCoverType(productDTO.getCoverType());
            book.setAuthors(productDTO.getAuthors());
            book.setPublisher(productDTO.getPublisher());
            book.setNumberOfPages(productDTO.getNumberOfPages());
            book.setLanguage(productDTO.getLanguage());
            book.setGenre(productDTO.getGenre());
            book.setPubDate(productDTO.getPubDate());

            bookRepository.save(book);
        } else if ("cd".equalsIgnoreCase(product.getCategory())) {
            Optional<CD> cdOpt = cdRepository.findById(id);
            CD cd = cdOpt.orElseGet(() -> {
                CD newCd = new CD();
                newCd.setProductID(id);
                newCd.setProduct(product);
                return newCd;
            });

            cd.setTracklist(productDTO.getTracklist());
            cd.setArtist(productDTO.getArtist());
            cd.setReleaseDate(productDTO.getReleaseDate());
            cd.setRecordLabel(productDTO.getRecordLabel());
            cd.setMusicType(productDTO.getMusicType());

            cdRepository.save(cd);
        } else if ("dvd".equalsIgnoreCase(product.getCategory())) {
            Optional<DVD> dvdOpt = dvdRepository.findById(id);
            DVD dvd = dvdOpt.orElseGet(() -> {
                DVD newDvd = new DVD();
                newDvd.setProductID(id);
                newDvd.setProduct(product);
                return newDvd;
            });

            dvd.setDiscType(productDTO.getDiscType());
            dvd.setRuntime(productDTO.getRuntime());
            dvd.setStudio(productDTO.getStudio());
            dvd.setDirector(productDTO.getDirector());
            dvd.setSubtitle(productDTO.getSubtitle());
            dvd.setReleaseDate(productDTO.getReleaseDate());
            dvd.setLanguage(productDTO.getLanguage());
            dvd.setGenre(productDTO.getGenre());

            dvdRepository.save(dvd);
        }

        return convertToDTO(product);
    }

    @Transactional
    public void deleteProduct(String id) {
        // SRP: Acceptable responsibility but could still benefit from separating type-specific deletion.

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        if ("book".equalsIgnoreCase(product.getCategory())) {
            bookRepository.deleteById(id);
        } else if ("cd".equalsIgnoreCase(product.getCategory())) {
            cdRepository.deleteById(id);
        } else if ("dvd".equalsIgnoreCase(product.getCategory())) {
            dvdRepository.deleteById(id);
        }
        
        ShopItem.ShopItemId shopItemId = new ShopItem.ShopItemId();
        shopItemId.setProductID(id);
        shopItemRepository.deleteById(shopItemId);
        
        productRepository.deleteById(id);
    }

    private ProductDTO convertToDTO(Product product) {
        // SRP VIOLATION: This method contains mapping logic and also loads type-specific data.
        // SOLUTION: Extract this method to ProductDTOMapper or individual mappers per category.

        ProductDTO dto = new ProductDTO();
        dto.setProductID(product.getProductID());
        dto.setCategory(product.getCategory());
        dto.setTitle(product.getTitle());
        dto.setValue(product.getValue());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setDescription(product.getDescription());
        dto.setBarcode(product.getBarcode());
        dto.setWarehouseEntryDate(product.getWarehouseEntryDate());
        dto.setDimensions(product.getDimensions());
        dto.setWeight(product.getWeight());
        dto.setImageURL(product.getImageURL());
        
        if ("book".equalsIgnoreCase(product.getCategory())) {
            bookRepository.findById(product.getProductID()).ifPresent(book -> {
                dto.setCoverType(book.getCoverType());
                dto.setAuthors(book.getAuthors());
                dto.setPublisher(book.getPublisher());
                dto.setNumberOfPages(book.getNumberOfPages());
                dto.setLanguage(book.getLanguage());
                dto.setGenre(book.getGenre());
                dto.setPubDate(book.getPubDate());
            });
        } else if ("cd".equalsIgnoreCase(product.getCategory())) {
            cdRepository.findById(product.getProductID()).ifPresent(cd -> {
                dto.setTracklist(cd.getTracklist());
                dto.setArtist(cd.getArtist());
                dto.setReleaseDate(cd.getReleaseDate());
                dto.setRecordLabel(cd.getRecordLabel());
                dto.setMusicType(cd.getMusicType());
            });
        } else if ("dvd".equalsIgnoreCase(product.getCategory())) {
            dvdRepository.findById(product.getProductID()).ifPresent(dvd -> {
                dto.setDiscType(dvd.getDiscType());
                dto.setRuntime(dvd.getRuntime());
                dto.setStudio(dvd.getStudio());
                dto.setDirector(dvd.getDirector());
                dto.setSubtitle(dvd.getSubtitle());
                dto.setReleaseDate(dvd.getReleaseDate());
                dto.setLanguage(dvd.getLanguage());
                dto.setGenre(dvd.getGenre());
            });
        }
        
        return dto;
    }
}