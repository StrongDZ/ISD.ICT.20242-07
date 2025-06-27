package com.example.aims.service;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.order.PaymentOrderResponseFromReturnDTO;
import com.example.aims.dto.order.PaymentOrderRetrievalDTO;
import com.example.aims.dto.order.PaymentOrderRequestDTO;
import com.example.aims.dto.transaction.TransactionRetrievalDTO;
import com.example.aims.exception.PaymentException.PaymentException;
import com.example.aims.mapper.OrderMapper;
import com.example.aims.mapper.VNPayErrorMapper;
import com.example.aims.model.Order;
import com.example.aims.model.PaymentTransaction;
import com.example.aims.repository.OrderRepository;
import com.example.aims.repository.PaymentTransactionRepository;
import com.example.aims.factory.PaymentSystemFactory;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Map;
import java.util.Optional;

// Communicational Cohesion – Methods share common data and purpose (processing payments), 
// but test logic reduces clarity of single-purpose design
// ❌ SRP violated – Class handles both business logic and testing setup
// 🔧 Improvement: move test-related code (currentOrder, currentPaymentTransaction) to a separate test/mock class 
// to make PayOrderService responsible only for real payment processing

// 🔧 Improvement suggestions:
// - Extract test logic (e.g. currentOrder, setCurrentOrderForTest) into a separate mock or test utility class
// - Use proper dependency injection (e.g. repository) to get Order and PaymentTransaction
//   instead of using instance-level test data
// - Keep PayOrderService focused only on real payment logic → move test-specific state out

// ✅ SOLID Evaluation Summary:
// - ✅ SRP violated: test-related logic should be extracted
// - ❌ OCP violated: payment logic is hardcoded, not extendable (suggest strategy pattern)
// - ✅ LSP respected: no inheritance misuse
// - ✅ ISP acceptable now, but keep in mind if adding interfaces later
// - ❌ DIP violated: depends directly on concrete classes; should rely on interfaces

@Service
public class PayOrderService {

    // 🔗 Coupling:
    // Stamp Coupling – This class depends on whole Order and PaymentTransaction
    // objects,
    // even though only specific fields (e.g., order.getId(), order.getStatus()) are
    // used.
    // → Suggestion: In future, pass only necessary fields (e.g., orderId,
    // totalAmount) to reduce coupling to Data level.
    @Autowired
    private final OrderRepository currentOrder;
    @Autowired
    private final PaymentTransactionRepository currentPaymentTransaction;
    @Autowired
    private VNPayErrorMapper errorMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private final JavaMailSender javaMailSender;

    public PayOrderService(OrderRepository orderRepository, PaymentTransactionRepository paymentTransactionRepository,
            JavaMailSender javaMailSender) {
        this.currentOrder = orderRepository;
        this.currentPaymentTransaction = paymentTransactionRepository;
        this.javaMailSender = javaMailSender;
    }

    /**
     * Generates a payment URL for the given order ID.
     * 
     * @param orderId
     * @return The payment URL for the order.
     * @throws IllegalArgumentException if the order is not found.
     */
    public String getPaymentURL(String orderId, String paymentType) {
        Optional<Order> orderOptional = currentOrder.findByOrderID(orderId);
        if (orderOptional.isEmpty()) {
            throw new IllegalArgumentException("Order not found with ID: " + orderId);
        }
        Order order = orderOptional.get();

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException(
                    "Order is not in PENDING state for payment. Current status: " + order.getStatus());
        }
        PaymentOrderRequestDTO dto = orderMapper.toPaymentOrderRequestDTO(order);
        return PaymentSystemFactory.getPaymentSystem(paymentType).getPaymentUrl(dto);
    }

    /**
     * Processes the payment response from VNPay.
     * 
     * @param allRequestParams The parameters received from the payment gateway.
     * @return A redirect URL based on the payment response.
     */
    public String processPayment(Map<String, String> allRequestParams, String paymentType) {
        String responseCode = allRequestParams.get("vnp_ResponseCode");
        if (responseCode.equals("00")) {
            String orderID = allRequestParams.get("vnp_TxnRef");
            Order order = currentOrder.findByOrderID(orderID)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderID));
            PaymentOrderResponseFromReturnDTO orderDto = orderMapper.toPaymentOrderResponseFromReturnDTO(order);
            PaymentTransaction paymentTransaction = PaymentSystemFactory.getPaymentSystem(paymentType)
                    .getTransactionInfo(allRequestParams, orderDto);
            PaymentTransaction savedTransaction = currentPaymentTransaction.save(paymentTransaction);
            String transactionId = savedTransaction.getTransactionId();
            order.setStatus(OrderStatus.PENDING);
            currentOrder.save(order);
            sendMail(transactionId);
            return "http://localhost:3001/payment-success?orderId=" + orderID;
        } else if (responseCode.equals("24")) { // Payment decline
            return "http://localhost:3001/payment-decline";
        } else { // Payment error
            try {
                errorMapper.responseCodeError(responseCode);
            } catch (PaymentException e) {
                System.out.println(e.getMessage());
            }
            return "http://localhost:3001/payment-error";
        }
    }

    /**
     * Sends a confirmation email after a successful payment.
     * 
     * @param transactionId The ID of the payment transaction.
     */
    public void sendMail(String transactionId) {
        PaymentTransaction paymentTransaction = currentPaymentTransaction
                .findByTransactionId(transactionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment transaction not found for transaction Id: " + transactionId));
        Order order = paymentTransaction.getOrder();
        String orderID = order.getOrderID();
        String recvMail = order.getCustomer().getGmail();
        String transactionLink = "localhost:3001/payment-history?orderId=" +
                orderID;
        String subject = "Payment Successful for Order ID: " + orderID;
        String body = "Dear " + order.getDeliveryInfo().getRecipientName() + ",\n\n"
                + "Your payment for Order ID: " + orderID + " has been successfully processed.\n"
                + "Transaction ID: " + transactionId + "\n"
                + "You can view your transaction details at: " + transactionLink + "\n\n"
                + "Thank you for your purchase!\n\n"
                + "Best regards,\nAIMS Team";
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("AIMS Support <itss.aims.07@gmail.com>");
            mailMessage.setTo(recvMail);
            mailMessage.setSubject(subject);
            mailMessage.setText(body);
            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the payment history for a given order ID.
     * 
     * @param orderId The ID of the order for which to retrieve the payment history.
     * @return A TransactionDto containing the payment transaction details.
     * @throws IllegalArgumentException if the payment transaction is not found.
     */
    public TransactionRetrievalDTO getPaymentHistory(String orderId) {
        PaymentTransaction paymentTransaction = currentPaymentTransaction.findByTransactionId(orderId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Payment transaction not found for order ID: " + orderId));
        TransactionRetrievalDTO transactionDto = new TransactionRetrievalDTO();
        transactionDto.setTransactionNo(paymentTransaction.getTransactionNo());
        transactionDto.setTransactionId(paymentTransaction.getTransactionId());
        transactionDto.setDatetime(paymentTransaction.getDatetime());
        transactionDto.setAmount(paymentTransaction.getAmount());
        Order order = paymentTransaction.getOrder();
        PaymentOrderRetrievalDTO orderDto = orderMapper.toPaymentOrderRetrievalDTO(order);
        transactionDto.setOrder(orderDto);
        return transactionDto;
    }

}