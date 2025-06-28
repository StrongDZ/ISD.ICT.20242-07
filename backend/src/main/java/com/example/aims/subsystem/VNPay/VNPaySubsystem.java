package com.example.aims.subsystem.VNPay;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.aims.dto.order.PaymentOrderRequestDTO;
import com.example.aims.dto.order.PaymentOrderResponseFromReturnDTO;
import com.example.aims.dto.transaction.TransactionResponseDTO;
import com.example.aims.model.PaymentTransaction;
import com.example.aims.subsystem.IPaymentSystem;

@Component
public class VNPaySubsystem implements IPaymentSystem {

    private final VNPayPayRequest request = new VNPayPayRequest();
    private final VNPayPayResponse response = new VNPayPayResponse();
    private final VNPayRefundRequest refundRequest = new VNPayRefundRequest();
    private final VNPayRefundResponse refundResponse = new VNPayRefundResponse();

    /**
     * Generates a payment URL for the given order request.
     *
     * @param dto The payment order request DTO containing order details.
     * @return A string representing the payment URL for the order.
     */
    public String getPaymentUrl(PaymentOrderRequestDTO dto) {
        // Get amount
        Double orderTotal = dto.getAmount();
        int amount = (int) (orderTotal * 100);
        // Build content for payment
        String content = dto.getContent();
        if (content == null || content.isEmpty()) {
            content = "Order: " + dto.getOrderId();
        }
        // Get order ID
        String orderId = dto.getOrderId();
        // Generate request payment url
        try {
            return request.generateUrl(amount, content, orderId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves transaction information based on the VNPay response and order
     * details.
     *
     * @param vnPayResponse The response map from VNPay containing transaction
     *                      details.
     * @param orderDto      The order response DTO containing order details.
     * @return A PaymentTransaction object containing parsed transaction
     *         information.
     */
    @Override
    public PaymentTransaction getTransactionInfo(Map<String, String> vnPayResponse,
            PaymentOrderResponseFromReturnDTO orderDto) {
        return response.responeParsing(vnPayResponse, orderDto);
    }

    /**
     * Processes a refund request for a transaction.
     *
     * @param dto The transaction response DTO containing details of the transaction
     *            to be refunded.
     * @return A string containing the refund information or status.
     */
    @Override
    public String getRefundInfo(TransactionResponseDTO dto) {
        String response = refundRequest.requestVNPayRefund(dto);
        return refundResponse.parseResponse(response);
    }

    /**
     * Returns the payment type identifier for VNPay.
     * 
     * @return "vnpay"
     */
    @Override
    public String getPaymentType() {
        return "vnpay";
    }

}