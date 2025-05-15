package com.example.aims.service;

import com.example.aims.dto.OrderDTO;
import com.example.aims.dto.TransactionDto;
import com.example.aims.model.Order;
import com.example.aims.model.PaymentTransaction;
import com.example.aims.repository.OrderRepository;
import com.example.aims.repository.PaymentTransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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

@Service
public class PayOrderService {

    // 🔗 Coupling:
    // Stamp Coupling – This class depends on whole Order and PaymentTransaction
    // objects,
    // even though only specific fields (e.g., order.getId(), order.getStatus()) are
    // used.
    // → Suggestion: In future, pass only necessary fields (e.g., orderId,
    // totalAmount) to reduce coupling to Data level.

    private final OrderRepository orderRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final OrderService orderService;

    public PayOrderService(OrderRepository orderRepository,
            PaymentTransactionRepository paymentTransactionRepository, OrderService orderService) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    public OrderDTO findOrderById(String orderId) {

        return orderService.getOrderById(orderId);
    }

    public TransactionDto findPaymentTransactionByOrderId(String orderId) {
        PaymentTransaction paymentTransaction = paymentTransactionRepository
                .findByOrderId(orderId).orElseThrow(() -> new RuntimeException(
                        "Payment transaction not found with order ID: " + orderId));
        return convertToTransactionDTO(paymentTransaction);
    }

    @Transactional
    public TransactionDto processPayment(String orderId, String content) {
        OrderDTO orderDTO = findOrderById(orderId);
        if (orderDTO == null) {
            throw new IllegalArgumentException("Order not found with ID: " + orderId);
        }

        Optional<Order> orderOptional = orderRepository.findById(orderId);
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

        PaymentTransaction paymentTransaction;
        if (paymentSuccessful) {
            // 2. Cập nhật trạng thái đơn hàng thành CONFIRMED
            order.setStatus("CONFIRMED");
            orderRepository.save(order); // Lưu trạng thái đơn hàng đã cập nhật

            // 3. Tạo bản ghi giao dịch thanh toán
            paymentTransaction = new PaymentTransaction();
            paymentTransaction.setOrder(order); // Thiết lập mối quan hệ với Order
            paymentTransaction.setContent(content);
            paymentTransaction.setDatetime(new Date());

            paymentTransactionRepository.save(paymentTransaction); // Lưu giao dịch thanh toán
        } else {
            // Xử lý trường hợp thanh toán thất bại
            paymentTransaction = new PaymentTransaction();
            paymentTransaction.setOrder(order);
            paymentTransaction.setContent(content);
            paymentTransaction.setDatetime(new Date());

            paymentTransactionRepository.save(paymentTransaction); // Lưu thông tin giao dịch thất bại (tùy chọn)

            throw new RuntimeException("Payment processing failed for order ID: " + orderId);
        }

        return convertToTransactionDTO(paymentTransaction);
    }

    private TransactionDto convertToTransactionDTO(PaymentTransaction paymentTransaction) {
        TransactionDto dto = new TransactionDto();
        dto.setOrderID(paymentTransaction.getOrder().getId());
        dto.setContent(paymentTransaction.getContent());
        dto.setDatetime(paymentTransaction.getDatetime());
        return dto;
    }

    // Không có nơi lưu trữ dữ liệu tập trung trong class này
}