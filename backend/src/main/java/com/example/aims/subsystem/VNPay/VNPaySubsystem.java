package com.example.aims.subsystem.VNPay;

import com.example.aims.model.Order;
import com.example.aims.subsystem.IPaymentSystem;

public class VNPaySubsystem implements IPaymentSystem {
    private final VNPayPayRequest request = new VNPayPayRequest();
    private final VNPayPayResponse response = new VNPayPayResponse();
    private final VNPayRefundRequest refundRequest = new VNPayRefundRequest();
    private final VNPayRefundResponse refundResponse = new VNPayRefundResponse();

    public String getPaymentUrl(Order orderEntity) {
        // Get amount
        Double orderTotal = orderEntity.getTotalAmount();
        int amount = (int) (orderTotal * 100);
        // Build content for payment
        String content = orderEntity.getShippingAddress();
        if (content == null || content.isEmpty()) {
            content = "Order: " + orderEntity.getOrderID();
        }
        // Get order ID
            Integer orderId = orderEntity.getOrderID();
        // Generate request payment url
        try {
            return "Hello";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
