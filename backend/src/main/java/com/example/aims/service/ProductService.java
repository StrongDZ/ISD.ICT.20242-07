package com.example.aims.service;

import com.example.aims.dto.ProductDTO;
import com.example.aims.model.*;
import com.example.aims.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final BookRepository bookRepository;
    private final CDRepository cdRepository;
    private final DVDRepository dvdRepository;
    private final ShopItemRepository shopItemRepository;
    private final UsersRepository userRepository;

    public ProductService(ProductRepository productRepository, BookRepository bookRepository,
                         CDRepository cdRepository, DVDRepository dvdRepository,
                         ShopItemRepository shopItemRepository, UsersRepository userRepository) {
        this.productRepository = productRepository;
        this.bookRepository = bookRepository;
        this.cdRepository = cdRepository;
        this.dvdRepository = dvdRepository;
        this.shopItemRepository = shopItemRepository;
        this.userRepository = userRepository;
    }

    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOs = new ArrayList<>();
        
        for (Product product : products) {
            ProductDTO dto = convertToDTO(product);
            productDTOs.add(dto);
        }
        
        return productDTOs;
    }

    public ProductDTO getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        return convertToDTO(product);
    }

    public List<ProductDTO> getProductsByCategory(String category) {
        List<Product> products = productRepository.findByCategory(category);
        List<ProductDTO> productDTOs = new ArrayList<>();
        
        for (Product product : products) {
            ProductDTO dto = convertToDTO(product);
            productDTOs.add(dto);
        }
        
        return productDTOs;
    }

    public List<ProductDTO> searchProducts(String keyword) {
        List<Product> products = productRepository.findByTitleContainingIgnoreCase(keyword);
        List<ProductDTO> productDTOs = new ArrayList<>();
        
        for (Product product : products) {
            ProductDTO dto = convertToDTO(product);
            productDTOs.add(dto);
        }
        
        return productDTOs;
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO, String managerID) {   //Add product ro Store
        // Generate a new product ID if not provided
        if (productDTO.getProductID() == null || productDTO.getProductID().isEmpty()) {
            productDTO.setProductID(UUID.randomUUID().toString());
        }
        
        // Create the base product
        Product product = new Product();
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
        
        // Create the specific product type
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
        
        // Associate the product with the manager
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
    public ProductDTO updateProduct(String id, ProductDTO productDTO) {         //Update product to 
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        // Update the base product
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
        
        // Update the specific product type
        if ("book".equalsIgnoreCase(product.getCategory())) {
            Optional<Book> bookOpt = bookRepository.findById(id);
            Book book;
            
            if (bookOpt.isPresent()) {
                book = bookOpt.get();
            } else {
                book = new Book();
                book.setProductID(id);
                book.setProduct(product);
            }
            
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
            CD cd;
            
            if (cdOpt.isPresent()) {
                cd = cdOpt.get();
            } else {
                cd = new CD();
                cd.setProductID(id);
                cd.setProduct(product);
            }
            
            cd.setTracklist(productDTO.getTracklist());
            cd.setArtist(productDTO.getArtist());
            cd.setReleaseDate(productDTO.getReleaseDate());
            cd.setRecordLabel(productDTO.getRecordLabel());
            cd.setMusicType(productDTO.getMusicType());
            
            cdRepository.save(cd);
        } else if ("dvd".equalsIgnoreCase(product.getCategory())) {
            Optional<DVD> dvdOpt = dvdRepository.findById(id);
            DVD dvd;
            
            if (dvdOpt.isPresent()) {
                dvd = dvdOpt.get();
            } else {
                dvd = new DVD();
                dvd.setProductID(id);
                dvd.setProduct(product);
            }
            
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
        // First check if the product exists
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        // Delete specific product type
        if ("book".equalsIgnoreCase(product.getCategory())) {
            bookRepository.deleteById(id);
        } else if ("cd".equalsIgnoreCase(product.getCategory())) {
            cdRepository.deleteById(id);
        } else if ("dvd".equalsIgnoreCase(product.getCategory())) {
            dvdRepository.deleteById(id);
        }
        
        // Delete shop items
        ShopItem.ShopItemId shopItemId = new ShopItem.ShopItemId();
        shopItemId.setProductID(id);
        shopItemRepository.deleteById(shopItemId);
        
        // Delete the base product
        productRepository.deleteById(id);
    }

    private ProductDTO convertToDTO(Product product) {
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
        
        // Load specific product type details
        if ("book".equalsIgnoreCase(product.getCategory())) {
            Optional<Book> bookOpt = bookRepository.findById(product.getProductID());
            if (bookOpt.isPresent()) {
                Book book = bookOpt.get();
                dto.setCoverType(book.getCoverType());
                dto.setAuthors(book.getAuthors());
                dto.setPublisher(book.getPublisher());
                dto.setNumberOfPages(book.getNumberOfPages());
                dto.setLanguage(book.getLanguage());
                dto.setGenre(book.getGenre());
                dto.setPubDate(book.getPubDate());
            }
        } else if ("cd".equalsIgnoreCase(product.getCategory())) {
            Optional<CD> cdOpt = cdRepository.findById(product.getProductID());
            if (cdOpt.isPresent()) {
                CD cd = cdOpt.get();
                dto.setTracklist(cd.getTracklist());
                dto.setArtist(cd.getArtist());
                dto.setReleaseDate(cd.getReleaseDate());
                dto.setRecordLabel(cd.getRecordLabel());
                dto.setMusicType(cd.getMusicType());
            }
        } else if ("dvd".equalsIgnoreCase(product.getCategory())) {
            Optional<DVD> dvdOpt = dvdRepository.findById(product.getProductID());
            if (dvdOpt.isPresent()) {
                DVD dvd = dvdOpt.get();
                dto.setDiscType(dvd.getDiscType());
                dto.setRuntime(dvd.getRuntime());
                dto.setStudio(dvd.getStudio());
                dto.setDirector(dvd.getDirector());
                dto.setSubtitle(dvd.getSubtitle());
                dto.setReleaseDate(dvd.getReleaseDate());
                dto.setLanguage(dvd.getLanguage());
                dto.setGenre(dvd.getGenre());
            }
        }
        
        return dto;
    }
}