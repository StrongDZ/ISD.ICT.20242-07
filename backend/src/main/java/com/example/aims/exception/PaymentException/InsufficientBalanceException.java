package com.example.aims.exception.PaymentException;

//51
public class InsufficientBalanceException extends PaymentException {
    public InsufficientBalanceException() {
        super("Giao dịch không thành công do: Số dư tài khoản không đủ để thực hiện giao dịch.");
    }
    
}
