package com.example.aims.subsystem.VNPay;

import java.util.Map;

import com.example.aims.dto.order.OrderDTO;
import com.example.aims.dto.order.OrderResponseDTO;
import com.example.aims.model.DeliveryInfo;
import com.example.aims.model.Order;
import com.example.aims.model.PaymentTransaction;
import com.example.aims.model.Users;
import com.example.aims.repository.OrderRepository;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class VNPayPayResponse {

    public PaymentTransaction responeParsing(Map<String, String> response, OrderResponseDTO order) {
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
        transaction.setTransactionId(orderId);
        transaction.setTransactionNo(transactionNo);
        transaction.setTransactionStatus(transactionStatus);
        transaction.setCardType(cardType);

        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.setAddressDetail(order.getDeliveryInfo().getAddressDetail());
        deliveryInfo.setCity(order.getDeliveryInfo().getCity());
        deliveryInfo.setDistrict(order.getDeliveryInfo().getDistrict());
        deliveryInfo.setPhoneNumber(order.getDeliveryInfo().getPhoneNumber());
        deliveryInfo.setRecipientName(order.getDeliveryInfo().getRecipientName());
        deliveryInfo.setMail(order.getDeliveryInfo().getMail());

        Users customer = new Users();
        customer.setUsername(order.getCustomerName());
        customer.setPassword(order.getCustomer().getPassword());
        customer.setId(order.getCustomer().getId());
        customer.setGmail(order.getCustomer().getGmail());
        customer.getType();
        customer.getUserStatus();

        Order orderNew = new Order();
        orderNew.setOrderID(order.getOrderID());
        orderNew.setTotalAmount(order.getTotalAmount());
        orderNew.setDeliveryInfo(deliveryInfo);
        orderNew.setCustomer(customer);
        orderNew.setShippingAddress(order.getShippingAddress());
        orderNew.setCustomerName(order.getCustomerName());
        orderNew.setPhoneNumber(order.getPhoneNumber());
        orderNew.setStatus(order.getStatus());
        orderNew.setProvince(order.getProvince());

        transaction.setOrder(orderNew);
        return transaction;
    }

}