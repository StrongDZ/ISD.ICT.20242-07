package com.example.aims.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aims.common.OrderStatus;
import com.example.aims.model.Order;
import com.example.aims.repository.OrderRepository;

@Service
public class CancelOrderService {
    @Autowired
    private OrderRepository orderRepository;

    public CancelOrderService(OrderRepository orderRepository) {
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
    public OrderStatus cancelOrder(String orderId, String transactionId, String paymentType) {
        Order order = orderRepository.findByOrderID(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return order.getStatus();
        } else if (order.getStatus() == OrderStatus.APPROVED || order.getStatus() == OrderStatus.REJECTED) {
            return order.getStatus();
        } else if (order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            // If the order has a payment transaction, refund it
            return OrderStatus.CANCELLED;
        }
        throw new RuntimeException("Order cannot be cancelled at this stage");
    }
}
