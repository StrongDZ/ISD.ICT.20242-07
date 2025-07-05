package com.example.aims.dto.rush;

import com.example.aims.dto.products.ProductDTO;
import lombok.Data;
import java.util.List;

/**
 * ✅ Represents the result of evaluating a rush order request.
 * ✅ High Cohesion: All fields and methods relate to describing the result of a rush order eligibility check.
 * ✅ Follows SRP: Only stores result data for response, does not mix logic.
 *
 * SOLID Evaluation:
 * - S (Single Responsibility Principle): This class only holds data about rush order response, no business logic.
 * - O (Open/Closed Principle):
 *   Can be extended with new fields if needed without modifying existing methods.
 * - L (Liskov Substitution Principle):
 *   As a simple DTO, can be substituted wherever this type is expected.
 * - I (Interface Segregation Principle):
 *   Not applicable since this is a data transfer object without interfaces.
 * - D (Dependency Inversion Principle):
 *   This class depends on low-level data structures (ProductDTO), which is acceptable for a DTO.
 */
@Data
public class PlaceRushOrderResponse {

    // ✅ Indicates if rush delivery is supported based on address and product check
    private boolean supported;

    // ✅ Optional message shown to user if rush delivery is not supported
    private String promptMessage;

    // ✅ Products eligible for rush delivery
    private List<ProductDTO> rushProducts;

    // ✅ Products not eligible for rush delivery
    private List<ProductDTO> regularProducts;

    // ✅ Lombok @Data automatically generates all getters and setters
    // No need for manual getter/setter methods
}
