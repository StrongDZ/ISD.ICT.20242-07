package com.example.aims.exception.PaymentException;

//24
public class CustomerCancelException extends PaymentException {
    public CustomerCancelException() {
        super("Giao dịch không thành công do: Khách hàng hủy giao dịch.");
    }
    
}
