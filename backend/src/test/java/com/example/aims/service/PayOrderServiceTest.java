package com.example.aims.service;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.order.PaymentOrderRequestDTO;
import com.example.aims.dto.order.PaymentOrderResponseFromReturnDTO;
import com.example.aims.dto.transaction.TransactionResponseDTO;
import com.example.aims.exception.PaymentException.PaymentException;
import com.example.aims.mapper.OrderMapper;
import com.example.aims.mapper.VNPayErrorMapper;
import com.example.aims.model.DeliveryInfo;
import com.example.aims.model.Order;
import com.example.aims.model.PaymentTransaction;
import com.example.aims.repository.OrderRepository;
import com.example.aims.repository.PaymentTransactionRepository;
import com.example.aims.factory.PaymentSystemFactory;
import com.example.aims.subsystem.IPaymentSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PayOrderServiceTest {
    private OrderRepository orderRepository;
    private PaymentTransactionRepository paymentTransactionRepository;
    private VNPayErrorMapper errorMapper;
    private OrderMapper orderMapper;
    private JavaMailSender javaMailSender;
    private PayOrderService payOrderService;

    @BeforeEach
    void setUp() throws Exception {
        orderRepository = mock(OrderRepository.class);
        paymentTransactionRepository = mock(PaymentTransactionRepository.class);
        errorMapper = mock(VNPayErrorMapper.class);
        orderMapper = mock(OrderMapper.class);
        javaMailSender = mock(JavaMailSender.class);
        payOrderService = new PayOrderService(orderRepository, paymentTransactionRepository, javaMailSender);
        // Sử dụng reflection để gán các field private
        Field errorMapperField = PayOrderService.class.getDeclaredField("errorMapper");
        errorMapperField.setAccessible(true);
        errorMapperField.set(payOrderService, errorMapper);
        Field orderMapperField = PayOrderService.class.getDeclaredField("orderMapper");
        orderMapperField.setAccessible(true);
        orderMapperField.set(payOrderService, orderMapper);
    }

    @Test
    void testGetPaymentURL_Success() {
        Order order = new Order();
        order.setOrderID("ORD001");
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(100.0);
        when(orderRepository.findByOrderID("ORD001")).thenReturn(Optional.of(order));
        PaymentOrderRequestDTO requestDTO = new PaymentOrderRequestDTO();
        when(orderMapper.toPaymentOrderRequestDTO(order)).thenReturn(requestDTO);
        IPaymentSystem paymentSystem = mock(IPaymentSystem.class);
        when(paymentSystem.getPaymentUrl(requestDTO)).thenReturn("http://pay.url");
        try (MockedStatic<PaymentSystemFactory> factoryMock = mockStatic(PaymentSystemFactory.class)) {
            factoryMock.when(() -> PaymentSystemFactory.getPaymentSystem("vnpay")).thenReturn(paymentSystem);
            String url = payOrderService.getPaymentURL("ORD001", "vnpay");
            assertEquals("http://pay.url", url);
        }
    }

    @Test
    void testGetPaymentURL_OrderNotFound() {
        when(orderRepository.findByOrderID("ORD404")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> payOrderService.getPaymentURL("ORD404", "vnpay"));
    }

    @Test
    void testGetPaymentURL_OrderNotPending() {
        Order order = new Order();
        order.setOrderID("ORD002");
        order.setStatus(OrderStatus.APPROVED);
        when(orderRepository.findByOrderID("ORD002")).thenReturn(Optional.of(order));
        assertThrows(IllegalStateException.class, () -> payOrderService.getPaymentURL("ORD002", "vnpay"));
    }


    @Test
    void testProcessPayment_Decline() {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_ResponseCode", "24");
        String redirect = payOrderService.processPayment(params, "vnpay");
        assertTrue(redirect.contains("payment-decline"));
    }

    @Test
    void testProcessPayment_Error() throws PaymentException {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_ResponseCode", "99");
        doThrow(new PaymentException("Error")).when(errorMapper).responseCodeError("99");
        String redirect = payOrderService.processPayment(params, "vnpay");
        assertTrue(redirect.contains("payment-error"));
    }

    @Test
    void testGetPaymentHistory_Success() {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionId("TXN001");
        transaction.setTransactionNo("123456");
        transaction.setAmount(100.0);
        transaction.setDatetime(new Date());
        Order order = new Order();
        transaction.setOrder(order);
        when(paymentTransactionRepository.findByTransactionId("TXN001")).thenReturn(Optional.of(transaction));
        PaymentOrderResponseFromReturnDTO orderDto = new PaymentOrderResponseFromReturnDTO();
        when(orderMapper.toPaymentOrderResponseFromReturnDTO(order)).thenReturn(orderDto);
        TransactionResponseDTO result = payOrderService.getPaymentHistory("TXN001");
        assertEquals("TXN001", result.getTransactionId());
        assertEquals("123456", result.getTransactionNo());
        assertEquals(100.0, result.getAmount());
        assertEquals(orderDto, result.getOrder());
    }

    @Test
    void testGetPaymentHistory_NotFound() {
        when(paymentTransactionRepository.findByTransactionId("TXN404")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> payOrderService.getPaymentHistory("TXN404"));
    }

    @Test
    void testSendMail() throws Exception {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionId("TXN001");
        Order order = new Order();
        order.setOrderID("ORD001");
        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.setMail("test@example.com");
        deliveryInfo.setRecipientName("Test User");
        order.setDeliveryInfo(deliveryInfo);
        transaction.setOrder(order);
        when(paymentTransactionRepository.findByTransactionId("TXN001")).thenReturn(Optional.of(transaction));
        payOrderService.sendMail("TXN001");
        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }
}