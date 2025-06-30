package com.example.aims.subsystem.VNPay;

import java.security.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

class VNPayConfig {
    // VNPay configuration for payment
    protected static String paymentUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    // protected static String returnUrl = "http://localhost:5173/payment-success";
    protected static String returnUrl = "https://767b-1-52-236-95.ngrok-free.app/api/payments/vnpay-return";
    protected static String vnpayTmnCode = "DOIHA1RT";
    protected static String secretKey = "D651N17NCI55EOYGOM6XKRAJDHJ4Y2D7";
    protected static String vnpayVersion = "2.1.0";
    protected static String vnpayPayCommand = "pay";
    protected static String vnpayCurrCode = "VND";

    // VNPay configuration for refund
    protected static String refundUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";
    protected static String vnpayRefundCommand = "refund";
    protected static String vnpayTransactionType = "02";
    protected static String vnpayCreateBy = "AIMS";

    // SHA256 hash
    protected static String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // HMAC SHA512 hash
    protected static String hmacSHA512(String secret, String data) {
        try {
            Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA512");
            sha512_HMAC.init(secret_key);
            byte[] hash = sha512_HMAC.doFinal(data.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}