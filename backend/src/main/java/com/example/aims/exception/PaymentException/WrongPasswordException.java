package com.example.aims.exception.PaymentException;

// 79
public class WrongPasswordException extends PaymentException {
    public WrongPasswordException() {
        super("Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch.");
    }
    
}
