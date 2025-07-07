package com.example.aims.service.products;

import com.example.aims.dto.products.ProductDTO;
import com.example.aims.dto.PagedResponse;
import com.example.aims.factory.ProductFactory;
import com.example.aims.model.Product;
import com.example.aims.repository.ProductRepository;
import com.example.aims.strategy.ProductStrategy;
import com.example.aims.common.ProductType;
import com.example.aims.exception.ResourceNotFoundException;
import com.example.aims.exception.PriceChangeException;
import com.example.aims.service.ManagerActivityService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PRODUCT-SERVICE")
public class ProductServiceImpl implements ProductService {

    private final ProductFactory productFactory;
    private final ProductRepository productRepository;
    private final ManagerActivityService managerActivityService;

    @Override
    public List<ProductDTO> getAllProducts() {
        return productFactory.getSupportedTypes().stream()
                .flatMap(type -> productFactory.getStrategy(type).getAllProducts().stream())
                .collect(Collectors.toList());
    }

    @Override
    public PagedResponse<ProductDTO> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);

        List<ProductDTO> productDTOs = productPage.getContent().stream()
                .map(product -> {
                    ProductStrategy strategy = productFactory.getStrategy(product.getCategory().name());
                    return strategy.getProductById(product.getProductID());
                })
                .collect(Collectors.toList());

        return new PagedResponse<>(
                productDTOs,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements());
    }

    @Override
    public ProductDTO getProductById(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        ProductStrategy strategy = productFactory.getStrategy(product.getCategory().name());
        return strategy.getProductById(productId);
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO, Integer managerID) {
        String productType = productDTO.getCategory();
        ProductStrategy strategy = productFactory.getStrategy(productType);
        return strategy.createProduct(productDTO);
    }

    @Override
    public ProductDTO updateProduct(String productId, ProductDTO productDTO) {
        String productType = productDTO.getCategory();
        ProductStrategy strategy = productFactory.getStrategy(productType);
        
        // Get current product to check if price will change
        Product currentProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        boolean priceWillChange = !currentProduct.getPrice().equals(productDTO.getPrice());
        
        // If price will change, validate BEFORE updating
        if (priceWillChange) {
            // Check and reset daily counters if it's a new day
            checkAndResetDailyCounters(currentProduct);
            
            // Validate price change limits
            validatePriceChangeLimits(currentProduct);
            validatePriceRange(productDTO.getPrice(), currentProduct.getValue());
        }
        managerActivityService.checkAndIncrementUpdateCount(1);
        
        ProductDTO updatedProduct = strategy.updateProduct(productId, productDTO);
        
        // If price changed, update tracking fields
        if (priceWillChange) {
            // Update tracking fields
            if (currentProduct.getUpdateCount() == null) currentProduct.setUpdateCount(0);
            currentProduct.setOldPrice(currentProduct.getPrice());
            currentProduct.setPrice(productDTO.getPrice());
            currentProduct.setUpdateCount(currentProduct.getUpdateCount() + 1);
            currentProduct.setUpdateAt(LocalDate.now());
            productRepository.save(currentProduct);
            
            log.info("Product price updated during product update. Product: {}, Old: {}, New: {}", 
                    productId, currentProduct.getOldPrice(), currentProduct.getPrice());
        }
        
        return updatedProduct;
    }

    @Override
    public void deleteProduct(String productId) {
        // Check daily delete limit
        managerActivityService.checkAndIncrementDeleteCount(1);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        ProductStrategy strategy = productFactory.getStrategy(product.getCategory().name());
        strategy.deleteProduct(productId);
    }

    @Override
    public void deleteProducts(List<String> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Product IDs list cannot be null or empty");
        }
        if (productIds.size() > 10) {
            throw new IllegalArgumentException("Cannot delete more than 10 products at once");
        }
        
        // Check daily delete limit for batch operation
        managerActivityService.checkAndIncrementDeleteCount(productIds.size());
        
        for (String productId : productIds) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
            ProductStrategy strategy = productFactory.getStrategy(product.getCategory().name());
            strategy.deleteProduct(productId);
        }
    }



    @Override
    public PagedResponse<ProductDTO> getFilteredProducts(String keyword, String category, Double minPrice,
            Double maxPrice, String sortBy, int page, int size) {
        try {
            log.info("getFilteredProducts called with: keyword={}, category={}, minPrice={}, maxPrice={}, sortBy={}, page={}, size={}", 
                     keyword, category, minPrice, maxPrice, sortBy, page, size);
            
            Pageable pageable = PageRequest.of(page, size, getSort(sortBy));
            ProductType categoryEnum = null;
            
            if (category != null && !category.equalsIgnoreCase("all") && !category.isBlank()) {
                try {
                    categoryEnum = ProductType.valueOf(category.toLowerCase());
                    log.info("Category enum resolved to: {}", categoryEnum);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid category provided: {}, treating as null", category);
                    categoryEnum = null;
                }
            }
            
            log.info("Calling repository.searchProducts with categoryEnum: {}", categoryEnum);
            Page<Product> productPage = productRepository.searchProducts(
                    (keyword == null || keyword.isBlank()) ? null : keyword,
                    categoryEnum,
                    minPrice,
                    maxPrice,
                    pageable);
            
            log.info("Repository returned {} products", productPage.getContent().size());
            
            List<ProductDTO> dtos = productPage.getContent().stream()
                    .map(product -> {
                        try {
                            log.debug("Processing product: {} with category: {}", product.getProductID(), product.getCategory());
                            ProductStrategy strategy = productFactory.getStrategy(product.getCategory().name());
                            return strategy.getProductById(product.getProductID());
                        } catch (Exception e) {
                            log.error("Error processing product {}: {}", product.getProductID(), e.getMessage(), e);
                            throw new RuntimeException("Error processing product " + product.getProductID(), e);
                        }
                    })
                    .collect(Collectors.toList());
            
            log.info("Successfully converted {} products to DTOs", dtos.size());
            return new PagedResponse<>(dtos, page, size, productPage.getTotalElements());
            
        } catch (Exception e) {
            log.error("Error in getFilteredProducts: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get filtered products: " + e.getMessage(), e);
        }
    }

    private Sort getSort(String sortBy) {
        if (sortBy == null || sortBy.isBlank())
            return Sort.unsorted();
        switch (sortBy) {
            case "title_asc":
                return Sort.by("title").ascending();
            case "title_desc":
                return Sort.by("title").descending();
            case "price_asc":
                return Sort.by("price").ascending();
            case "price_desc":
                return Sort.by("price").descending();
            default:
                return Sort.unsorted();
        }
    }
    
    // Price management methods
    @Override
    public void updateProductPrice(String productId, Double newPrice, Integer managerId, String reason) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new PriceChangeException("Product not found with id: " + productId));
        
        Double oldPrice = product.getPrice();
        Double productValue = product.getPrice();
        
        // Check if it's a new day and reset counters
        checkAndResetDailyCounters(product);
        
        // Validate price change limits
        validatePriceChangeLimits(product);
        
        // Validate price range
        validatePriceRange(newPrice, productValue);
        
        // Update product price and tracking fields
        product.setOldPrice(oldPrice);
        product.setPrice(newPrice);
        product.setUpdateCount(product.getUpdateCount() + 1);
        product.setUpdateAt(LocalDate.now());
        productRepository.save(product);
        
        log.info("Product price updated. Product: {}, Old: {}, New: {}, Manager: {}, Update Count: {}", 
                productId, oldPrice, newPrice, managerId, product.getUpdateCount());
    }
    
    @Override
    public Integer getDailyPriceChangeCount(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new PriceChangeException("Product not found with id: " + productId));
        
        checkAndResetDailyCounters(product);
        return product.getUpdateCount();
    }
    
    @Override
    public Double getOldPrice(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new PriceChangeException("Product not found with id: " + productId));
        
        checkAndResetDailyCounters(product);
        return product.getOldPrice();
    }
    
    private void checkAndResetDailyCounters(Product product) {
        LocalDate today = LocalDate.now();
        LocalDate lastUpdateDate = product.getUpdateAt();
        
        if (lastUpdateDate == null || !lastUpdateDate.equals(today)) {
            // New day, reset counters
            product.setOldPrice(product.getPrice());
            product.setUpdateCount(0);
            product.setUpdateAt(LocalDate.now());
            log.info("Reset daily counters for product: {}", product.getProductID());
        } else if (product.getUpdateCount() == null) {
            product.setUpdateCount(0);
        }
    }
    
    private void validatePriceChangeLimits(Product product) {
        if (product.getUpdateCount() == null) product.setUpdateCount(0);
        if (product.getUpdateCount() >= 2) { // MAX_PRICE_CHANGES_PER_DAY = 2
            throw new PriceChangeException(
                String.format("Maximum price changes per day (2) has been reached for product %s", 
                    product.getProductID()));
        }
    }
    
    private void validatePriceRange(Double newPrice, Double productValue) {
        Double minPrice = productValue * 0.30; // MIN_PRICE_PERCENTAGE = 0.30
        Double maxPrice = productValue * 1.50; // MAX_PRICE_PERCENTAGE = 1.50
        
        if (newPrice < minPrice) {
            throw new PriceChangeException(
                String.format("Price %.2f is below minimum allowed price %.2f (30%% of product value %.2f)", 
                    newPrice, minPrice, productValue));
        }
        
        if (newPrice > maxPrice) {
            throw new PriceChangeException(
                String.format("Price %.2f is above maximum allowed price %.2f (150%% of product value %.2f)", 
                    newPrice, maxPrice, productValue));
        }
    }
}
