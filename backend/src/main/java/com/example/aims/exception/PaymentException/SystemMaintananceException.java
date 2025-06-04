package com.example.aims.exception.PaymentException;

// 75
public class SystemMaintananceException extends PaymentException {
    public SystemMaintananceException() {
        super("Giao dịch không thành công do: Ngân hàng thanh toán đang bảo trì.");
    }
    
}
