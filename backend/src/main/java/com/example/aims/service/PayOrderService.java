package com.example.aims.service;

import com.example.aims.model.Order;
import com.example.aims.model.PaymentTransaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class PayOrderService {

    private Order currentOrder; // Biến instance để giữ đơn hàng hiện tại (chỉ dùng cho test)
    private PaymentTransaction currentPaymentTransaction; // Biến instance cho giao dịch thanh toán

    public void setCurrentOrderForTest(Order order) {
        this.currentOrder = order;
    }

    public Optional<Order> findOrderById(String orderId) {
        if (currentOrder != null && currentOrder.getid().equals(orderId)) {
            return Optional.of(currentOrder);
        }
        return Optional.empty();
    }

    public Optional<PaymentTransaction> findPaymentTransactionByOrderId(String orderId) {
        if (currentPaymentTransaction != null && currentPaymentTransaction.getOrder().getid().equals(orderId)) {
            return Optional.of(currentPaymentTransaction);
        }
        return Optional.empty();
    }

    @Transactional
    public PaymentTransaction processPayment(String orderId, String paymentMethod, String paymentDetails) {
        Optional<Order> orderOptional = findOrderById(orderId);
        if (orderOptional.isEmpty()) {
            throw new IllegalArgumentException("Order not found with ID: " + orderId);
        }
        Order order = orderOptional.get();

        if (!"PENDING".equals(order.getstatus())) {
            throw new IllegalStateException("Order is not in PENDING state for payment. Current status: " + order.getStatus());
        }

        // 1. Gọi service/component xử lý thanh toán thực tế (tương tự như trước)
        boolean paymentSuccessful = true; // Tạm thời gán true để demo

        if (paymentSuccessful) {
            // 2. Cập nhật trạng thái đơn hàng thành CONFIRMED
            order.setstatus("CONFIRMED");
            // Không cần "lưu" vào store nữa

            // 3. Tạo bản ghi giao dịch thanh toán
            currentPaymentTransaction = new PaymentTransaction();
            currentPaymentTransaction.setOrder(order); // Thiết lập mối quan hệ với Order
            currentPaymentTransaction.setcontent("Payment successful. Method: " + paymentMethod + ". Details: " + paymentDetails);
            currentPaymentTransaction.setdatetime(new Date());

            return currentPaymentTransaction;
        } else {
            // Xử lý trường hợp thanh toán thất bại
            currentPaymentTransaction = new PaymentTransaction();
            currentPaymentTransaction.setorder(order);
            currentPaymentTransaction.setcontent("Payment failed. Method: " + paymentMethod + ". Details: " + paymentDetails);
            currentPaymentTransaction.setdatetime(new Date());

            throw new RuntimeException("Payment processing failed for order ID: " + orderId);
        }
    }

    public PaymentTransaction getPaymentTransactionByOrderId(String orderId) {
        return findPaymentTransactionByOrderId(orderId).orElse(null);
    }

    // Không có nơi lưu trữ dữ liệu tập trung trong class này
}