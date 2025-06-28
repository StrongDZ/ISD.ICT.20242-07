package com.example.aims.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.aims.model.DeliveryInfo;
import com.example.aims.model.Order;
import com.example.aims.model.PaymentTransaction;
import com.example.aims.repository.PaymentTransactionRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

class EmailServiceTest {

    private EmailService emailService;
    private JavaMailSender mailSender;
    private PaymentTransactionRepository paymentTransactionRepository;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        paymentTransactionRepository = mock(PaymentTransactionRepository.class);
        emailService = new EmailService(mailSender, paymentTransactionRepository);
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@example.com");
    }

    @Test
    void testSend_Success() throws Exception {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        // Act
        emailService.send(to, subject, body);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertEquals("AIMS Support <test@example.com>", capturedMessage.getFrom());
        assertEquals(to, capturedMessage.getTo()[0]);
        assertEquals(subject, capturedMessage.getSubject());
        assertEquals(body, capturedMessage.getText());
    }

    @Test
    void testSend_Exception() {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> emailService.send(to, subject, body));
        assertTrue(exception.getMessage().contains("Mail error"));
    }

    @Test
    void testSendPaymentConfirmation_Success() throws Exception {
        // Arrange
        String recipientName = "John Doe";
        String recipientEmail = "john@example.com";
        String orderId = "ORD001";
        String transactionId = "TXN001";
        String transactionLink = "http://localhost:3001/payment-history?orderId=ORD001";

        // Act
        emailService.sendPaymentConfirmation(recipientName, recipientEmail, orderId, transactionId, transactionLink);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertEquals("AIMS Support <test@example.com>", capturedMessage.getFrom());
        assertEquals(recipientEmail, capturedMessage.getTo()[0]);
        assertEquals("Payment Successful for Order ID: ORD001", capturedMessage.getSubject());

        String expectedBody = String.format(
                "Dear %s,\n\n" +
                        "Your payment for Order ID: %s has been successfully processed.\n" +
                        "Transaction ID: %s\n" +
                        "You can view your transaction details at: %s\n\n" +
                        "Thank you for your purchase!\n\n" +
                        "Best regards,\nAIMS Team",
                recipientName, orderId, transactionId, transactionLink);
        assertEquals(expectedBody, capturedMessage.getText());
    }

    @Test
    void testSendPaymentConfirmation_Exception() {
        // Arrange
        String recipientName = "John Doe";
        String recipientEmail = "john@example.com";
        String orderId = "ORD001";
        String transactionId = "TXN001";
        String transactionLink = "http://localhost:3001/payment-history?orderId=ORD001";

        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
                () -> emailService.sendPaymentConfirmation(recipientName, recipientEmail, orderId, transactionId,
                        transactionLink));
        assertTrue(exception.getMessage().contains("Mail error"));
    }

    @Test
    void testSendPaymentConfirmationEmail_Success() throws Exception {
        // Arrange
        String transactionId = "TXN001";
        String orderId = "ORD001";
        String recipientName = "John Doe";
        String recipientEmail = "john@example.com";
        String paymentType = "vnpay";

        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setTransactionId(transactionId);
        paymentTransaction.setPaymentType(paymentType);

        Order order = new Order();
        order.setOrderID(orderId);

        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.setRecipientName(recipientName);
        deliveryInfo.setMail(recipientEmail);
        order.setDeliveryInfo(deliveryInfo);

        paymentTransaction.setOrder(order);

        when(paymentTransactionRepository.findByTransactionId(transactionId))
                .thenReturn(Optional.of(paymentTransaction));

        // Act
        emailService.sendPaymentConfirmationEmail(transactionId);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertEquals("AIMS Support <test@example.com>", capturedMessage.getFrom());
        assertEquals(recipientEmail, capturedMessage.getTo()[0]);
        assertEquals("Payment Successful for Order ID: ORD001", capturedMessage.getSubject());

        String expectedTransactionLink = "localhost:3001/payment-history?orderId=" + orderId +
                "&transactionId=" + transactionId +
                "&paymentType=" + paymentType;

        String expectedBody = String.format(
                "Dear %s,\n\n" +
                        "Your payment for Order ID: %s has been successfully processed.\n" +
                        "Transaction ID: %s\n" +
                        "You can view your transaction details at: %s\n\n" +
                        "Thank you for your purchase!\n\n" +
                        "Best regards,\nAIMS Team",
                recipientName, orderId, transactionId, expectedTransactionLink);
        assertEquals(expectedBody, capturedMessage.getText());
    }

    @Test
    void testSendPaymentConfirmationEmail_TransactionNotFound() {
        // Arrange
        String transactionId = "TXN404";
        when(paymentTransactionRepository.findByTransactionId(transactionId))
                .thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> emailService.sendPaymentConfirmationEmail(transactionId));
        assertEquals("Payment transaction not found for transaction Id: TXN404", exception.getMessage());
    }

    @Test
    void testSendPaymentConfirmationEmail_MailException() throws Exception {
        // Arrange
        String transactionId = "TXN001";
        String orderId = "ORD001";
        String recipientName = "John Doe";
        String recipientEmail = "john@example.com";
        String paymentType = "vnpay";

        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setTransactionId(transactionId);
        paymentTransaction.setPaymentType(paymentType);

        Order order = new Order();
        order.setOrderID(orderId);

        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.setRecipientName(recipientName);
        deliveryInfo.setMail(recipientEmail);
        order.setDeliveryInfo(deliveryInfo);

        paymentTransaction.setOrder(order);

        when(paymentTransactionRepository.findByTransactionId(transactionId))
                .thenReturn(Optional.of(paymentTransaction));
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
                () -> emailService.sendPaymentConfirmationEmail(transactionId));
        assertTrue(exception.getMessage().contains("Mail error"));
    }
}