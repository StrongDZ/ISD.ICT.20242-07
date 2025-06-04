package com.example.aims.exception.PaymentException;

// 11
public class TimeRunOutPaymentException extends PaymentException {
    public TimeRunOutPaymentException() {
        super("Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.");
    }
    
}
