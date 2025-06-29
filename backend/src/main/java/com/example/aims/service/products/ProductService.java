package com.example.aims.service.products;

import com.example.aims.dto.products.ProductDTO;
import com.example.aims.dto.PagedResponse;

import java.util.List;

public interface ProductService {

    List<ProductDTO> getAllProducts();

    PagedResponse<ProductDTO> getAllProducts(int page, int size);

    ProductDTO getProductById(String productId);

    ProductDTO createProduct(ProductDTO productDTO, Integer managerID);

    ProductDTO updateProduct(String productId, ProductDTO productDTO);

    /*
     * Unified filtering, searching and pagination
     */
    PagedResponse<ProductDTO> getFilteredProducts(
            String keyword,
            String category,
            Double minPrice,
            Double maxPrice,
            String sortBy,
            int page,
            int size);

    void deleteProduct(String productId);

    List<ProductDTO> searchProducts(String query);

    PagedResponse<ProductDTO> searchProducts(String query, int page, int size);

    List<ProductDTO> getProductsByCategory(String category);

    PagedResponse<ProductDTO> getProductsByCategory(String category, int page, int size);
}