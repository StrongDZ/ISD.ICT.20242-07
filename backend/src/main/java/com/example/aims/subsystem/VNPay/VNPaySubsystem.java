package com.example.aims.subsystem.VNPay;

import java.util.Map;

import com.example.aims.model.Order;
import com.example.aims.model.PaymentTransaction;
import com.example.aims.repository.OrderRepository;
import com.example.aims.subsystem.IPaymentSystem;

// Functional Cohesion – All fields and methods support the single purpose of integrating with VNPay payment system
// ✅ SRP respected – Class handles only VNPay-specific operations
// 🔧 Suggestion: Implement actual payment URL logic; split refund logic if needed in future

// 🔧 Improvement suggestions:
// - Implement the real logic of getPaymentUrl() to interact with VNPay's API or signature generation.
// - Extract URL generation or formatting logic to a helper class if it grows complex.
// - Consider separating refund functionality (if added later) into a distinct service if logic becomes too heavy.

public class VNPaySubsystem implements IPaymentSystem {

    // 🔗 Coupling:
    // Stamp Coupling – This class accepts an Order object and accesses only
    // selected fields
    // (e.g., order.getTotalAmount(), order.getShippingAddress(), order.getId()).
    // → Suggestion: Refactor method signatures to accept only required fields
    // (e.g., amount, orderId, address)
    // to reduce to Data Coupling and improve modularity/testability.

    private final VNPayPayRequest request = new VNPayPayRequest();
    private final VNPayPayResponse response = new VNPayPayResponse();
    private final VNPayRefundRequest refundRequest = new VNPayRefundRequest();
    private final VNPayRefundResponse refundResponse = new VNPayRefundResponse();

    public String getPaymentUrl(Order orderEntity) {
        // Get amount
        Double orderTotal = orderEntity.getTotalAmount();
        int amount = (int) (orderTotal * 100);
        // Build content for payment
        String content = orderEntity.getDeliveryInfo().getAddressDetail();
        if (content == null || content.isEmpty()) {
            content = "Order: " + orderEntity.getOrderID();
        }
        // Get order ID
        String orderId = orderEntity.getOrderID();
        // Generate request payment url
        try {
            return request.generateUrl(amount, content, orderId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public PaymentTransaction getTransactionInfo(Map<String, String> vnPayResponse, OrderRepository orderRepository){
        return response.responeParsing(vnPayResponse, orderRepository);
    }

    @Override
    public String getRefundInfo(PaymentTransaction transaction){
        String response = refundRequest.requestVNPayRefund(transaction);
        return refundResponse.parseResponse(response);
    }
}
