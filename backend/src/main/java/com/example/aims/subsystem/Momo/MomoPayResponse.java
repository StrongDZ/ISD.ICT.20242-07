package com.example.aims.subsystem.Momo;

import java.util.Map;

import com.example.aims.dto.order.PaymentOrderResponseFromReturnDTO;
import com.example.aims.model.DeliveryInfo;
import com.example.aims.model.Order;
import com.example.aims.model.PaymentTransaction;
import com.example.aims.model.Users;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MomoPayResponse {
    /**
     * Parses the response from VNPay and constructs a PaymentTransaction object.
     *
     * @param response The response map from VNPay containing transaction details.
     * @param order    The OrderResponseDTO containing order details.
     * @return A PaymentTransaction object populated with the parsed data.
     */
    public PaymentTransaction responeParsing(Map<String, String> response, PaymentOrderResponseFromReturnDTO order) {
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

        Order orderNew = new Order();
        orderNew.setOrderID(order.getOrderID());
        orderNew.setTotalAmount(order.getTotalAmount());
        orderNew.setDeliveryInfo(deliveryInfo);
        // orderNew.setShippingAddress(order.getShippingAddress());
        // orderNew.setCustomerName(order.getCustomerName());
        // orderNew.setPhoneNumber(order.getPhoneNumber());
        orderNew.setStatus(order.getStatus());
        // orderNew.setProvince(order.getProvince());

        transaction.setOrder(orderNew);
        transaction.setPaymentType("Momo");
        return transaction;
    }
}