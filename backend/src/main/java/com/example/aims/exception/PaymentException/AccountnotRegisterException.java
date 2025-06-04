package com.example.aims.exception.PaymentException;

//09
public class AccountnotRegisterException extends PaymentException {
    public AccountnotRegisterException() {
        super("Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.");
    }

}
