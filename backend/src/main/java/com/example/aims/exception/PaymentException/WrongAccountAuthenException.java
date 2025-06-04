package com.example.aims.exception.PaymentException;

//10
public class WrongAccountAuthenException extends PaymentException {
    public WrongAccountAuthenException() {
        super("Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần.");
    }
    
}
