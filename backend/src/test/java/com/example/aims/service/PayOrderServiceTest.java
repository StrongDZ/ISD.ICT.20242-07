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
        testOrder.setid("ORD001");
        testOrder.setstatus("PENDING");
        testOrder.settotalAmount(100.0);
        payOrderService.setCurrentOrderForTest(testOrder);
    }

    @Test
    void processPayment_success() {
        String orderId = "ORD001";
        String paymentMethod = "Credit Card";
        String paymentDetails = "Visa ****-****-****-1234";

        PaymentTransaction paymentTransaction = payOrderService.processPayment(orderId, paymentMethod, paymentDetails);

        assertNotNull(paymentTransaction);
        assertEquals("CONFIRMED", testOrder.getstatus());
        assertTrue(paymentTransaction.getcontent().contains("Payment successful"));
        assertEquals(testOrder, paymentTransaction.getOrder());
        assertEquals(paymentMethod, paymentTransaction.getcontent().split("Method: ")[1].split("\\. Details:")[0]);
        assertEquals(paymentDetails, paymentTransaction.getcontent().split("Details: ")[1]);
        assertNotNull(paymentTransaction.getdatetime());
    }

    @Test
    void getPaymentTransactionByOrderId_found() {
        String orderId = "ORD001";
        payOrderService.processPayment(orderId, "Cash", "Paid in hand");
        PaymentTransaction paymentTransaction = payOrderService.getPaymentTransactionByOrderId(orderId);

        assertNotNull(paymentTransaction);
        assertTrue(paymentTransaction.getcontent().contains("successful") || paymentTransaction.getcontent().contains("failed"));
        assertEquals(testOrder, paymentTransaction.getOrder());
        assertNotNull(paymentTransaction.getdatetime());
    }

    @Test
    void getPaymentTransactionByOrderId_notFound() {
        PaymentTransaction paymentTransaction = payOrderService.getPaymentTransactionByOrderId("ORD002");
        assertNull(paymentTransaction);
    }

    @Test
    void processPayment_orderNotFound() {
        String orderId = "ORD002";
        String paymentMethod = "Credit Card";
        String paymentDetails = "Visa ****-****-****-1234";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            payOrderService.processPayment(orderId, paymentMethod, paymentDetails);
        });

        assertEquals("Order not found with ID: ORD002", exception.getMessage());
    }

    @Test
    void processPayment_orderNotInPendingState() {
        testOrder.setstatus("APPROVED");
        String orderId = "ORD001";
        String paymentMethod = "Credit Card";
        String paymentDetails = "Visa ****-****-****-1234";

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            payOrderService.processPayment(orderId, paymentMethod, paymentDetails);
        });

        assertEquals("Order is not in PENDING state for payment. Current status: APPROVED", exception.getMessage());
    }
}