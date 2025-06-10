package com.example.aims.service;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.PaymentOrderRequestDTO;
import com.example.aims.exception.PaymentException.AbnormalTransactionException;
import com.example.aims.exception.PaymentException.AccountnotRegisterException;
import com.example.aims.exception.PaymentException.BlockAccountException;
import com.example.aims.exception.PaymentException.CustomerCancelException;
import com.example.aims.exception.PaymentException.ExceedQuotasException;
import com.example.aims.exception.PaymentException.InsufficientBalanceException;
import com.example.aims.exception.PaymentException.OtherException;
import com.example.aims.exception.PaymentException.PaymentException;
import com.example.aims.exception.PaymentException.SystemMaintananceException;
import com.example.aims.exception.PaymentException.TimeRunOutPaymentException;
import com.example.aims.exception.PaymentException.WrongAccountAuthenException;
import com.example.aims.exception.PaymentException.WrongOTPInputException;
import com.example.aims.exception.PaymentException.WrongPasswordException;
import com.example.aims.model.Order;
import com.example.aims.model.PaymentTransaction;
import com.example.aims.repository.OrderRepository;
import com.example.aims.repository.PaymentTransactionRepository;
import com.example.aims.subsystem.VNPay.VNPaySubsystem;

import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private final OrderRepository currentOrder; // Biến instance để giữ đơn hàng hiện tại (chỉ dùng cho test)
    @Autowired
    private final PaymentTransactionRepository currentPaymentTransaction; // Biến instance cho giao dịch thanh toán

    private VNPaySubsystem vnpay = new VNPaySubsystem();

    public PayOrderService(OrderRepository orderRepository, PaymentTransactionRepository paymentTransactionRepository) {
        this.currentOrder = orderRepository;
        this.currentPaymentTransaction = paymentTransactionRepository;
    }

    // public Optional<Order> findOrderById(String orderId) {
    // return Optional.ofNullable(currentOrder.findByOrderId(orderId));
    // }

    // public Optional<PaymentTransaction> findPaymentTransactionByOrderId(String
    // orderId) {
    // return Optional.ofNullable(currentPaymentTransaction.findByOrderId(orderId));
    // }
    public String getPaymentURL(String orderId) {
        Optional<Order> orderOptional = currentOrder.findByOrderID(orderId);
        if (orderOptional.isEmpty()) {
            throw new IllegalArgumentException("Order not found with ID: " + orderId);
        }
        Order order = orderOptional.get();
        
        if (OrderStatus.PENDING == order.getStatus()) {
            throw new IllegalStateException(
                    "Order is not in PENDING state for payment. Current status: " + order.getStatus());
        }
        PaymentOrderRequestDTO dto = new PaymentOrderRequestDTO();
        dto.setOrderId(order.getOrderID());
        dto.setAmount(order.getTotalAmount());
        dto.setContent("Payment for order: " + order.getOrderID() + "Order created by: " + order.getCustomer().getGmail());
        return vnpay.getPaymentUrl(dto); // 1. Gọi service/component xử lý thanh toán thực tế (tương tự như trước)
    }

    public String processPayment(Map<String, String> allRequestParams) {
        String responseCode = allRequestParams.get("vnp_ResponseCode");
        if (responseCode.equals("00")) {
            String orderID = allRequestParams.get("vnp_TxnRef");
            Order order = currentOrder.findByOrderID(orderID)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderID));
            PaymentTransaction paymentTransaction = vnpay.getTransactionInfo(allRequestParams, order);

            PaymentTransaction savedTransaction = currentPaymentTransaction.save(paymentTransaction);
            String transactionNo = savedTransaction.getTransactionNo();
            
            
            order.setStatus(OrderStatus.PENDING);
            currentOrder.save(order);
            sendMail(transactionNo);
            return "redirect:" + "http://localhost:3001/payment-success?orderId=" + orderID;
        } else if (responseCode.equals("24")) { // Payment decline
            return "redirect:" + "http://localhost:3001/invoice";
        } else { // Payment error
            try {
                responseCodeError(responseCode);
            } catch (PaymentException e) {
                System.out.println(e.getMessage());
            }
            return "redirect:" + "http://localhost:3001/payment-error";
        }
    }

    public void sendMail(String transactionNo) {
    }

    public void responseCodeError(@NotNull String responseCode) {
        switch (responseCode) {
            case "07":
                throw new AbnormalTransactionException();
            case "09":
                throw new AccountnotRegisterException();
            case "10":
                throw new WrongAccountAuthenException();
            case "11":
                throw new TimeRunOutPaymentException();
            case "12":
                throw new BlockAccountException();
            case "13":
                throw new WrongOTPInputException();
            case "24":
                throw new CustomerCancelException();
            case "51":
                throw new InsufficientBalanceException();
            case "65":
                throw new ExceedQuotasException();
            case "75":
                throw new SystemMaintananceException();
            case "79":
                throw new WrongPasswordException();
            case "99":
                throw new OtherException();
        }
    }
    // public PaymentTransaction getPaymentTransactionByOrderId(String orderId) {
    // return findPaymentTransactionByOrderId(orderId).orElse(null);
    // }

    // Không có nơi lưu trữ dữ liệu tập trung trong class này
}