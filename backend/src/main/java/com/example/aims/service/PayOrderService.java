package com.example.aims.service;
import com.example.aims.dto.OrderDTO;
import com.example.aims.dto.ProductDTO;
import com.example.aims.dto.TransactionDto;
import com.example.aims.model.DeliveryInfo;
import com.example.aims.model.Order;
import com.example.aims.model.OrderItem;
import com.example.aims.model.PaymentTransaction;
import com.example.aims.repository.*;
import com.example.aims.subsystem.IPaymentSystem;
import com.example.aims.subsystem.VNPay.VNPaySubsystem;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PayOrderService {
    private final IPaymentSystem vnpay = new VNPaySubsystem();
    @Autowired
    private final OrderRepository orderRepository;
    @Autowired
    private final TransactionInfoRepository transactionInfoRepository;
    @Autowired
    private final DeliveryInfoRepository deliveryInfoRepository;
    @Autowired
    private final OrderItemRepository orderItemRepository;
    @Autowired

    public PayOrderService(OrderRepository orderRepository, TransactionInfoRepository transactionInfoRepository, DeliveryInfoRepository deliveryInfoRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.transactionInfoRepository = transactionInfoRepository;
        this.deliveryInfoRepository = deliveryInfoRepository;
        this.orderItemRepository = orderItemRepository;
    }

    // Get payment URL using VNPay subsystem
//    public String getPaymentUrl(String orderId){
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
//        System.out.println(vnpay.getPaymentUrl(order));
//        return vnpay.getPaymentUrl(order);
//    }
//
//    public PaymentTransaction getTransactionInfo(Map<String, String> transactionInfo){
//        return vnpay.getTransactionInfo(transactionInfo, orderRepository, transactionInfoRepository);
//    }
//
//    public void sendMail(String transactionID){
//    }
//
//    public Integer saveTransaction(PaymentTransaction transactionInfo){
//        PaymentTransaction savedTransaction = transactionInfoRepository.save(transactionInfo);
//        return savedTransaction.getId();
//    }
//
//    // Get transaction history
//    public TransactionDto getTransactionHistory(Integer transactionId){
//        PaymentTransaction transactionInfo = transactionInfoRepository.findById(transactionId)
//                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with ID: " + transactionId));
//        Order order = transactionInfo.getOrder();
//        TransactionDto transactionDto = new TransactionDto();
//        return transactionDto;
//    }
//
//    // Get order info
//    public OrderDTO getOrderInfo(Integer transactionId) {
//        PaymentTransaction transactionInfo = transactionInfoRepository.findById(transactionId)
//                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with ID: " + transactionId));
//        Order order = transactionInfo.getOrder();
//        DeliveryInfo deliveryInfo = order.getDeliveryInfo();
////        List<ProductOrderEntity> productDtos = new ArrayList<>();
////        productDtos = productOrderRepository.findByOrderId(order.getId());
//        OrderDTO orderInfoDto = new OrderDTO();
//        System.out.println(orderInfoDto);
//        return orderInfoDto;
//    }
//
//    // Get product info
//    public List<ProductDTO> getProductInfo(Integer orderId){
//        List<OrderItem> productOrderEntities = OrderItemRepository.findByOrderId(orderId);
//        List<ProductDTO> productDtos = new ArrayList<>();
//        return productDtos;
//    }
//
//    // Cancel order
//    public String cancelOrder(Integer transactionId){
//        // Refund transaction
//        PaymentTransaction transactionInfo = transactionInfoRepository.findById(transactionId)
//                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with ID: " + transactionId));
//        Order order = transactionInfo.getOrder();
//        orderRepository.save(order);
//        String response = vnpay.refundTransaction(transactionInfo);
////        // Delete delivery info & order
////        OrderEntity order = transactionInfo.getOrder();
////        // Delete delivery info
////        deliveryInfoRepository.delete(order.getDeliveryInfo());
////        // Delete order
////        orderRepository.delete(order);
////        // Delete transaction info
////        transactionInfoRepository.delete(transactionInfo);
//        return response;
//    }
}
