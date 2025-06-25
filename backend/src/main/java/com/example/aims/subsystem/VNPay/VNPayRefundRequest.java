package com.example.aims.subsystem.VNPay;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import com.example.aims.dto.transaction.TransactionResponseDTO;

public class VNPayRefundRequest {
    private String getIPAddr() {
        return "192.168.2.14";
    }

    /**
     * This method is used to request a refund from VNPay.
     *
     * @param transaction The transaction details for which the refund is requested.
     * @return A string response from the VNPay API indicating the result of the refund request.
     */
    public String requestVNPayRefund(TransactionResponseDTO transaction) {
        Random random = new Random();
        String vnp_TxnRef = transaction.getOrder().getOrderID();
        String vnp_Amount = String.valueOf(transaction.getAmount() * 100); // Convert to VND
        String vnp_OrderInfo = "Refund for Order ID: " + transaction.getTransactionId();
        String vnp_TransactionNo = transaction.getTransactionNo();
        String vnp_TransactionDate = transaction.getDatetime().toString();

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        Map<String, String> vnpayParams = new HashMap<>();
        String vnp_RequestId = String.valueOf(random.nextInt(1000000));
        vnpayParams.put("vnp_RequestId", vnp_RequestId);
        vnpayParams.put("vnp_Version", VNPayConfig.vnpayVersion);
        vnpayParams.put("vnp_Command", VNPayConfig.vnpayRefundCommand);
        vnpayParams.put("vnp_TmnCode", VNPayConfig.vnpayTmnCode);
        vnpayParams.put("vnp_TransactionType", VNPayConfig.vnpayTransactionType);
        vnpayParams.put("vnp_TxnRef", vnp_TxnRef);
        vnpayParams.put("vnp_Amount", vnp_Amount); // Convert to VND
        vnpayParams.put("vnp_OrderInfo", vnp_OrderInfo);
        vnpayParams.put("vnp_TransactionNo", vnp_TransactionNo);
        vnpayParams.put("vnp_TransactionDate", vnp_TransactionDate);
        vnpayParams.put("vnp_CreateBy", VNPayConfig.vnpayCreateBy);
        vnpayParams.put("vnp_CreateDate", vnp_CreateDate);
        vnpayParams.put("vnp_IpAddr", getIPAddr());

        String hash_Data = String.join("|", vnp_RequestId, VNPayConfig.vnpayVersion, VNPayConfig.vnpayRefundCommand,
                VNPayConfig.vnpayTmnCode,
                "02", vnp_TxnRef, vnp_Amount, vnp_TransactionNo, vnp_TransactionDate,
                VNPayConfig.vnpayCreateBy, vnp_CreateDate, getIPAddr(), vnp_OrderInfo);

        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hash_Data);
        vnpayParams.put("vnp_SecureHash", vnp_SecureHash);

        Gson gson = new Gson();
        Type typeObject = new TypeToken<HashMap>() {
        }.getType();
        String refundJSON = gson.toJson(vnpayParams, typeObject);
        try {
            // Send refund request to VNPay
            URI uri = new URI(VNPayConfig.refundUrl);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "Application/json");
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(refundJSON);
            wr.flush();
            wr.close();
            // Get response data
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String output;
            StringBuilder response = new StringBuilder();
            while ((output = in.readLine()) != null) {
                response.append(output);
            }
            in.close();
            System.out.print(response);
            return response.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}