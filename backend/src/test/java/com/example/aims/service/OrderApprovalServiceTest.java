package com.example.aims.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.order.OrderApprovalRequestDTO;
import com.example.aims.dto.order.OrderManagementResponseDTO;
import com.example.aims.dto.order.OrderRejectionRequestDTO;
import com.example.aims.model.Book;
import com.example.aims.model.Order;
import com.example.aims.model.DeliveryInfo;
import com.example.aims.repository.OrderRepository;
import com.example.aims.repository.ProductRepository;

class OrderApprovalServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderManagementService orderManagementService;

    private Book mockProduct;
    private Order mockPendingOrder;
    private DeliveryInfo mockDeliveryInfo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mock product
        mockProduct = new Book();
        mockProduct.setProductID("book123");
        mockProduct.setTitle("Test Book");
        mockProduct.setPrice(100.0);
        mockProduct.setQuantity(50);

        // Setup mock delivery info
        mockDeliveryInfo = new DeliveryInfo();

        // Setup mock pending order
        mockPendingOrder = new Order();
        mockPendingOrder.setOrderID("order123");
        mockPendingOrder.setStatus(OrderStatus.PENDING);
        mockPendingOrder.setTotalAmount(59.98); // 29.99 * 2
        mockPendingOrder.setDeliveryInfo(mockDeliveryInfo);

        when(orderRepository.findByOrderID("order123")).thenReturn(Optional.of(mockPendingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(mockPendingOrder);
        when(productRepository.findById("book123")).thenReturn(Optional.of(mockProduct));
        when(productRepository.save(any(Book.class))).thenReturn(mockProduct);
    }

    @Test
    void approveOrder_ValidPendingOrder_ShouldSucceed() {
        // Arrange
        OrderApprovalRequestDTO request = new OrderApprovalRequestDTO();
        request.setOrderId("order123");
        request.setApprovedBy("manager1");
        request.setNotes("Approved");

        // Act
        OrderManagementResponseDTO response = orderManagementService.approveOrder(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(OrderStatus.APPROVED, response.getNewStatus());
        assertTrue(response.getMessage().contains("Order approved successfully by manager1"));
        
        // Verify order was updated
        verify(orderRepository).save(mockPendingOrder);
        assertEquals(OrderStatus.APPROVED, mockPendingOrder.getStatus());
    }

    @Test
    void approveOrder_MissingDeliveryInfo_ShouldFail() {
        // Arrange
        OrderApprovalRequestDTO request = new OrderApprovalRequestDTO();
        request.setOrderId("order123");
        request.setApprovedBy("manager1");

        // Remove delivery info
        mockPendingOrder.setDeliveryInfo(null);

        // Act
        OrderManagementResponseDTO response = orderManagementService.approveOrder(request);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getNewStatus());
        assertTrue(response.getMessage().contains("missing delivery information"));
        
        // Verify no updates were made
        verify(orderRepository, never()).save(any(Order.class));
        verify(productRepository, never()).save(any(Book.class));
    }

    @Test
    void rejectOrder_ValidPendingOrder_ShouldSucceed() {
        // Arrange
        OrderRejectionRequestDTO request = new OrderRejectionRequestDTO();
        request.setOrderId("order123");
        request.setRejectedBy("manager1");
        request.setReason("Out of stock");
        request.setNotes("Items not available");

        // Act
        OrderManagementResponseDTO response = orderManagementService.rejectOrder(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(OrderStatus.REJECTED, response.getNewStatus());
        assertTrue(response.getMessage().contains("Order rejected successfully by manager1"));
        assertTrue(response.getMessage().contains("Out of stock"));
        
        // Verify order was updated
        verify(orderRepository).save(mockPendingOrder);
        assertEquals(OrderStatus.REJECTED, mockPendingOrder.getStatus());
        assertEquals("Out of stock", mockPendingOrder.getRejectedReason());
        
        // Verify no product updates
        verify(productRepository, never()).save(any(Book.class));
    }

    @Test
    void rejectOrder_MissingReason_ShouldFail() {
        // Arrange
        OrderRejectionRequestDTO request = new OrderRejectionRequestDTO();
        request.setOrderId("order123");
        request.setRejectedBy("manager1");
        // Missing rejection reason

        // Act
        OrderManagementResponseDTO response = orderManagementService.rejectOrder(request);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getNewStatus());
        assertTrue(response.getMessage().contains("Rejection reason is required"));
        
        // Verify no updates were made
        verify(orderRepository, never()).save(any(Order.class));
        verify(productRepository, never()).save(any(Book.class));
    }

    @Test
    void approveOrder_NonPendingOrder_ShouldFail() {
        // Arrange
        OrderApprovalRequestDTO request = new OrderApprovalRequestDTO();
        request.setOrderId("order123");
        request.setApprovedBy("manager1");

        // Set order to non-pending status
        mockPendingOrder.setStatus(OrderStatus.APPROVED);

        // Act
        OrderManagementResponseDTO response = orderManagementService.approveOrder(request);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getNewStatus());
        assertTrue(response.getMessage().contains("Current status: " + OrderStatus.APPROVED));
        
        // Verify no updates were made
        verify(orderRepository, never()).save(any(Order.class));
        verify(productRepository, never()).save(any(Book.class));
    }

    @Test
    void rejectOrder_NonPendingOrder_ShouldFail() {
        // Arrange
        OrderRejectionRequestDTO request = new OrderRejectionRequestDTO();
        request.setOrderId("order123");
        request.setRejectedBy("manager1");
        request.setReason("Out of stock");

        // Set order to non-pending status
        mockPendingOrder.setStatus(OrderStatus.APPROVED);

        // Act
        OrderManagementResponseDTO response = orderManagementService.rejectOrder(request);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getNewStatus());
        assertTrue(response.getMessage().contains("Current status: " + OrderStatus.APPROVED));
        
        // Verify no updates were made
        verify(orderRepository, never()).save(any(Order.class));
        verify(productRepository, never()).save(any(Book.class));
    }

    @Test
    void approveOrder_OrderNotFound_ShouldFail() {
        // Arrange
        OrderApprovalRequestDTO request = new OrderApprovalRequestDTO();
        request.setOrderId("nonexistent");
        request.setApprovedBy("manager1");

        when(orderRepository.findByOrderID("nonexistent")).thenReturn(Optional.empty());

        // Act
        OrderManagementResponseDTO response = orderManagementService.approveOrder(request);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getNewStatus());
        assertTrue(response.getMessage().contains("Order not found"));
        
        // Verify no updates were made
        verify(orderRepository, never()).save(any(Order.class));
        verify(productRepository, never()).save(any(Book.class));
    }

    @Test
    void rejectOrder_OrderNotFound_ShouldFail() {
        // Arrange
        OrderRejectionRequestDTO request = new OrderRejectionRequestDTO();
        request.setOrderId("nonexistent");
        request.setRejectedBy("manager1");
        request.setReason("Out of stock");

        when(orderRepository.findByOrderID("nonexistent")).thenReturn(Optional.empty());

        // Act
        OrderManagementResponseDTO response = orderManagementService.rejectOrder(request);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getNewStatus());
        assertTrue(response.getMessage().contains("Order not found"));
        
        // Verify no updates were made
        verify(orderRepository, never()).save(any(Order.class));
        verify(productRepository, never()).save(any(Book.class));
    }
} 