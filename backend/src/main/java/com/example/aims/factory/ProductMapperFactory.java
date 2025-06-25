package com.example.aims.factory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.aims.exception.BadRequestException;
import com.example.aims.mapper.ProductMapper;

@Component
public class ProductMapperFactory {

    private final Map<String, ProductMapper<?, ?>> mappers;

    public ProductMapperFactory(List<ProductMapper<?, ?>> mappers) {
        this.mappers = mappers.stream()
                .collect(Collectors.toMap(ProductMapper::getProductType, Function.identity()));
    }

    public ProductMapper<?, ?> getMapper(String productType) {
        if (productType == null || productType.trim().isEmpty()) {
            throw new BadRequestException("Product type cannot be null or empty");
        }

        String normalizedType = productType.toLowerCase().trim();
        ProductMapper<?, ?> mapper = mappers.get(normalizedType);

        if (mapper == null) {
            throw new BadRequestException("Unsupported product type: " + productType +
                    ". Supported types are: " + String.join(", ", mappers.keySet()));
        }

        return mapper;
    }

    public List<String> getSupportedTypes() {
        return mappers.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
    }
}