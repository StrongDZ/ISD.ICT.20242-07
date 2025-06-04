package com.example.aims.exception.PaymentException;

// 07
public class AbnormalTransactionException extends PaymentException {
    public AbnormalTransactionException() {
        super("Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).");
    }

}
