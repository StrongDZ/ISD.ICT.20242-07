package com.example.aims.service.products;

import com.example.aims.dto.products.ProductDTO;
import com.example.aims.dto.PagedResponse;
import com.example.aims.factory.ProductFactory;
import com.example.aims.model.Product;
import com.example.aims.repository.ProductRepository;
import com.example.aims.strategy.ProductStrategy;
import com.example.aims.common.ProductType;
import com.example.aims.exception.ResourceNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PRODUCT-SERVICE")
public class ProductServiceImpl implements ProductService {

    private final ProductFactory productFactory;
    private final ProductRepository productRepository;

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
        return strategy.updateProduct(productId, productDTO);
    }

    @Override
    public void deleteProduct(String productId) {
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

    
}
