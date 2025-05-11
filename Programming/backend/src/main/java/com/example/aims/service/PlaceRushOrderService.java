package com.example.aims.service;

import com.example.aims.dto.PlaceRushOrderResponse;
import com.example.aims.model.DeliveryInfo;
import com.example.aims.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PlaceRushOrderService {
     // Cohesion: High – all methods are related to evaluating rush orders.
    // SRP: Violated – this class is handling multiple responsibilities:
    //   - Checking if the delivery district supports rush delivery,
    //   - Filtering rush-eligible products,
    //   - Constructing the response object.
    // Suggested improvement: Split into separate services or helper classes, one for each responsibility.
    private static final Set<String> INNER_CITY_DISTRICTS = Set.of(
            "Ba Dinh", "Hoan Kiem", "Dong Da", "Hai Ba Trung", "Thanh Xuan", "Hoang Mai", "Ha Dong", "Long Bien"
    );
      // OCP Violation: This hardcoded list prevents extensibility.
    // Suggestion: Move the list to a config file or external service.

    public PlaceRushOrderResponse evaluateRushOrder(DeliveryInfo deliveryInfo, List<Product> products) {
        String addressDistrict = deliveryInfo.getProvince();
        boolean addressSupported = isRushDeliverySupported(addressDistrict);

        List<Product> rushProducts = getRushEligibleProducts(products);
        List<Product> regularProducts = products.stream()
                .filter(p -> !p.isRushEligible())
                .collect(Collectors.toList());

        PlaceRushOrderResponse result = new PlaceRushOrderResponse();
        result.setRushProducts(rushProducts);
        result.setRegularProducts(regularProducts);
        result.setSupported(addressSupported && !rushProducts.isEmpty());

        if (!result.isSupported()) {
            result.setPromptMessage("Rush order not available. Please update your delivery address or product selection.");
        }

        return result;
    }

    public boolean isRushDeliverySupported(String district) {
        return INNER_CITY_DISTRICTS.contains(district);
    }

    public List<Product> getRushEligibleProducts(List<Product> products) {
        return products.stream()
                .filter(Product::isRushEligible)
                .collect(Collectors.toList());
    }
}