package com.example.aims.service.products;

import com.example.aims.dto.products.ProductDTO;
import com.example.aims.factory.ProductFactory;
import com.example.aims.model.Product;
import com.example.aims.repository.ProductRepository;
import com.example.aims.strategy.ProductStrategy;

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
    public ProductDTO getProductById(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        ProductStrategy strategy = productFactory.getStrategy(product.getCategory());
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
        ProductStrategy strategy = productFactory.getStrategy(product.getCategory());
        strategy.deleteProduct(productId);
    }

    @Override
    public List<ProductDTO> searchProducts(String query) {
        List<Product> products = productRepository.findByTitleContainingIgnoreCase(query);
        return products.stream()
                .map(product -> {
                    ProductStrategy strategy = productFactory.getStrategy(product.getCategory());
                    return strategy.getProductById(product.getProductID());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByCategory(String category) {
        ProductStrategy strategy = productFactory.getStrategy(category);
        return strategy.getAllProducts();
    }
}
