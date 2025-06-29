package com.example.aims.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.aims.common.OrderStatus;

/**
 * ✅ SOLID Evaluation for OrderManagementResponseDTO:
 * - S (SRP): Single responsibility - only holds management response data
 * - O (OCP): Open for extension, closed for modification
 * - L (LSP): Not applicable for DTOs
 * - I (ISP): Not applicable for DTOs
 * - D (DIP): No dependencies on concrete classes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderManagementResponseDTO {
    private String orderId;
    private OrderStatus newStatus;
    private String message;
    private boolean success;
    
    // Factory methods for common responses
    public static OrderManagementResponseDTO success(String orderId, OrderStatus newStatus, String message) {
        return new OrderManagementResponseDTO(orderId, newStatus, message, true);
    }
    
    public static OrderManagementResponseDTO failure(String orderId, String message) {
        return new OrderManagementResponseDTO(orderId, null, message, false);
    }
} 