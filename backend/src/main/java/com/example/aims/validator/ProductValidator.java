package com.example.aims.validator;

import com.example.aims.dto.ProductDTO;
import com.example.aims.exception.BadRequestException;
import com.example.aims.repository.ProductRepository;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductValidator {
    private final ProductRepository productRepository;

    public void validateProductCreation(ProductDTO productDTO) {
        if (productDTO.getProductID() != null && productRepository.existsById(productDTO.getProductID())) {
            throw new BadRequestException("Product with ID " + productDTO.getProductID() + " already exists.");
        }

        String category = productDTO.getCategory().toLowerCase();
        if (!category.equals("book") && !category.equals("cd") && !category.equals("dvd")) {
            throw new BadRequestException("Invalid category: " + category);
        }
    }

    public void validateProductUpdate(String id, ProductDTO productDTO) {
        if (!productRepository.existsById(id)) {
            throw new BadRequestException("Product not found with id: " + id);
        }

        String category = productDTO.getCategory().toLowerCase();
        if (!category.equals("book") && !category.equals("cd") && !category.equals("dvd")) {
            throw new BadRequestException("Invalid category: " + category);
        }
    }
} 