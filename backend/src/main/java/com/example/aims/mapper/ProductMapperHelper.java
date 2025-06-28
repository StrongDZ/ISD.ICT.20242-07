package com.example.aims.mapper;

import java.util.Arrays;

import com.example.aims.exception.BadRequestException;
import com.example.aims.dto.products.ProductDTO;
import com.example.aims.factory.ProductMapperFactory;
import com.example.aims.model.Product;
import com.example.aims.common.ProductType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Helper class for mapping between Product entities and ProductDTOs.
 * This class encapsulates the logic for determining the correct mapper
 * based on product category and performing the mapping operations.
 */
@Component
public class ProductMapperHelper {

    @Autowired
    private ProductMapperFactory mapperFactory;

    /**
     * Maps a Product entity to its corresponding ProductDTO.
     * 
     * @param product the Product entity to map
     * @return the mapped ProductDTO, or null if product or category is null
     */
    @SuppressWarnings("unchecked")
    public ProductDTO mapProductToDTO(Product product) {
        if (product == null || product.getCategory() == null) {
            return null;
        }

        ProductMapper<?, ?> mapper = mapperFactory.getMapper(product.getCategory());
        return ((ProductMapper<Product, ProductDTO>) mapper).toDTO(product);
    }

    /**
     * Maps a ProductDTO to its corresponding Product entity.
     * 
     * @param productDTO the ProductDTO to map
     * @return the mapped Product entity, or null if productDTO or category is null
     */
    @SuppressWarnings("unchecked")
    public Product mapDTOToProduct(ProductDTO productDTO) {
        if (productDTO == null || productDTO.getCategory() == null) {
            return null;
        }

        try {
            ProductType productType = ProductType.valueOf(productDTO.getCategory());
            ProductMapper<?, ?> mapper = mapperFactory.getMapper(productType);
            return ((ProductMapper<Product, ProductDTO>) mapper).toEntity(productDTO);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid product category: " + productDTO.getCategory() +
                    ". Valid categories are: " +
                    Arrays.toString(ProductType.values()));
        }
    }
}