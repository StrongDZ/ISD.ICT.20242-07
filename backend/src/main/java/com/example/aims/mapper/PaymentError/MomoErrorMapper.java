package com.example.aims.mapper.PaymentError;

import org.springframework.stereotype.Component;

import com.example.aims.exception.PaymentException.AbnormalTransactionException;
import com.example.aims.exception.PaymentException.AccountnotRegisterException;
import com.example.aims.exception.PaymentException.BlockAccountException;
import com.example.aims.exception.PaymentException.CustomerCancelException;
import com.example.aims.exception.PaymentException.ExceedQuotasException;
import com.example.aims.exception.PaymentException.InsufficientBalanceException;
import com.example.aims.exception.PaymentException.OtherException;
import com.example.aims.exception.PaymentException.PaymentException;
import com.example.aims.exception.PaymentException.SystemMaintananceException;
import com.example.aims.exception.PaymentException.TimeRunOutPaymentException;
import com.example.aims.exception.PaymentException.WrongAccountAuthenException;
import com.example.aims.exception.PaymentException.WrongOTPInputException;
import com.example.aims.exception.PaymentException.WrongPasswordException;

import jakarta.validation.constraints.NotNull;

@Component
public class MomoErrorMapper implements IPaymentErrorMapper {

    @Override
    public String getPaymentType() {
        return "momo";
    }

    /**
     * Handles different response codes from Momo payment gateway and throws
     * appropriate exceptions.
     * 
     * @param responseCode The response code from Momo payment gateway.
     * @throws PaymentException if the response code indicates an error.
     */
    @Override
    public void responseCodeError(@NotNull String responseCode) {
        switch (responseCode) {
            case "07":
                throw new AbnormalTransactionException();
            case "09":
                throw new AccountnotRegisterException();
            case "10":
                throw new WrongAccountAuthenException();
            case "11":
                throw new TimeRunOutPaymentException();
            case "12":
                throw new BlockAccountException();
            case "13":
                throw new WrongOTPInputException();
            case "24":
                throw new CustomerCancelException();
            case "51":
                throw new InsufficientBalanceException();
            case "65":
                throw new ExceedQuotasException();
            case "75":
                throw new SystemMaintananceException();
            case "79":
                throw new WrongPasswordException();
            case "99":
                throw new OtherException();
        }
    }
}