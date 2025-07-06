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

@Service
public class PayOrderService {

    private final OrderRepository currentOrder;
    private final PaymentTransactionRepository currentPaymentTransaction;
    private final OrderItemRepository orderItemRepository;
    private final PaymentErrorMapperFactory errorMapperFactory;
    private final EmailService emailService;
    private final PaymentSystemFactory paymentSystemFactory;
    private final OrderMapper orderMapper;

    @Autowired
    public PayOrderService(
            OrderRepository currentOrder,
            PaymentTransactionRepository currentPaymentTransaction,
            OrderItemRepository orderItemRepository,
            PaymentErrorMapperFactory errorMapperFactory,
            EmailService emailService,
            PaymentSystemFactory paymentSystemFactory,
            OrderMapper orderMapper) {
        this.currentOrder = currentOrder;
        this.currentPaymentTransaction = currentPaymentTransaction;
        this.orderItemRepository = orderItemRepository;
        this.errorMapperFactory = errorMapperFactory;
        this.emailService = emailService;
        this.paymentSystemFactory = paymentSystemFactory;
        this.orderMapper = orderMapper;
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

            return "http://localhost:3001/payment-success?orderId=" + orderID;
        } else if (isCancelResponse(responseCode, paymentType)) { // Payment decline/cancel
            return "http://localhost:3001/payment-decline";
        } else { // Payment error
            try {
                IPaymentErrorMapper errorMapper = errorMapperFactory.getMapper(paymentType);
                errorMapper.responseCodeError(responseCode);
            } catch (PaymentException e) {
                System.out.println(e.getMessage());
            }
            return "http://localhost:3001/payment-error";
        }
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
        }
        throw new IllegalArgumentException("Unsupported payment type: " + paymentType);
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
        }
        throw new IllegalArgumentException("Unsupported payment type: " + paymentType);
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
        }
        throw new IllegalArgumentException("Unsupported payment type: " + paymentType);
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
        }
        throw new IllegalArgumentException("Unsupported payment type: " + paymentType);
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