package com.example.aims.service;

import com.example.aims.model.Order;
import com.example.aims.model.PaymentTransaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

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

    private Order currentOrder; // Biến instance để giữ đơn hàng hiện tại (chỉ dùng cho test)
    private PaymentTransaction currentPaymentTransaction; // Biến instance cho giao dịch thanh toán

    public void setCurrentOrderForTest(Order order) {
        this.currentOrder = order;
    }

    public Optional<Order> findOrderById(String orderId) {
        if (currentOrder != null && currentOrder.getOrderID().equals(orderId)) {
            return Optional.of(currentOrder);
        }
        return Optional.empty();
    }

    public Optional<PaymentTransaction> findPaymentTransactionByOrderId(String orderId) {
        if (currentPaymentTransaction != null && currentPaymentTransaction.getOrder().getOrderID().equals(orderId)) {
            return Optional.of(currentPaymentTransaction);
        }
        return Optional.empty();
    }

    @Transactional
    public PaymentTransaction processPayment(String orderId, String content) {
        Optional<Order> orderOptional = findOrderById(orderId);
        if (orderOptional.isEmpty()) {
            throw new IllegalArgumentException("Order not found with ID: " + orderId);
        }
        Order order = orderOptional.get();

        if (!"PENDING".equals(order.getStatus())) {
            throw new IllegalStateException(
                    "Order is not in PENDING state for payment. Current status: " + order.getStatus());
        }

        // 1. Gọi service/component xử lý thanh toán thực tế (tương tự như trước)
        boolean paymentSuccessful = true; // Tạm thời gán true để demo

        if (paymentSuccessful) {
            // 2. Cập nhật trạng thái đơn hàng thành CONFIRMED
            order.setStatus("CONFIRMED");
            // Không cần "lưu" vào store nữa

            // 3. Tạo bản ghi giao dịch thanh toán
            currentPaymentTransaction = new PaymentTransaction();
            currentPaymentTransaction.setOrder(order); // Thiết lập mối quan hệ với Order
            currentPaymentTransaction.setContent(content);
            currentPaymentTransaction.setDatetime(new Date());

            return currentPaymentTransaction;
        } else {
            // Xử lý trường hợp thanh toán thất bại
            currentPaymentTransaction = new PaymentTransaction();
            currentPaymentTransaction.setOrder(order);
            currentPaymentTransaction.setContent(content);
            currentPaymentTransaction.setDatetime(new Date());

            throw new RuntimeException("Payment processing failed for order ID: " + orderId);
        }
    }

    public PaymentTransaction getPaymentTransactionByOrderId(String orderId) {
        return findPaymentTransactionByOrderId(orderId).orElse(null);
    }

    // Không có nơi lưu trữ dữ liệu tập trung trong class này
}