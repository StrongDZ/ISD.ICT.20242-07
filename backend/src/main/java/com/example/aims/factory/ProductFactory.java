package com.example.aims.factory;

import com.example.aims.exception.BadRequestException;
import com.example.aims.strategy.ProductStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ProductFactory {

    private final Map<String, ProductStrategy> strategies;

    public ProductFactory(List<ProductStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(
                        ProductStrategy::getProductType,
                        Function.identity()));
    }

    public ProductStrategy getStrategy(String productType) {
        if (productType == null || productType.trim().isEmpty()) {
            throw new BadRequestException("Product type cannot be null or empty");
        }

        String normalizedType = productType.toLowerCase().trim();
        ProductStrategy strategy = strategies.get(normalizedType);

        if (strategy == null) {
            throw new BadRequestException("Unsupported product type: " + productType +
                    ". Supported types are: " + String.join(", ", strategies.keySet()));
        }

        return strategy;
    }

    public List<String> getSupportedTypes() {
        return strategies.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
    }
}