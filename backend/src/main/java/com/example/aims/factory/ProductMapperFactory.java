package com.example.aims.factory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.aims.exception.BadRequestException;
import com.example.aims.mapper.ProductMapper;
import com.example.aims.common.ProductType;

@Component
public class ProductMapperFactory {

    private final Map<ProductType, ProductMapper<?, ?>> mappers;

    public ProductMapperFactory(List<ProductMapper<?, ?>> mappers) {
        this.mappers = mappers.stream()
                .collect(Collectors.toMap(ProductMapper::getProductType, Function.identity()));
    }

    public ProductMapper<?, ?> getMapper(ProductType productType) {
        if (productType == null) {
            throw new BadRequestException("Product type cannot be null or empty");
        }

        ProductMapper<?, ?> mapper = mappers.get(productType);

        if (mapper == null) {
            throw new BadRequestException("Unsupported product type: " + productType +
                    ". Supported types are: " + mappers.keySet().stream()
                            .map(ProductType::name)
                            .collect(Collectors.joining(", ")));
        }

        return mapper;
    }

    public List<ProductType> getSupportedTypes() {
        return mappers.keySet().stream()
                .map(Function.identity())
                .sorted()
                .collect(Collectors.toList());
    }
}