package com.example.aims.exception.PaymentException;

public class OtherException extends PaymentException {
    public OtherException() {
        super("Giao dịch không thành công do: Lỗi khác.");
    }

}
