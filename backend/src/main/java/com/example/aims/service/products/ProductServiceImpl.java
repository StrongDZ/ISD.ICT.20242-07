package com.example.aims.service.products;

import com.example.aims.dto.products.ProductDTO;
import com.example.aims.factory.ProductFactory;
import com.example.aims.strategy.ProductStrategy;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductFactory productFactory;

    @Override
    public List<ProductDTO> getAllProducts() {
        return productFactory.getSupportedTypes().stream()
                .flatMap(type -> productFactory.getStrategy(type).getAllProducts().stream())
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO getProductById(String productId) {
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
    public List<ProductDTO> searchProducts(String query) {
        return productFactory.getSupportedTypes().stream()
                .flatMap(type -> productFactory.getStrategy(type).searchProducts(query).stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByCategory(String category) {
        ProductStrategy strategy = productFactory.getStrategy(category);
        return strategy.getAllProducts();
    }
}
