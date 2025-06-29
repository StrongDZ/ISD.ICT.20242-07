package com.example.aims.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ✅ SOLID Evaluation for OrderRejectionRequestDTO:
 * - S (SRP): Single responsibility - only holds rejection request data
 * - O (OCP): Open for extension, closed for modification
 * - L (LSP): Not applicable for DTOs
 * - I (ISP): Not applicable for DTOs
 * - D (DIP): No dependencies on concrete classes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRejectionRequestDTO {
    private String orderId;
    private String rejectedBy; // Manager ID or username who rejected
    private String reason; // Required rejection reason
    private String notes; // Optional additional notes
} 