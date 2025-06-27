package com.example.aims.mapper.PaymentError;

import com.example.aims.exception.PaymentException.PaymentException;

/**
 * Strategy interface for payment error mapping.
 * Each payment gateway (VNPay, Momo, etc.) will implement this interface
 * to handle their specific error codes.
 */
public interface IPaymentErrorMapper {

    /**
     * Maps response codes from payment gateway to appropriate exceptions.
     * 
     * @param responseCode The response code from the payment gateway
     * @throws PaymentException if the response code indicates an error
     */
    void responseCodeError(String responseCode);

    /**
     * Gets the payment type this mapper handles.
     * 
     * @return The payment type (e.g., "vnpay", "momo")
     */
    String getPaymentType();
}