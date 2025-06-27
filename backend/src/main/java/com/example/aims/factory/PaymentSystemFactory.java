package com.example.aims.factory;

import com.example.aims.subsystem.IPaymentSystem;
import com.example.aims.subsystem.Momo.MomoSubsystem;
import com.example.aims.subsystem.VNPay.VNPaySubsystem;

public class PaymentSystemFactory {
    public static IPaymentSystem getPaymentSystem(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Payment type cannot be null");
        }
        switch (type.toLowerCase()) {
            case "vnpay":
                return new VNPaySubsystem();
            case "momo":
                return new MomoSubsystem(); 
            default:
                throw new IllegalArgumentException("Unsupported payment type: " + type);
        }
    }
}