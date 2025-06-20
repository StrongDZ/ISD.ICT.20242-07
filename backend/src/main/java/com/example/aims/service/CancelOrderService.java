package com.example.aims.service;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.dto.UsersDTO;
import com.example.aims.dto.order.OrderResponseDTO;
import com.example.aims.dto.transaction.TransactionDto;
import com.example.aims.dto.transaction.TransactionResponseDTO;
import com.example.aims.model.DeliveryInfo;
import com.example.aims.model.Order;
import com.example.aims.model.PaymentTransaction;
import com.example.aims.model.Users;
import com.example.aims.repository.OrderRepository;
import com.example.aims.repository.PaymentTransactionRepository;
import com.example.aims.subsystem.VNPay.VNPaySubsystem;

import jakarta.persistence.Id;
import jakarta.transaction.Transaction;

public class CancelOrderService {
    private PaymentTransactionRepository paymentTransactionRepository;
    private OrderRepository orderRepository;
    private VNPaySubsystem vnpay = new VNPaySubsystem();

    public CancelOrderService(PaymentTransactionRepository paymentTransactionRepository,
            OrderRepository orderRepository) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.orderRepository = orderRepository;
    }

    public String cancelOrder(String orderId, String transactionId) {
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
                UsersDTO customerDto = new UsersDTO(order.getCustomer().getId(),
                        order.getCustomer().getUsername(),
                        order.getCustomer().getPassword(),
                        order.getCustomer().getGmail(),
                        order.getCustomer().getType(), order.getCustomer().getUserStatus());

                DeliveryInfoDTO deliveryInfo = new DeliveryInfoDTO(
                        order.getDeliveryInfo().getRecipientName(),
                        order.getDeliveryInfo().getPhoneNumber(),
                        order.getDeliveryInfo().getAddressDetail(),
                        order.getDeliveryInfo().getDistrict(),
                        order.getDeliveryInfo().getCity(),
                        order.getDeliveryInfo().getMail());
                OrderResponseDTO orderResponse = new OrderResponseDTO(order.getOrderID(), customerDto,
                        order.getCustomerName(), order.getPhoneNumber(),
                        order.getStatus(),
                        order.getShippingAddress(),
                        order.getProvince(),
                        order.getTotalAmount(),
                        deliveryInfo);

                TransactionResponseDTO transaction = new TransactionResponseDTO();
                transaction.setOrder(orderResponse);
                transaction.setTransactionId(paymentTransaction.getTransactionId());
                transaction.setTransactionNo(paymentTransaction.getTransactionNo());
                transaction.setAmount(paymentTransaction.getAmount());
                transaction.setDatetime(paymentTransaction.getDatetime());
                // Call the VNPay subsystem to process the refund
                String refundInfo = vnpay.getRefundInfo(transaction);
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
