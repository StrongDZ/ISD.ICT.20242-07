package com.example.aims.service.products;

import com.example.aims.dto.products.ProductDTO;
import com.example.aims.factory.ProductFactory;
import com.example.aims.strategy.ProductStrategy;
import com.example.aims.validator.OnResponse;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated(OnResponse.class)
public class ProductServiceImpl implements ProductService {

    private final ProductFactory productFactory;

    @Override
    public List<@Valid ProductDTO> getAllProducts() {
        return productFactory.getSupportedTypes().stream()
                .flatMap(type -> productFactory.getStrategy(type).getAllProducts().stream())
                .collect(Collectors.toList());
    }

    @Override
    public @Valid ProductDTO getProductById(String productId) {
        for (String type : productFactory.getSupportedTypes()) {
            try {
                ProductStrategy strategy = productFactory.getStrategy(type);
                return strategy.getProductById(productId);
            } catch (Exception e) {
                // ignore and try next
            }
        }
        throw new RuntimeException("Product not found with id: " + productId);
    }

    @Override
    public @Valid ProductDTO createProduct(ProductDTO productDTO, Integer managerID) {
        String productType = productDTO.getCategory();
        ProductStrategy strategy = productFactory.getStrategy(productType);
        return strategy.createProduct(productDTO);
    }

    @Override
    public @Valid ProductDTO updateProduct(String productId, ProductDTO productDTO) {
        String productType = productDTO.getCategory();
        ProductStrategy strategy = productFactory.getStrategy(productType);
        return strategy.updateProduct(productId, productDTO);
    }

    @Override
    public void deleteProduct(String productId) {
        boolean deleted = false;
        for (String type : productFactory.getSupportedTypes()) {
            try {
                ProductStrategy strategy = productFactory.getStrategy(type);
                strategy.deleteProduct(productId);
                deleted = true;
                break;
            } catch (Exception e) {
                // ignore and try next
            }
        }
        if (!deleted) {
            throw new RuntimeException("Product not found with id: " + productId);
        }
    }

    @Override
    public List<@Valid ProductDTO> searchProducts(String query) {
        return productFactory.getSupportedTypes().stream()
                .flatMap(type -> productFactory.getStrategy(type).searchProducts(query).stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<@Valid ProductDTO> getProductsByCategory(String category) {
        ProductStrategy strategy = productFactory.getStrategy(category);
        return strategy.getAllProducts();
    }
}
