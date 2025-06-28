package com.example.aims.service;

import com.example.aims.common.OrderStatus;
import com.example.aims.model.Order;
import com.example.aims.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CancelOrderServiceTest {

    private OrderRepository orderRepository;
    private CancelOrderService cancelOrderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        cancelOrderService = new CancelOrderService(orderRepository);
    }

    @Test
    void testCancelOrder_Success() {
        // Arrange
        Order order = new Order();
        order.setOrderID("ORD001");
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findByOrderID("ORD001")).thenReturn(Optional.of(order));

        // Act
        OrderStatus result = cancelOrderService.cancelOrder("ORD001", "TXN001", "vnpay");

        // Assert
        assertEquals(OrderStatus.CANCELLED, result);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void testCancelOrder_AlreadyCancelled() {
        Order order = new Order();
        order.setOrderID("ORD002");
        order.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findByOrderID("ORD002")).thenReturn(Optional.of(order));

        OrderStatus result = cancelOrderService.cancelOrder("ORD002", "TXN001", "vnpay");

        assertEquals(OrderStatus.CANCELLED, result);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCancelOrder_Approved() {
        Order order = new Order();
        order.setOrderID("ORD003");
        order.setStatus(OrderStatus.APPROVED);

        when(orderRepository.findByOrderID("ORD003")).thenReturn(Optional.of(order));

        OrderStatus result = cancelOrderService.cancelOrder("ORD003", "TXN001", "vnpay");

        assertEquals(OrderStatus.APPROVED, result);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCancelOrder_Rejected() {
        Order order = new Order();
        order.setOrderID("ORD004");
        order.setStatus(OrderStatus.REJECTED);

        when(orderRepository.findByOrderID("ORD004")).thenReturn(Optional.of(order));

        OrderStatus result = cancelOrderService.cancelOrder("ORD004", "TXN001", "vnpay");

        assertEquals(OrderStatus.REJECTED, result);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCancelOrder_OrderNotFound() {
        when(orderRepository.findByOrderID("ORD404")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> cancelOrderService.cancelOrder("ORD404", "TXN001", "vnpay"));

        assertEquals("Order not found", ex.getMessage());
    }

    @Test
    void testCancelOrder_InvalidStatus() {
        Order order = new Order();
        order.setOrderID("ORD005");
        order.setStatus(null); // Invalid status

        when(orderRepository.findByOrderID("ORD005")).thenReturn(Optional.of(order));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> cancelOrderService.cancelOrder("ORD005", "TXN001", "vnpay"));

        assertEquals("Order cannot be cancelled at this stage", ex.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }
}
