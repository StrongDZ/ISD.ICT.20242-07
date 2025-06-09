package com.example.aims.strategy;

import com.example.aims.dto.products.ProductDTO;
import java.util.List;

public interface ProductStrategy {
    ProductDTO createProduct(ProductDTO productDTO);

    ProductDTO updateProduct(String id, ProductDTO productDTO);

    void deleteProduct(String id);

    ProductDTO getProductById(String id);

    List<ProductDTO> getAllProducts();

    List<ProductDTO> searchProducts(String keyword);

    String getProductType();
}