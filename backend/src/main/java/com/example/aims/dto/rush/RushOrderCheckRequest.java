package com.example.aims.dto.rush;

import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.dto.products.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ✅ DTO for rush order check request.
 * ✅ High Cohesion: All fields and methods relate to rush order check request.
 * ✅ Follows SRP: This class only holds data for rush order check request.
 *
 * SOLID Evaluation:
 * - S (Single Responsibility Principle): Focused solely on holding rush order check request data.
 * - O (Open/Closed Principle): Can be extended with new fields without modifying existing ones.
 * - L (Liskov Substitution Principle): As a plain DTO, can be substituted where rush order check request is needed.
 * - I (Interface Segregation Principle): Not applicable; no interfaces implemented here.
 * - D (Dependency Inversion Principle): Depends only on basic types and other DTOs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RushOrderCheckRequest {
    
    /**
     * Delivery information for the rush order check
     */
    private DeliveryInfoDTO deliveryInfo;
    
    /**
     * List of products to check for rush eligibility
     */
    private List<ProductDTO> products;

    // Manual getters and setters to ensure compatibility
    public DeliveryInfoDTO getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(DeliveryInfoDTO deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }
} 