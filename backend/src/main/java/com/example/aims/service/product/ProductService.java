package com.example.aims.service.product;

import com.example.aims.dto.products.ProductDTO;
import com.example.aims.validator.OnResponse;

import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import java.util.List;

@Validated(OnResponse.class)
public interface ProductService {

    List<@Valid ProductDTO> getAllProducts();

    @Valid
    ProductDTO getProductById(String productId);

    @Valid
    ProductDTO createProduct(ProductDTO productDTO, String managerID);

    @Valid
    ProductDTO updateProduct(String productId, ProductDTO productDTO);

    void deleteProduct(String productId);

    List<@Valid ProductDTO> searchProducts(String query);

    List<@Valid ProductDTO> getProductsByCategory(String category);
}