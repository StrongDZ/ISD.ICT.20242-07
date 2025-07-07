package com.example.aims.subsystem.Momo;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.aims.dto.order.PaymentOrderRequestDTO;
import com.example.aims.dto.order.PaymentOrderResponseFromReturnDTO;
import com.example.aims.dto.transaction.TransactionResponseDTO;
import com.example.aims.model.PaymentTransaction;
import com.example.aims.subsystem.IPaymentSystem;

@Component
public class Momo implements IPaymentSystem {

    private final MomoPay pay = new MomoPay();
    private final MomoRefund refund = new MomoRefund();

    public String getPaymentUrl(PaymentOrderRequestDTO dto) {
        Double orderTotal = dto.getAmount();
        int amount = (int) (orderTotal * 100);
        String content = dto.getContent();
        if (content == null || content.isEmpty()) {
            content = "Order: " + dto.getOrderId();
        }
        String orderId = dto.getOrderId();
        try {
            return pay.generateUrl(amount, content, orderId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public PaymentTransaction getTransactionInfo(Map<String, String> momoResponse,
            PaymentOrderResponseFromReturnDTO orderDto) {
        return pay.responeParsing(momoResponse, orderDto);
    }

    @Override
    public String getRefundInfo(TransactionResponseDTO dto) {
        String response = refund.requestMomoRefund(dto);
        return refund.parseResponse(response);
    }

    /**
     * Returns the payment type identifier for MoMo.
     * 
     * @return "momo"
     */
    @Override
    public String getPaymentType() {
        return "momo";
    }
}