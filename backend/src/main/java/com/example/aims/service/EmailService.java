package com.example.aims.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.aims.model.Order;
import com.example.aims.model.PaymentTransaction;
import com.example.aims.repository.PaymentTransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL_SERVICE")
public class EmailService {
    private final JavaMailSender mailSender;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void send(String to, String subject, String body) throws Exception {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("AIMS Support <" + fromEmail + ">");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to {} with subject {}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new Exception("Mail error: " + e.getMessage());
        }
    }

    /**
     * Sends payment confirmation email
     * 
     * @param recipientName   Name of the recipient
     * @param recipientEmail  Email address of the recipient
     * @param orderId         Order ID
     * @param transactionId   Transaction ID
     * @param transactionLink Link to view transaction details
     * @throws Exception if email sending fails
     */
    public void sendPaymentConfirmation(String recipientName, String recipientEmail,
            String orderId, String transactionId, String transactionLink) throws Exception {
        String subject = "Payment Successful for Order ID: " + orderId;
        String body = buildPaymentConfirmationBody(recipientName, orderId, transactionId, transactionLink);

        send(recipientEmail, subject, body);
    }

    /**
     * Sends payment confirmation email by transaction ID
     * 
     * @param transactionId The ID of the payment transaction
     * @throws Exception if email sending fails
     */
    public void sendPaymentConfirmationEmail(String transactionId) throws Exception {
        PaymentTransaction paymentTransaction = paymentTransactionRepository
                .findByTransactionId(transactionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment transaction not found for transaction Id: " + transactionId));

        Order order = paymentTransaction.getOrder();
        String orderID = order.getOrderID();
        String recipientName = order.getDeliveryInfo().getRecipientName();
        String recipientEmail = order.getDeliveryInfo().getMail();
        String transactionLink = "localhost:3001/payment-history?orderId=" + orderID +
                "&transactionId=" + transactionId +
                "&paymentType=" + paymentTransaction.getPaymentType();

        sendPaymentConfirmation(recipientName, recipientEmail, orderID, transactionId, transactionLink);
    }

    /**
     * Builds the payment confirmation email body
     */
    private String buildPaymentConfirmationBody(String recipientName, String orderId,
            String transactionId, String transactionLink) {
        return String.format(
                "Dear %s,\n\n" +
                        "Your payment for Order ID: %s has been successfully processed.\n" +
                        "Transaction ID: %s\n" +
                        "You can view your transaction details at: %s\n\n" +
                        "Thank you for your purchase!\n\n" +
                        "Best regards,\nAIMS Team",
                recipientName, orderId, transactionId, transactionLink);
    }
}
