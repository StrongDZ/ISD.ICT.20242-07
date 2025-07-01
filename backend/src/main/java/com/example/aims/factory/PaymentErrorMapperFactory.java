package com.example.aims.factory;

import org.springframework.stereotype.Component;

import com.example.aims.mapper.PaymentError.IPaymentErrorMapper;
import com.example.aims.mapper.PaymentError.MomoErrorMapper;
import com.example.aims.mapper.PaymentError.VNPayErrorMapper;

/**
 * Factory class for creating payment error mappers.
 * Uses Strategy pattern to select the appropriate mapper based on payment type.
 */
@Component
public class PaymentErrorMapperFactory {


    public IPaymentErrorMapper getMapper(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Payment type cannot be null");
        }
        switch (type.toLowerCase()) {
            case "vnpay":
                return new VNPayErrorMapper();
            case "momo":
                return new MomoErrorMapper();
            default:
                throw new IllegalArgumentException("Unsupported payment type: " + type);
        }
    }
}