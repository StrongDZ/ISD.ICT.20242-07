package com.example.aims.service.rush.eligibility;

import com.example.aims.model.Product;
import org.springframework.stereotype.Component;

/**
 * ✅ High Cohesion:
 *     - This class is solely responsible for determining if a Product is eligible for rush delivery.
 *     - It encapsulates the logic related to product rush eligibility.
 *
 * ✅ S - Single Responsibility Principle (SRP):
 *     - Only checks product’s rush eligibility status.
 *     - No other responsibilities like address validation or order handling.
 *
 * ✅ O - Open/Closed Principle (OCP):
 *     - The class can be extended to support more complex eligibility checks (e.g., checking product category or inventory)
 *       without modifying this implementation directly, by subclassing or strategy pattern.
 *
 * ✅ L - Liskov Substitution Principle (LSP):
 *     - Implements the RushEligibility<Product> interface correctly.
 *     - Can be replaced by any other implementation of RushEligibility<Product> without breaking client code.
 *
 * ✅ I - Interface Segregation Principle (ISP):
 *     - Implements a focused interface with a single method.
 *
 * ✅ D - Dependency Inversion Principle (DIP):
 *     - Depends only on the RushEligibility abstraction.
 *     - No direct dependencies on concrete classes beyond Product, which is a domain model.
 */
@Component
public class ProductRushEligibility implements RushEligibility<Product> {

    /**
     * Checks if the product is eligible for rush delivery based on its internal flag.
     *
     * @param product the product to check
     * @return true if product is not null and is marked as rush eligible; false otherwise
     */
    @Override
    public boolean isRushAllowed(Product product) {
        return product != null && product.isRushEligible();
    }
}
