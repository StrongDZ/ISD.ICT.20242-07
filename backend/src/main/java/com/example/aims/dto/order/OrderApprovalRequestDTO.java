package com.example.aims.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ✅ SOLID Evaluation for OrderApprovalRequestDTO:
 * - S (SRP): Single responsibility - only holds approval request data
 * - O (OCP): Open for extension, closed for modification
 * - L (LSP): Not applicable for DTOs
 * - I (ISP): Not applicable for DTOs
 * - D (DIP): No dependencies on concrete classes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderApprovalRequestDTO {
    private String orderId;
    private String approvedBy; // Manager ID or username who approved
    private String notes; // Optional approval notes
} 