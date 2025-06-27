package com.example.aims.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.transaction.TransactionResponseDTO;
import com.example.aims.mapper.TransactionMapper;
import com.example.aims.model.Order;
import com.example.aims.model.PaymentTransaction;
import com.example.aims.repository.OrderRepository;
import com.example.aims.repository.PaymentTransactionRepository;
import com.example.aims.factory.PaymentSystemFactory;

@Service
public class CancelOrderService {
    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TransactionMapper transactionMapper;

    public CancelOrderService(PaymentTransactionRepository paymentTransactionRepository,
            OrderRepository orderRepository) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Cancels an order by its ID and processes the refund if applicable.
     *
     * @param orderId       The ID of the order to cancel.
     * @param transactionId The ID of the payment transaction associated with the
     *                      order.
     * @param paymentType   Loại cổng thanh toán (vnpay, momo, ...)
     * @return A message indicating the result of the cancellation.
     */
    public String cancelOrder(String orderId, String transactionId, String paymentType) {
        Order order = orderRepository.findByOrderID(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return "Order is already cancelled";
        } else if (order.getStatus() == OrderStatus.APPROVED || order.getStatus() == OrderStatus.REJECTED) {
            return "Order cannot be cancelled after approval or rejection";
        } else if (order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            // If the order has a payment transaction, refund it
            PaymentTransaction paymentTransaction = paymentTransactionRepository.findByTransactionId(transactionId)
                    .orElseThrow(() -> new RuntimeException("Payment transaction not found"));
            if (paymentTransaction != null) {
                TransactionResponseDTO transaction = transactionMapper.toTransactionResponseDTO(paymentTransaction);
                String refundInfo = PaymentSystemFactory.getPaymentSystem(paymentType).getRefundInfo(transaction);
                if (refundInfo != null) {
                    return refundInfo;
                } else {
                    return "Failed to process refund";
                }
            } else {
                return "No payment transaction found for this order";
            }
        } else {
            return "Order cannot be cancelled at this stage";
        }
    }
}
