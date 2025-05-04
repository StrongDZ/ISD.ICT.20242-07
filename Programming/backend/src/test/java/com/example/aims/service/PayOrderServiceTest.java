package com.example.aims.service;

import com.example.aims.model.Order;
import com.example.aims.model.PaymentTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PayOrderServiceTest {

    private PayOrderService payOrderService;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        payOrderService = new PayOrderService();
        testOrder = new Order();
        testOrder.setId("ORD001");
        testOrder.setStatus("PENDING");
        testOrder.setTotalAmount(100.0);
        payOrderService.setCurrentOrderForTest(testOrder);
    }

    @Test
    void processPayment_success() {
        String orderId = "ORD001";
        String content = "Visa ****-****-****-1234";

        PaymentTransaction paymentTransaction = payOrderService.processPayment(orderId, content);

        assertNotNull(paymentTransaction);
        assertEquals("CONFIRMED", testOrder.getStatus());
        assertEquals(testOrder, paymentTransaction.getOrder());
        assertEquals(content, paymentTransaction.getContent());
        assertNotNull(paymentTransaction.getDatetime());
    }

    @Test
    void getPaymentTransactionByOrderId_found() {
        String orderId = "ORD001";
        //payOrderService.processPayment(orderId, "Cash");
        PaymentTransaction paymentTransaction = payOrderService.getPaymentTransactionByOrderId(orderId);

        assertNotNull(paymentTransaction);
        assertEquals(testOrder, paymentTransaction.getOrder());
        assertNotNull(paymentTransaction.getDatetime());
    }

    @Test
    void getPaymentTransactionByOrderId_notFound() {
        PaymentTransaction paymentTransaction = payOrderService.getPaymentTransactionByOrderId("ORD002");
        assertNull(paymentTransaction);
    }

    @Test
    void processPayment_orderNotFound() {
        String orderId = "ORD002";
        String content = "Visa ****-****-****-1234";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            payOrderService.processPayment(orderId, content);
        });

        assertEquals("Order not found with ID: ORD002", exception.getMessage());
    }

    @Test
    void processPayment_orderNotInPendingState() {
        testOrder.setStatus("APPROVED");
        String orderId = "ORD001";
        String content = "Visa ****-****-****-1234";

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            payOrderService.processPayment(orderId, content);
        });

        assertEquals("Order is not in PENDING state for payment. Current status: APPROVED", exception.getMessage());
    }
}