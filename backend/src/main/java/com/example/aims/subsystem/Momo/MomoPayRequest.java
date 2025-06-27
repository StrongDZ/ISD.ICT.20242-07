package com.example.aims.subsystem.Momo;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MomoPayRequest {
    private String getIPAddr() {
        return "192.168.2.1";
    }

    /**
     * Generates a payment URL for VNPay.
     *
     * @param amount  The amount to be paid in VND.
     * @param content The content of the payment.
     * @param orderId The order ID associated with the payment.
     * @return A string representing the payment URL.
     * @throws UnsupportedEncodingException If encoding fails.
     */
    protected String generateUrl(int amount, String content, String orderId) throws UnsupportedEncodingException {
        // This method should implement the logic to generate the payment URL
        // using VNPay's API or signature generation.
        // Currently, it is a placeholder and does not perform any operations.
        Map<String, String> vnpayParams = new HashMap<>();
        vnpayParams.put("vnp_Version", MomoConfig.vnpayVersion);
        vnpayParams.put("vnp_Command", MomoConfig.vnpayPayCommand);
        vnpayParams.put("vnp_TmnCode", MomoConfig.vnpayTmnCode);
        vnpayParams.put("vnp_Amount", String.valueOf(amount));
        vnpayParams.put("vnp_OrderType", "other");
        vnpayParams.put("vnp_CurrCode", MomoConfig.vnpayCurrCode);
        vnpayParams.put("vnp_BankCode", "");
        vnpayParams.put("vnp_TxnRef", orderId);
        vnpayParams.put("vnp_OrderInfo", content);
        vnpayParams.put("vnp_Locale", "vn");
        vnpayParams.put("vnp_IpAddr", getIPAddr());
        vnpayParams.put("vnp_ReturnUrl", MomoConfig.returnUrl);
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnpayParams.put("vnp_CreateDate", vnp_CreateDate);
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnpayParams.put("vnp_ExpireDate", vnp_ExpireDate);
        List<String> fieldNames = new ArrayList<>(vnpayParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnpayParams.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = MomoConfig.hmacSHA512(MomoConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        // Print checkoutUrl to console
        // System.out.println("URL: " + VNPayConfig.paymentUrl + "?" + queryUrl);
        return MomoConfig.paymentUrl + "?" + queryUrl;
    }
}
