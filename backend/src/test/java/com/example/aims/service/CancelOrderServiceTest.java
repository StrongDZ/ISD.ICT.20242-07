package com.example.aims.service;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.transaction.TransactionResponseDTO;
import com.example.aims.factory.PaymentSystemFactory;
import com.example.aims.mapper.TransactionMapper;
import com.example.aims.model.Order;
import com.example.aims.model.PaymentTransaction;
import com.example.aims.repository.OrderRepository;
import com.example.aims.repository.PaymentTransactionRepository;
import com.example.aims.subsystem.IPaymentSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CancelOrderServiceTest {

    private CancelOrderService cancelOrderService;
    private PaymentTransactionRepository paymentTransactionRepository;
    private OrderRepository orderRepository;
    private TransactionMapper transactionMapper;

    @BeforeEach
    void setUp() {
        paymentTransactionRepository = mock(PaymentTransactionRepository.class);
        orderRepository = mock(OrderRepository.class);
        transactionMapper = mock(TransactionMapper.class);

        cancelOrderService = new CancelOrderService(paymentTransactionRepository, orderRepository);

        // Sử dụng reflection để gán transactionMapper
        try {
            java.lang.reflect.Field field = CancelOrderService.class.getDeclaredField("transactionMapper");
            field.setAccessible(true);
            field.set(cancelOrderService, transactionMapper);
        } catch (Exception e) {
            fail("Failed to set transactionMapper field: " + e.getMessage());
        }
    }

    @Test
    void testCancelOrder_Success() {
        // Arrange
        String orderId = "ORD001";
        String transactionId = "TXN001";
        String paymentType = "vnpay";

        Order order = new Order();
        order.setOrderID(orderId);
        order.setStatus(OrderStatus.PENDING);

        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setTransactionId(transactionId);
        paymentTransaction.setAmount(100.0);
        paymentTransaction.setDatetime(new Date());

        TransactionResponseDTO transactionDTO = new TransactionResponseDTO();
        transactionDTO.setTransactionId(transactionId);
        transactionDTO.setAmount(100.0);

        when(orderRepository.findByOrderID(orderId)).thenReturn(Optional.of(order));
        when(paymentTransactionRepository.findByTransactionId(transactionId))
                .thenReturn(Optional.of(paymentTransaction));
        when(transactionMapper.toTransactionResponseDTO(paymentTransaction)).thenReturn(transactionDTO);

        IPaymentSystem paymentSystem = mock(IPaymentSystem.class);
        when(paymentSystem.getRefundInfo(transactionDTO)).thenReturn("Refund processed successfully");

        try (MockedStatic<PaymentSystemFactory> factoryMock = mockStatic(PaymentSystemFactory.class)) {
            factoryMock.when(() -> PaymentSystemFactory.getPaymentSystem(paymentType)).thenReturn(paymentSystem);

            // Act
            String result = cancelOrderService.cancelOrder(orderId, transactionId, paymentType);

            // Assert
            assertEquals("Refund processed successfully", result);
            assertEquals(OrderStatus.CANCELLED, order.getStatus());
            verify(orderRepository).save(order);
            verify(paymentTransactionRepository).findByTransactionId(transactionId);
            verify(transactionMapper).toTransactionResponseDTO(paymentTransaction);
        }
    }

    @Test
    void testCancelOrder_OrderNotFound() {
        // Arrange
        String orderId = "ORD404";
        String transactionId = "TXN001";
        String paymentType = "vnpay";

        when(orderRepository.findByOrderID(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> cancelOrderService.cancelOrder(orderId, transactionId, paymentType));
        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    void testCancelOrder_OrderAlreadyCancelled() {
        // Arrange
        String orderId = "ORD001";
        String transactionId = "TXN001";
        String paymentType = "vnpay";

        Order order = new Order();
        order.setOrderID(orderId);
        order.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findByOrderID(orderId)).thenReturn(Optional.of(order));

        // Act
        String result = cancelOrderService.cancelOrder(orderId, transactionId, paymentType);

        // Assert
        assertEquals("Order is already cancelled", result);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCancelOrder_OrderApproved() {
        // Arrange
        String orderId = "ORD001";
        String transactionId = "TXN001";
        String paymentType = "vnpay";

        Order order = new Order();
        order.setOrderID(orderId);
        order.setStatus(OrderStatus.APPROVED);

        when(orderRepository.findByOrderID(orderId)).thenReturn(Optional.of(order));

        // Act
        String result = cancelOrderService.cancelOrder(orderId, transactionId, paymentType);

        // Assert
        assertEquals("Order cannot be cancelled after approval or rejection", result);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCancelOrder_OrderRejected() {
        // Arrange
        String orderId = "ORD001";
        String transactionId = "TXN001";
        String paymentType = "vnpay";

        Order order = new Order();
        order.setOrderID(orderId);
        order.setStatus(OrderStatus.REJECTED);

        when(orderRepository.findByOrderID(orderId)).thenReturn(Optional.of(order));

        // Act
        String result = cancelOrderService.cancelOrder(orderId, transactionId, paymentType);

        // Assert
        assertEquals("Order cannot be cancelled after approval or rejection", result);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCancelOrder_PaymentTransactionNotFound() {
        // Arrange
        String orderId = "ORD001";
        String transactionId = "TXN404";
        String paymentType = "vnpay";

        Order order = new Order();
        order.setOrderID(orderId);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findByOrderID(orderId)).thenReturn(Optional.of(order));
        when(paymentTransactionRepository.findByTransactionId(transactionId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> cancelOrderService.cancelOrder(orderId, transactionId, paymentType));
        assertEquals("Payment transaction not found", exception.getMessage());
    }

    @Test
    void testCancelOrder_RefundFailed() {
        // Arrange
        String orderId = "ORD001";
        String transactionId = "TXN001";
        String paymentType = "vnpay";

        Order order = new Order();
        order.setOrderID(orderId);
        order.setStatus(OrderStatus.PENDING);

        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setTransactionId(transactionId);
        paymentTransaction.setAmount(100.0);
        paymentTransaction.setDatetime(new Date());

        TransactionResponseDTO transactionDTO = new TransactionResponseDTO();
        transactionDTO.setTransactionId(transactionId);
        transactionDTO.setAmount(100.0);

        when(orderRepository.findByOrderID(orderId)).thenReturn(Optional.of(order));
        when(paymentTransactionRepository.findByTransactionId(transactionId))
                .thenReturn(Optional.of(paymentTransaction));
        when(transactionMapper.toTransactionResponseDTO(paymentTransaction)).thenReturn(transactionDTO);

        IPaymentSystem paymentSystem = mock(IPaymentSystem.class);
        when(paymentSystem.getRefundInfo(transactionDTO)).thenReturn(null);

        try (MockedStatic<PaymentSystemFactory> factoryMock = mockStatic(PaymentSystemFactory.class)) {
            factoryMock.when(() -> PaymentSystemFactory.getPaymentSystem(paymentType)).thenReturn(paymentSystem);

            // Act
            String result = cancelOrderService.cancelOrder(orderId, transactionId, paymentType);

            // Assert
            assertEquals("Failed to process refund", result);
            assertEquals(OrderStatus.CANCELLED, order.getStatus());
            verify(orderRepository).save(order);
        }
    }

    @Test
    void testCancelOrder_InvalidOrderStatus() {
        // Arrange
        String orderId = "ORD001";
        String transactionId = "TXN001";
        String paymentType = "vnpay";

        Order order = new Order();
        order.setOrderID(orderId);
        // Sử dụng null status để test trường hợp không xác định
        order.setStatus(null);

        when(orderRepository.findByOrderID(orderId)).thenReturn(Optional.of(order));

        // Act
        String result = cancelOrderService.cancelOrder(orderId, transactionId, paymentType);

        // Assert
        assertEquals("Order cannot be cancelled at this stage", result);
        verify(orderRepository, never()).save(any(Order.class));
    }
}