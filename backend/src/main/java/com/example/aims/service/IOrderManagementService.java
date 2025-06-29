package com.example.aims.service;

import com.example.aims.dto.order.OrderApprovalRequestDTO;
import com.example.aims.dto.order.OrderRejectionRequestDTO;
import com.example.aims.dto.order.OrderManagementResponseDTO;

/**
 * ✅ SOLID Evaluation for IOrderManagementService:
 * 
 * - S (SRP): Single responsibility - handles order approval/rejection operations only
 * - O (OCP): Open for extension through interface implementation
 * - L (LSP): Implementations must be substitutable for this interface
 * - I (ISP): Interface is focused only on order management, not bloated with unrelated methods
 * - D (DIP): High-level modules depend on this abstraction, not concrete implementations
 * 
 * This interface follows ISP by providing only order management-specific methods,
 * avoiding fat interface anti-pattern.
 */
public interface IOrderManagementService {
    
    /**
     * Approves a pending order and updates its status to APPROVED.
     * 
     * @param approvalRequest contains order ID and approval details
     * @return response with operation result and new order status
     * @throws com.example.aims.exception.BadRequestException if order cannot be approved
     */
    OrderManagementResponseDTO approveOrder(OrderApprovalRequestDTO approvalRequest);
    
    /**
     * Rejects a pending order and updates its status to REJECTED.
     * 
     * @param rejectionRequest contains order ID, reason, and rejection details
     * @return response with operation result and new order status
     * @throws com.example.aims.exception.BadRequestException if order cannot be rejected
     */
    OrderManagementResponseDTO rejectOrder(OrderRejectionRequestDTO rejectionRequest);
    
    /**
     * Validates if an order is eligible for status change operations.
     * 
     * @param orderId the order to validate
     * @return true if order can be approved/rejected, false otherwise
     */
    boolean isOrderEligibleForManagement(String orderId);
} 