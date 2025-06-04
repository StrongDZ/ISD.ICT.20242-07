package com.example.aims.subsystem.VNPay;

import java.util.Map;

import com.example.aims.model.PaymentTransaction;
import com.example.aims.repository.OrderRepository;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class VNPayPayResponse {

    public PaymentTransaction responeParsing(Map<String, String> response, OrderRepository orderRepository) {
        PaymentTransaction transaction = new PaymentTransaction();
        String bank = response.get("vnp_BankCode");
        String orderId = response.get("vnp_TxnRef");
        String transactionNo = response.get("vnp_TransactionNo");
        String transactionStatus = response.get("vnp_ResponseCode");
        String transactionDate = response.get("vnp_PayDate");
        String transactionAmount = response.get("vnp_Amount");
        String cardType = response.get("vnp_CardType");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Etc/GMT+7"));
        try {
            Date date = dateFormat.parse(transactionDate);
            transaction.setDatetime(date);

        } catch (ParseException e) {
            e.printStackTrace();
            transaction.setDatetime(null);
        }
        transaction.setTransactionBank(bank);
        transaction.setAmount(Double.parseDouble(transactionAmount) / 100); // Convert from VND to original amount
        transaction.setOrderID(orderId);
        transaction.setTransactionNo(transactionNo);
        transaction.setTransactionStatus(transactionStatus);
        transaction.setCardType(cardType);
        transaction.setOrder(orderRepository.findById(orderId).orElse(null));
        return transaction;
    }

}
