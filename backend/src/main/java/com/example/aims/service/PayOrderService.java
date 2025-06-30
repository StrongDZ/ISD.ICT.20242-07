package com.example.aims.service;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.order.PaymentOrderResponseFromReturnDTO;
import com.example.aims.dto.order.PaymentOrderRequestDTO;
import com.example.aims.dto.order.OrderInfoDTO;
import com.example.aims.dto.order.OrderItemDTO;
import com.example.aims.dto.transaction.TransactionResponseDTO;
import com.example.aims.exception.PaymentException.PaymentException;
import com.example.aims.mapper.OrderMapper;
import com.example.aims.mapper.PaymentError.IPaymentErrorMapper;
import com.example.aims.model.Order;
import com.example.aims.model.OrderItem;
import com.example.aims.model.PaymentTransaction;
import com.example.aims.repository.OrderItemRepository;
import com.example.aims.repository.OrderRepository;
import com.example.aims.repository.PaymentTransactionRepository;
import com.example.aims.factory.PaymentErrorMapperFactory;
import com.example.aims.factory.PaymentSystemFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
    private final OrderItemRepository orderItemRepository;
    @Autowired
    private PaymentErrorMapperFactory errorMapperFactory;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PaymentSystemFactory paymentSystemFactory;

    public PayOrderService(OrderRepository orderRepository, PaymentTransactionRepository paymentTransactionRepository,
            OrderItemRepository orderItemRepository) {
        this.currentOrder = orderRepository;
        this.currentPaymentTransaction = paymentTransactionRepository;
        this.orderItemRepository = orderItemRepository;
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
        return paymentSystemFactory.getPaymentSystem(paymentType).getPaymentUrl(dto);
    }

    /**
     * Processes the payment response from payment gateway.
     * 
     * @param allRequestParams The parameters received from the payment gateway.
     * @param paymentType      The type of payment gateway (vnpay, momo, etc.)
     * @return A redirect URL based on the payment response.
     */
    public String processPayment(Map<String, String> allRequestParams, String paymentType) {
        // Get response code based on payment type
        String responseCode = getResponseCode(allRequestParams, paymentType);
        String orderID = getOrderId(allRequestParams, paymentType);

        if (isSuccessResponse(responseCode, paymentType)) {
            Order order = currentOrder.findByOrderID(orderID)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderID));
            PaymentOrderResponseFromReturnDTO orderDto = orderMapper.toPaymentOrderResponseFromReturnDTO(order);
            PaymentTransaction paymentTransaction = paymentSystemFactory.getPaymentSystem(paymentType)
                    .getTransactionInfo(allRequestParams, orderDto);
            PaymentTransaction savedTransaction = currentPaymentTransaction.save(paymentTransaction);
            String transactionId = savedTransaction.getTransactionId();
            order.setStatus(OrderStatus.PENDING);
            currentOrder.save(order);

            try {
                emailService.sendPaymentConfirmationEmail(transactionId);
            } catch (Exception e) {
                // Log error but don't throw to avoid breaking payment flow
                System.err.println("Failed to send payment confirmation email: " + e.getMessage());
            }

            return "http://localhost:3000/payment-success?orderId=" + orderID;
        } else if (isCancelResponse(responseCode, paymentType)) { // Payment decline/cancel
            return "http://localhost:3000/payment-decline";
        } else { // Payment error
            try {
                IPaymentErrorMapper errorMapper = errorMapperFactory.getMapper(paymentType);
                errorMapper.responseCodeError(responseCode);
            } catch (PaymentException e) {
                System.out.println(e.getMessage());
            }
            return "http://localhost:3000/payment-error";
        }
    }

    /**
     * Processes the payment return from payment gateway by looking up paymentType
     * from database.
     * This method is used when paymentType is not available in the callback
     * parameters.
     * 
     * @param allRequestParams The parameters received from the payment gateway.
     * @param orderId          The order ID to look up paymentType from database.
     * @return A redirect URL based on the payment response.
     */
    public String processPaymentReturn(Map<String, String> allRequestParams, String orderId) {
        // Try to find existing payment transaction to get paymentType
        PaymentTransaction existingTransaction = currentPaymentTransaction.findByTransactionId(orderId).orElse(null);
        String paymentType;

        if (existingTransaction != null && existingTransaction.getPaymentType() != null) {
            // Use paymentType from existing transaction
            paymentType = existingTransaction.getPaymentType().toLowerCase();
        } else {
            // Fallback: try to determine paymentType from response parameters
            // This is a fallback mechanism in case transaction doesn't exist yet
            if (allRequestParams.containsKey("vnp_ResponseCode")) {
                paymentType = "vnpay"; // Default to VNPay if VNPay parameters are present
            } else {
                paymentType = "momo"; // Default to MoMo otherwise
            }
        }

        return processPayment(allRequestParams, paymentType);
    }

    /**
     * Gets the response code from payment gateway parameters.
     */
    private String getResponseCode(Map<String, String> params, String paymentType) {
        switch (paymentType.toLowerCase()) {
            case "vnpay":
                return params.get("vnp_ResponseCode");
            case "momo":
                return params.get("vnp_ResponseCode");
            default:
                return params.get("vnp_ResponseCode");
        }
    }

    /**
     * Gets the order ID from payment gateway parameters.
     */
    private String getOrderId(Map<String, String> params, String paymentType) {
        switch (paymentType.toLowerCase()) {
            case "vnpay":
                return params.get("vnp_TxnRef");
            case "momo":
                return params.get("vnp_TxnRef");
            default:
                return params.get("vnp_TxnRef");
        }
    }

    /**
     * Checks if the response indicates success.
     */
    private boolean isSuccessResponse(String responseCode, String paymentType) {
        switch (paymentType.toLowerCase()) {
            case "vnpay":
                return "00".equals(responseCode);
            case "momo":
                return "00".equals(responseCode);
            default:
                return "00".equals(responseCode);
        }
    }

    /**
     * Checks if the response indicates cancellation.
     */
    private boolean isCancelResponse(String responseCode, String paymentType) {
        switch (paymentType.toLowerCase()) {
            case "vnpay":
                return "24".equals(responseCode);
            case "momo":
                return "24".equals(responseCode);
            default:
                return "24".equals(responseCode);
        }
    }

    /**
     * Retrieves the payment history for a given order ID.
     * 
     * @param orderId The ID of the order for which to retrieve the payment history.
     * @return A TransactionDto containing the payment transaction details.
     * @throws IllegalArgumentException if the payment transaction is not found.
     */
    public TransactionResponseDTO getPaymentHistory(String orderId) {
        PaymentTransaction paymentTransaction = currentPaymentTransaction.findByTransactionId(orderId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Payment transaction not found for order ID: " + orderId));
        TransactionResponseDTO transactionDto = new TransactionResponseDTO();
        transactionDto.setTransactionNo(paymentTransaction.getTransactionNo());
        transactionDto.setTransactionId(paymentTransaction.getTransactionId());
        transactionDto.setDatetime(paymentTransaction.getDatetime());
        transactionDto.setAmount(paymentTransaction.getAmount());
        transactionDto.setPaymentType(paymentTransaction.getPaymentType());
        Order order = paymentTransaction.getOrder();
        PaymentOrderResponseFromReturnDTO orderDto = orderMapper.toPaymentOrderResponseFromReturnDTO(order);
        transactionDto.setOrder(orderDto);
        return transactionDto;
    }

    /**
     * Gets detailed information about an order including delivery info and status.
     * 
     * @param orderId The ID of the order to retrieve information for.
     * @return The Order object with all its details.
     * @throws IllegalArgumentException if the order is not found.
     */
    public OrderInfoDTO getOrderInfo(String orderId) {
        Order order = currentOrder.findByOrderID(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        return orderMapper.toOrderInfoDTO(order);
    }

    /**
     * Gets all products in a specific order with their quantities.
     * 
     * @param orderId The ID of the order to get products for.
     * @return List of OrderItem objects containing product details and quantities.
     * @throws IllegalArgumentException if the order is not found.
     */
    public List<OrderItemDTO> getOrderProduct(String orderId) {
        OrderInfoDTO orderdto = getOrderInfo(orderId);
        Order order = orderMapper.toOrder(orderdto);
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        List<OrderItemDTO> orderItemDtos = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            OrderItemDTO orderItemDto = new OrderItemDTO();
            orderItemDto.setProductID(orderItem.getProduct().getProductID());
            orderItemDto.setProductPrice(orderItem.getProduct().getPrice());
            orderItemDto.setProductTitle(orderItem.getProduct().getTitle());
            orderItemDto.setQuantity(orderItem.getQuantity());
            orderItemDtos.add(orderItemDto);
        }
        return orderItemDtos;
    }

}