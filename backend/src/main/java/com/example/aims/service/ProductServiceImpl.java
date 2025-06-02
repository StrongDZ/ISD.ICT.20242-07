package com.example.aims.service;

import com.example.aims.dto.ProductDTO;
import com.example.aims.exception.ResourceNotFoundException;
import com.example.aims.factory.ProductFactory;
import com.example.aims.mapper.ProductMapper;
import com.example.aims.model.*;
import com.example.aims.repository.*;
import com.example.aims.validator.ProductValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final BookRepository bookRepository;
    private final CDRepository cdRepository;
    private final DVDRepository dvdRepository;
    private final ShopItemRepository shopItemRepository;
    private final UsersRepository userRepository;
    private final ProductMapper productMapper;
    private final ProductValidator productValidator;
    private final ProductFactory productFactory;

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return productMapper.toDTO(product);
    }

    @Override
    public List<ProductDTO> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> searchProducts(String keyword) {
        return productRepository.findByTitleContainingIgnoreCase(keyword).stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO, String managerID) {
        if (productDTO.getProductID() == null || productDTO.getProductID().isEmpty()) {
            productDTO.setProductID(UUID.randomUUID().toString());
        }

        productValidator.validateProductCreation(productDTO);
        
        Product product = productFactory.createProduct(productDTO);
        productRepository.save(product);
        
        createTypeSpecificProduct(productDTO, product);
        
        Users manager = userRepository.findById(managerID)
                .orElseThrow(() -> new ResourceNotFoundException("Users", "id", managerID));
        
        createShopItem(product, manager);
        
        return productMapper.toDTO(product);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(String id, ProductDTO productDTO) {
        productValidator.validateProductUpdate(id, productDTO);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        updateBaseProduct(product, productDTO);
        updateTypeSpecificProduct(product, productDTO);
        
        return productMapper.toDTO(product);
    }

    @Override
    @Transactional
    public void deleteProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        deleteTypeSpecificProduct(product);
        deleteShopItem(id);
        productRepository.deleteById(id);
    }

    private void createTypeSpecificProduct(ProductDTO dto, Product product) {
        switch (product.getCategory().toLowerCase()) {
            case "book":
                bookRepository.save(productFactory.createBook(dto, product));
                break;
            case "cd":
                cdRepository.save(productFactory.createCD(dto, product));
                break;
            case "dvd":
                dvdRepository.save(productFactory.createDVD(dto, product));
                break;
        }
    }

    private void updateBaseProduct(Product product, ProductDTO dto) {
        product.setCategory(dto.getCategory());
        product.setTitle(dto.getTitle());
        product.setValue(dto.getValue());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setDescription(dto.getDescription());
        product.setBarcode(dto.getBarcode());
        product.setWarehouseEntryDate(dto.getWarehouseEntryDate());
        product.setDimensions(dto.getDimensions());
        product.setWeight(dto.getWeight());
        product.setImageURL(dto.getImageURL());
        productRepository.save(product);
    }

    private void updateTypeSpecificProduct(Product product, ProductDTO dto) {
        switch (product.getCategory().toLowerCase()) {
            case "book":
                updateBook(product, dto);
                break;
            case "cd":
                updateCD(product, dto);
                break;
            case "dvd":
                updateDVD(product, dto);
                break;
        }
    }

    private void updateBook(Product product, ProductDTO dto) {
        Book book = bookRepository.findById(product.getProductID())
                .orElseGet(() -> productFactory.createBook(dto, product));
        book.setCoverType(dto.getCoverType());
        book.setAuthors(dto.getAuthors());
        book.setPublisher(dto.getPublisher());
        book.setNumberOfPages(dto.getNumberOfPages());
        book.setLanguage(dto.getLanguage());
        book.setGenre(dto.getGenre());
        book.setPubDate(dto.getPubDate());
        bookRepository.save(book);
    }

    private void updateCD(Product product, ProductDTO dto) {
        CD cd = cdRepository.findById(product.getProductID())
                .orElseGet(() -> productFactory.createCD(dto, product));
        cd.setTracklist(dto.getTracklist());
        cd.setArtist(dto.getArtist());
        cd.setReleaseDate(dto.getReleaseDate());
        cd.setRecordLabel(dto.getRecordLabel());
        cd.setMusicType(dto.getMusicType());
        cdRepository.save(cd);
    }

    private void updateDVD(Product product, ProductDTO dto) {
        DVD dvd = dvdRepository.findById(product.getProductID())
                .orElseGet(() -> productFactory.createDVD(dto, product));
        dvd.setDiscType(dto.getDiscType());
        dvd.setRuntime(dto.getRuntime());
        dvd.setStudio(dto.getStudio());
        dvd.setDirector(dto.getDirector());
        dvd.setSubtitle(dto.getSubtitle());
        dvd.setReleaseDate(dto.getReleaseDate());
        dvd.setLanguage(dto.getLanguage());
        dvd.setGenre(dto.getGenre());
        dvdRepository.save(dvd);
    }

    private void createShopItem(Product product, Users manager) {
        ShopItem shopItem = new ShopItem();
        ShopItem.ShopItemId shopItemId = new ShopItem.ShopItemId(product.getProductID(), manager.getId());
        shopItem.setId(shopItemId);
        shopItem.setProduct(product);
        shopItem.setManager(manager);
        shopItemRepository.save(shopItem);
    }

    private void deleteTypeSpecificProduct(Product product) {
        switch (product.getCategory().toLowerCase()) {
            case "book":
                bookRepository.deleteById(product.getProductID());
                break;
            case "cd":
                cdRepository.deleteById(product.getProductID());
                break;
            case "dvd":
                dvdRepository.deleteById(product.getProductID());
                break;
        }
    }

    private void deleteShopItem(String productId) {
        ShopItem.ShopItemId shopItemId = new ShopItem.ShopItemId();
        shopItemId.setProductID(productId);
        shopItemRepository.deleteById(shopItemId);
    }
} 