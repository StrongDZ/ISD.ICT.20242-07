package com.example.aims.service.rush.eligibility;

import com.example.aims.config.RushAddressProperties;
import com.example.aims.dto.DeliveryInfoDTO;
import org.springframework.stereotype.Component;

/**
 * ✅ High Cohesion:
 *     - This class has a single, well-defined purpose: check whether a delivery address is eligible for rush delivery.
 *     - It uses injected configuration (RushAddressProperties) to make its decision.
 *
 * ✅ S - Single Responsibility Principle (SRP):
 *     - The class is only responsible for validating address eligibility for rush delivery.
 *     - It doesn't deal with product validation, delivery logic, or any unrelated functionality.
 *
 * ✅ O - Open/Closed Principle (OCP):
 *     - The class can be extended (e.g., by subclassing or strategy modification) to support more complex rules
 *       like postal code filtering or geo-coordinates, without modifying its existing logic.
 *
 * ✅ L - Liskov Substitution Principle (LSP):
 *     - This class implements `RushEligibility<DeliveryInfoDTO>` and can be used wherever that interface is expected.
 *     - Replacing it with another implementation won’t break the system behavior.
 *
 * ✅ I - Interface Segregation Principle (ISP):
 *     - The interface `RushEligibility<T>` is simple and focused, and this class fully implements it.
 *
 * ✅ D - Dependency Inversion Principle (DIP):
 *     - This class depends on the abstraction (`RushEligibility`) and uses a configuration class (`RushAddressProperties`)
 *       injected via constructor, adhering to inversion of control and loose coupling.
 */
@Component
public class AddressRushEligibility implements RushEligibility<DeliveryInfoDTO> {

    // ✅ DIP: Configuration injected instead of hardcoded values
    private final RushAddressProperties properties;

    public AddressRushEligibility(RushAddressProperties properties) {
        this.properties = properties;
    }

    /**
     * ✅ SRP: Only checks address rush eligibility
     * ❗ Slight suggestion: Null-checking logic and trimming could be factored into helper methods for clarity/testability
     */
    @Override
    public boolean isRushAllowed(DeliveryInfoDTO deliveryInfo) {
        if (deliveryInfo == null
                || deliveryInfo.getCity() == null
                || deliveryInfo.getDistrict() == null) {
            return false;
        }

        String city = deliveryInfo.getCity().trim();
        String district = deliveryInfo.getDistrict().trim();

        // ✅ Business logic is clean and readable
        return city.equalsIgnoreCase(properties.getCity())
                && properties.getDistricts().stream()
                .anyMatch(d -> d.equalsIgnoreCase(district));
    }
}
