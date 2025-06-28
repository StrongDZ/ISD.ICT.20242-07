package com.example.aims.service;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.order.OrderInfoDTO;
import com.example.aims.dto.order.OrderItemDTO;
import com.example.aims.dto.order.PaymentOrderRequestDTO;
import com.example.aims.dto.order.PaymentOrderResponseFromReturnDTO;
import com.example.aims.dto.transaction.TransactionResponseDTO;
import com.example.aims.exception.PaymentException.PaymentException;
import com.example.aims.mapper.OrderMapper;
import com.example.aims.mapper.PaymentError.IPaymentErrorMapper;
import com.example.aims.model.Book;
import com.example.aims.model.Order;
import com.example.aims.model.OrderItem;
import com.example.aims.model.PaymentTransaction;
import com.example.aims.repository.OrderItemRepository;
import com.example.aims.repository.OrderRepository;
import com.example.aims.repository.PaymentTransactionRepository;
import com.example.aims.factory.PaymentErrorMapperFactory;
import com.example.aims.factory.PaymentSystemFactory;
import com.example.aims.subsystem.IPaymentSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PayOrderServiceTest {

    private OrderRepository orderRepository;
    private PaymentTransactionRepository paymentTransactionRepository;
    private OrderItemRepository orderItemRepository;
    private PaymentErrorMapperFactory errorMapperFactory;
    private EmailService emailService;
    private PaymentSystemFactory paymentSystemFactory;
    private OrderMapper orderMapper;

    private PayOrderService payOrderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        paymentTransactionRepository = mock(PaymentTransactionRepository.class);
        orderItemRepository = mock(OrderItemRepository.class);
        errorMapperFactory = mock(PaymentErrorMapperFactory.class);
        emailService = mock(EmailService.class);
        paymentSystemFactory = mock(PaymentSystemFactory.class);
        orderMapper = mock(OrderMapper.class);

        payOrderService = new PayOrderService(
                orderRepository,
                paymentTransactionRepository,
                orderItemRepository,
                errorMapperFactory,
                emailService,
                paymentSystemFactory,
                orderMapper
        );

        // inject manually since orderMapper is @Autowired
    }

    @Test
    void testGetPaymentURL_Success() {
        Order order = new Order();
        order.setOrderID("ORD001");
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(100.0);

        PaymentOrderRequestDTO dto = new PaymentOrderRequestDTO();
        IPaymentSystem paymentSystem = mock(IPaymentSystem.class);

        when(orderRepository.findByOrderID("ORD001")).thenReturn(Optional.of(order));
        when(orderMapper.toPaymentOrderRequestDTO(order)).thenReturn(dto);
        when(paymentSystemFactory.getPaymentSystem("vnpay")).thenReturn(paymentSystem);
        when(paymentSystem.getPaymentUrl(dto)).thenReturn("http://pay.url");

        String url = payOrderService.getPaymentURL("ORD001", "vnpay");
        assertEquals("http://pay.url", url);
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
    void testProcessPayment_Success() throws Exception {
        Map<String, String> params = Map.of("vnp_ResponseCode", "00", "vnp_TxnRef", "ORD001");

        Order order = new Order();
        order.setOrderID("ORD001");
        order.setStatus(OrderStatus.PENDING);

        PaymentOrderResponseFromReturnDTO orderDto = new PaymentOrderResponseFromReturnDTO();
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionId("TXN001");
        transaction.setOrder(order);

        IPaymentSystem paymentSystem = mock(IPaymentSystem.class);

        when(orderRepository.findByOrderID("ORD001")).thenReturn(Optional.of(order));
        when(orderMapper.toPaymentOrderResponseFromReturnDTO(order)).thenReturn(orderDto);
        when(paymentSystemFactory.getPaymentSystem("vnpay")).thenReturn(paymentSystem);
        when(paymentSystem.getTransactionInfo(params, orderDto)).thenReturn(transaction);
        when(paymentTransactionRepository.save(transaction)).thenReturn(transaction);
        when(orderRepository.save(order)).thenReturn(order);

        String result = payOrderService.processPayment(params, "vnpay");

        assertTrue(result.contains("payment-success"));
        verify(emailService).sendPaymentConfirmationEmail("TXN001");
    }

    @Test
    void testProcessPayment_Decline() {
        Map<String, String> params = Map.of("vnp_ResponseCode", "24", "vnp_TxnRef", "ORD001");
        String result = payOrderService.processPayment(params, "vnpay");
        assertTrue(result.contains("payment-decline"));
    }

    @Test
    void testProcessPayment_Error() throws PaymentException {
        Map<String, String> params = Map.of("vnp_ResponseCode", "99", "vnp_TxnRef", "ORD001");

        IPaymentErrorMapper errorMapper = mock(IPaymentErrorMapper.class);
        when(errorMapperFactory.getMapper("vnpay")).thenReturn(errorMapper);
        doThrow(new PaymentException("Error")).when(errorMapper).responseCodeError("99");

        String result = payOrderService.processPayment(params, "vnpay");
        assertTrue(result.contains("payment-error"));
    }

    @Test
    void testProcessPayment_OrderNotFound() {
        Map<String, String> params = Map.of("vnp_ResponseCode", "00", "vnp_TxnRef", "ORD404");
        when(orderRepository.findByOrderID("ORD404")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> payOrderService.processPayment(params, "vnpay"));
    }

    @Test
    void testGetPaymentHistory_Success() {
        PaymentTransaction txn = new PaymentTransaction();
        txn.setTransactionId("TXN001");
        txn.setTransactionNo("123456");
        txn.setAmount(100.0);
        txn.setPaymentType("vnpay");
        txn.setDatetime(new Date());

        Order order = new Order();
        txn.setOrder(order);

        PaymentOrderResponseFromReturnDTO dto = new PaymentOrderResponseFromReturnDTO();

        when(paymentTransactionRepository.findByTransactionId("TXN001")).thenReturn(Optional.of(txn));
        when(orderMapper.toPaymentOrderResponseFromReturnDTO(order)).thenReturn(dto);

        TransactionResponseDTO response = payOrderService.getPaymentHistory("TXN001");

        assertEquals("TXN001", response.getTransactionId());
        assertEquals("123456", response.getTransactionNo());
        assertEquals("vnpay", response.getPaymentType());
        assertEquals(dto, response.getOrder());
    }

    @Test
    void testGetOrderInfo_Success() {
        Order order = new Order();
        order.setOrderID("ORD001");

        OrderInfoDTO dto = new OrderInfoDTO();
        dto.setOrderID("ORD001");

        when(orderRepository.findByOrderID("ORD001")).thenReturn(Optional.of(order));
        when(orderMapper.toOrderInfoDTO(order)).thenReturn(dto);

        OrderInfoDTO result = payOrderService.getOrderInfo("ORD001");
        assertEquals("ORD001", result.getOrderID());
    }

    @Test
    void testGetOrderProduct_Success() {
        Order order = new Order();
        order.setOrderID("ORD001");

        OrderInfoDTO dto = new OrderInfoDTO();
        dto.setOrderID("ORD001");

        OrderItem item = new OrderItem();
        Book book = new Book();
        book.setProductID("BOOK001");
        book.setTitle("Java");
        book.setPrice(100.0);
        item.setProduct(book);
        item.setQuantity(2);

        when(orderRepository.findByOrderID("ORD001")).thenReturn(Optional.of(order));
        when(orderMapper.toOrderInfoDTO(order)).thenReturn(dto);
        when(orderMapper.toOrder(dto)).thenReturn(order);
        when(orderItemRepository.findByOrder(order)).thenReturn(List.of(item));

        List<OrderItemDTO> result = payOrderService.getOrderProduct("ORD001");

        assertEquals(1, result.size());
        assertEquals("BOOK001", result.get(0).getProductID());
        assertEquals("Java", result.get(0).getProductTitle());
        assertEquals(2, result.get(0).getQuantity());
    }
}
