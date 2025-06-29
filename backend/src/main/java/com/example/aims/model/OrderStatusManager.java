package com.example.aims.model;

import com.example.aims.common.OrderStatus;
import com.example.aims.exception.BadRequestException;

/**
 * Class responsible for managing order status transitions
 * Follows Single Responsibility Principle by focusing only on status management
 */
public class OrderStatusManager {
    private OrderStatus currentStatus;
    private final Order order;

    public OrderStatusManager(Order order) {
        this.order = order;
        this.currentStatus = order.getStatus();
    }

    public void approve() {
        validateTransition(OrderStatus.APPROVED);
        order.setStatus(OrderStatus.APPROVED);
        this.currentStatus = OrderStatus.APPROVED;
    }

    public void reject(String reason) {
        validateTransition(OrderStatus.REJECTED);
        order.setStatus(OrderStatus.REJECTED);
        order.setRejectedReason(reason);
        this.currentStatus = OrderStatus.REJECTED;
    }

    private void validateTransition(OrderStatus newStatus) {
        if (!isValidTransition(currentStatus, newStatus)) {
            throw new BadRequestException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }

    private boolean isValidTransition(OrderStatus current, OrderStatus next) {
        if (current == null) {
            return true; // Allow initial status setting
        }

        switch (current) {
            case PENDING:
                // From PENDING can go to APPROVED or REJECTED
                return next == OrderStatus.APPROVED || next == OrderStatus.REJECTED;
            case APPROVED:
                // From APPROVED cannot change status
                return false;
            case REJECTED:
                // From REJECTED cannot change status
                return false;
            default:
                return false;
        }
    }

    public OrderStatus getCurrentStatus() {
        return currentStatus;
    }
} 