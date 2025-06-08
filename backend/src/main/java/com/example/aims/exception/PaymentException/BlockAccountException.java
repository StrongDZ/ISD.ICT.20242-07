package com.example.aims.exception.PaymentException;

//12
public class BlockAccountException extends PaymentException {
    public BlockAccountException() {
        super("Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.");
    }
    
}
