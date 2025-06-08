package com.example.aims.exception.PaymentException;

// 13
public class WrongOTPInputException extends PaymentException {
    public WrongOTPInputException() {
        super("Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch.");
    }
    
}
