package com.example.aims.service.rush;

import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.dto.PlaceRushOrderResponse;
import com.example.aims.dto.products.ProductDTO;
import com.example.aims.service.rush.eligibility.RushEligibility;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlaceRushOrderService {

    // ✅ Dependency on abstractions, not concrete implementations.
    //    This follows the Dependency Inversion Principle (DIP).
    private final RushEligibility<DeliveryInfoDTO> addressEligibility;
    private final RushEligibility<ProductDTO> productEligibility;

    // ✅ Constructor-based dependency injection ensures better testability and loose coupling.
    public PlaceRushOrderService(RushEligibility<DeliveryInfoDTO> addressEligibility,
                                 RushEligibility<ProductDTO> productEligibility) {
        this.addressEligibility = addressEligibility;
        this.productEligibility = productEligibility;
    }

    /**
     * Determines if the order can be rushed based on delivery info and product eligibility.
     * Returns a response that splits products into rushable and non-rushable categories.
     *
     * ✅ High Cohesion: This method does only one thing — process rush order eligibility.
     * ✅ SRP (Single Responsibility Principle): This class focuses only on rush order handling.
     * ✅ OCP (Open/Closed Principle): New eligibility logic can be added without modifying this class.
     * ✅ LSP (Liskov Substitution Principle): Uses interface `RushEligibility<T>`, any implementation can be substituted.
     * ✅ DIP (Dependency Inversion Principle): Depends only on interfaces, not concrete classes.
     */
    public PlaceRushOrderResponse placeRushOrder(DeliveryInfoDTO deliveryInfo, List<ProductDTO> products) {
        // Determine if the delivery address qualifies for rush shipping
        boolean addressOk = addressEligibility.isRushAllowed(deliveryInfo);

        List<ProductDTO> rushProducts = new ArrayList<>();
        List<ProductDTO> regularProducts = new ArrayList<>();

        // Separate products into those eligible and not eligible for rush shipping
        for (ProductDTO product : products) {
            try {
                if (productEligibility.isRushAllowed(product)) {
                    rushProducts.add(product);
                } else {
                    regularProducts.add(product);
                }
            } catch (Exception e) {
                // If there's any error checking rush eligibility, treat as not eligible
                regularProducts.add(product);
            }
        }

        // A rush order is only supported if the address is eligible and there is at least one rushable product
        boolean supported = addressOk && !rushProducts.isEmpty();

        PlaceRushOrderResponse response = new PlaceRushOrderResponse();
        response.setSupported(supported);
        response.setRushProducts(rushProducts);
        response.setRegularProducts(regularProducts);

        if (!supported) {
            response.setPromptMessage("Rush order is not available. Please update delivery address or product selection.");
        }

        return response;
    }
}
