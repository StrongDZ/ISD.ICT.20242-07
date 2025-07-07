package com.example.aims.service;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.transaction.TransactionResponseDTO;
import com.example.aims.factory.PaymentSystemFactory;
import com.example.aims.mapper.TransactionMapper;
import com.example.aims.model.Order;
import com.example.aims.model.PaymentTransaction;
import com.example.aims.repository.OrderRepository;
import com.example.aims.repository.PaymentTransactionRepository;
import com.example.aims.subsystem.VNPay.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CancelOrderServiceTest {

    private OrderRepository orderRepository;
    private CancelOrderService cancelOrderService;
    private PaymentTransactionRepository paymentTransactionRepository;
    private TransactionMapper tr;
    private PaymentSystemFactory paymentSystemFactory;
    private VNPay paymentSystem;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        paymentTransactionRepository = mock(PaymentTransactionRepository.class);
        tr = mock(TransactionMapper.class);
        paymentSystemFactory = mock(PaymentSystemFactory.class);
        paymentSystem = mock(VNPay.class);

        cancelOrderService = new CancelOrderService(
                orderRepository,
                paymentTransactionRepository,
                tr,
                paymentSystemFactory);
    }

    @Test
    void testCancelOrder_Success() {
        Order order = new Order();
        order.setOrderID("ORD001");
        order.setStatus(OrderStatus.PENDING);

        PaymentTransaction transaction = new PaymentTransaction();
        TransactionResponseDTO dto = new TransactionResponseDTO();

        when(orderRepository.findByOrderID("ORD001")).thenReturn(Optional.of(order));
        when(paymentTransactionRepository.findByTransactionId("TXN001")).thenReturn(Optional.of(transaction));
        when(tr.toTransactionResponseDTO(transaction)).thenReturn(dto);
        when(paymentSystemFactory.getPaymentSystem("vnpay")).thenReturn(paymentSystem);
        when(paymentSystem.getRefundInfo(dto)).thenReturn("REFUNDED");

        OrderStatus result = cancelOrderService.cancelOrder("ORD001", "TXN001", "vnpay");

        assertEquals(OrderStatus.CANCELLED, result);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void testCancelOrder_AlreadyCancelled() {
        Order order = new Order();
        order.setOrderID("ORD002");
        order.setStatus(OrderStatus.CANCELLED);

        PaymentTransaction transaction = new PaymentTransaction();
        TransactionResponseDTO dto = new TransactionResponseDTO();

        when(orderRepository.findByOrderID("ORD002")).thenReturn(Optional.of(order));
        when(paymentTransactionRepository.findByTransactionId("TXN001")).thenReturn(Optional.of(transaction));
        when(tr.toTransactionResponseDTO(transaction)).thenReturn(dto);
        when(paymentSystemFactory.getPaymentSystem("vnpay")).thenReturn(paymentSystem);
        when(paymentSystem.getRefundInfo(dto)).thenReturn("REFUNDED");

        OrderStatus result = cancelOrderService.cancelOrder("ORD002", "TXN001", "vnpay");

        assertEquals(OrderStatus.CANCELLED, result);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testCancelOrder_Approved() {
        Order order = new Order();
        order.setOrderID("ORD003");
        order.setStatus(OrderStatus.APPROVED);

        PaymentTransaction transaction = new PaymentTransaction();
        TransactionResponseDTO dto = new TransactionResponseDTO();

        when(orderRepository.findByOrderID("ORD003")).thenReturn(Optional.of(order));
        when(paymentTransactionRepository.findByTransactionId("TXN001")).thenReturn(Optional.of(transaction));
        when(tr.toTransactionResponseDTO(transaction)).thenReturn(dto);
        when(paymentSystemFactory.getPaymentSystem("vnpay")).thenReturn(paymentSystem);
        when(paymentSystem.getRefundInfo(dto)).thenReturn("REFUNDED");

        OrderStatus result = cancelOrderService.cancelOrder("ORD003", "TXN001", "vnpay");

        assertEquals(OrderStatus.APPROVED, result);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testCancelOrder_Rejected() {
        Order order = new Order();
        order.setOrderID("ORD004");
        order.setStatus(OrderStatus.REJECTED);

        PaymentTransaction transaction = new PaymentTransaction();
        TransactionResponseDTO dto = new TransactionResponseDTO();

        when(orderRepository.findByOrderID("ORD004")).thenReturn(Optional.of(order));
        when(paymentTransactionRepository.findByTransactionId("TXN001")).thenReturn(Optional.of(transaction));
        when(tr.toTransactionResponseDTO(transaction)).thenReturn(dto);
        when(paymentSystemFactory.getPaymentSystem("vnpay")).thenReturn(paymentSystem);
        when(paymentSystem.getRefundInfo(dto)).thenReturn("REFUNDED");

        OrderStatus result = cancelOrderService.cancelOrder("ORD004", "TXN001", "vnpay");

        assertEquals(OrderStatus.REJECTED, result);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testCancelOrder_OrderNotFound() {
        when(orderRepository.findByOrderID("ORD404")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> cancelOrderService.cancelOrder("ORD404", "TXN001", "vnpay"));

        assertEquals("Order not found", ex.getMessage());
    }

    @Test
    void testCancelOrder_TransactionNotFound() {
        Order order = new Order();
        order.setOrderID("ORD006");
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findByOrderID("ORD006")).thenReturn(Optional.of(order));
        when(paymentTransactionRepository.findByTransactionId("TXN404")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> cancelOrderService.cancelOrder("ORD006", "TXN404", "vnpay"));

        assertTrue(ex.getMessage().contains("Payment transaction not found"));
    }

    @Test
    void testCancelOrder_InvalidStatus() {
        Order order = new Order();
        order.setOrderID("ORD007");
        order.setStatus(null); // Invalid

        PaymentTransaction transaction = new PaymentTransaction();
        TransactionResponseDTO dto = new TransactionResponseDTO();

        when(orderRepository.findByOrderID("ORD007")).thenReturn(Optional.of(order));
        when(paymentTransactionRepository.findByTransactionId("TXN007")).thenReturn(Optional.of(transaction));
        when(tr.toTransactionResponseDTO(transaction)).thenReturn(dto);
        when(paymentSystemFactory.getPaymentSystem("vnpay")).thenReturn(paymentSystem);
        when(paymentSystem.getRefundInfo(dto)).thenReturn("REFUNDED");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> cancelOrderService.cancelOrder("ORD007", "TXN007", "vnpay"));

        assertEquals("Order cannot be cancelled at this stage", ex.getMessage());
    }
}
