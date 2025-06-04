package com.example.aims.exception.PaymentException;

// 65
public class ExceedQuotasException extends PaymentException {
    public ExceedQuotasException() {
        super("Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.");
    }
    
}
