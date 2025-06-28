package com.example.aims.mapper;

import com.example.aims.common.ProductType;
import com.example.aims.dto.products.ProductDTO;
import com.example.aims.model.Product;

import java.util.List;

/**
 * Abstract mapper interface for Product entities and DTOs.
 * This interface defines common mapping operations that can be shared
 * across different product type mappers.
 *
 * @param <T> the specific product entity type that extends Product
 * @param <D> the specific product DTO type that extends ProductDTO
 */
public interface ProductMapper<T extends Product, D extends ProductDTO> {

    D toDTO(T entity);

    T toEntity(D dto);

    List<D> toDTOList(List<T> entities);

    List<T> toEntityList(List<D> dtos);

    ProductType getProductType();
}