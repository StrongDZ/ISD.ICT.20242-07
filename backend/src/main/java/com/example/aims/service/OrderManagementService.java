package com.example.aims.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.order.OrderApprovalRequestDTO;
import com.example.aims.dto.order.OrderRejectionRequestDTO;
import com.example.aims.dto.order.OrderManagementResponseDTO;
import com.example.aims.exception.BadRequestException;
import com.example.aims.mapper.OrderMapper;
import com.example.aims.model.Order;
import com.example.aims.repository.OrderRepository;

import java.util.Arrays;
import java.util.List;

/**
 * ✅ SOLID Evaluation for OrderManagementService:
 * 
 * - S (SRP): Single responsibility - handles only order approval/rejection business logic
 * - O (OCP): Open for extension, closed for modification - can be extended without changing existing code
 * - L (LSP): Implements IOrderManagementService interface correctly, substitutable
 * - I (ISP): Depends only on necessary interfaces (OrderRepository)
 * - D (DIP): Depends on abstractions (IOrderManagementService interface, JPA repository interface)
 * 
 * This service follows clean architecture principles and separates business logic
 * from data access and presentation layers.
 */
@Service
public class OrderManagementService implements IOrderManagementService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    
    // List of statuses that are eligible for management operations
    private static final List<OrderStatus> MANAGEABLE_STATUSES = Arrays.asList(
        OrderStatus.PENDING
    );

    @Autowired
    public OrderManagementService(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public OrderManagementResponseDTO approveOrder(OrderApprovalRequestDTO approvalRequest) {
        Order order = orderRepository.findById(approvalRequest.getOrderId())
                .orElseThrow(() -> new BadRequestException("Order not found"));

        order.approveOrder();
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toManagementResponseDTO(savedOrder);
    }

    @Override
    @Transactional
    public OrderManagementResponseDTO rejectOrder(OrderRejectionRequestDTO rejectionRequest) {
        Order order = orderRepository.findById(rejectionRequest.getOrderId())
                .orElseThrow(() -> new BadRequestException("Order not found"));

        order.rejectOrder(rejectionRequest.getReason());
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toManagementResponseDTO(savedOrder);
    }

    @Override
    public boolean isOrderEligibleForManagement(String orderId) {
        try {
            if (orderId == null || orderId.trim().isEmpty()) {
                return false;
            }

            Order order = orderRepository.findByOrderID(orderId).orElse(null);
            if (order == null) {
                return false;
            }

            return MANAGEABLE_STATUSES.contains(order.getStatus());
            
        } catch (Exception e) {
            // Log error in production
            return false;
        }
    }
} 