package com.example.aims.mapper;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.order.PaymentOrderResponseFromReturnDTO;
import com.example.aims.dto.order.PaymentOrderRetrievalDTO;
import com.example.aims.dto.transaction.TransactionResponseDTO;
import com.example.aims.dto.transaction.TransactionRetrievalDTO;
import com.example.aims.model.Order;
import com.example.aims.model.PaymentTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.Date;

public class TransactionMapperTest {

    private TransactionMapper transactionMapper;
    private OrderMapper orderMapper;

    @BeforeEach
    void setUp() throws Exception {
        orderMapper = mock(OrderMapper.class);
        transactionMapper = new TransactionMapper();

        // Use reflection to set the private field
        Field orderMapperField = TransactionMapper.class.getDeclaredField("orderMapper");
        orderMapperField.setAccessible(true);
        orderMapperField.set(transactionMapper, orderMapper);
    }

    @Test
    void testToTransactionResponseDTO() {
        // Arrange
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionId("TXN001");
        transaction.setTransactionNo("123456789");
        transaction.setAmount(1000.0);
        transaction.setDatetime(new Date());
        transaction.setTransactionBank("Vietcombank");
        transaction.setTransactionStatus("00");
        transaction.setCardType("ATM");

        Order order = new Order();
        order.setOrderID("ORD001");
        order.setTotalAmount(1000.0);
        order.setStatus(OrderStatus.PENDING);
        transaction.setOrder(order);

        PaymentOrderResponseFromReturnDTO orderDTO = new PaymentOrderResponseFromReturnDTO();
        orderDTO.setOrderID("ORD001");
        orderDTO.setTotalAmount(1000.0);
        orderDTO.setStatus(OrderStatus.PENDING);

        when(orderMapper.toPaymentOrderResponseFromReturnDTO(order)).thenReturn(orderDTO);

        // Act
        TransactionResponseDTO result = transactionMapper.toTransactionResponseDTO(transaction);

        // Assert
        assertNotNull(result);
        assertEquals("TXN001", result.getTransactionId());
        assertEquals("123456789", result.getTransactionNo());
        assertEquals(1000.0, result.getAmount());
        assertEquals(transaction.getDatetime(), result.getDatetime());
        assertEquals(orderDTO, result.getOrder());

        verify(orderMapper).toPaymentOrderResponseFromReturnDTO(order);
    }

    @Test
    void testToTransactionResponseDTO_WithNullTransaction() {
        // Act
        TransactionResponseDTO result = transactionMapper.toTransactionResponseDTO(null);

        // Assert
        assertNull(result);
    }

    @Test
    void testToTransactionRetrievalDTO() {
        // Arrange
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionId("TXN002");
        transaction.setTransactionNo("987654321");
        transaction.setAmount(2000.0);
        transaction.setDatetime(new Date());
        transaction.setTransactionBank("BIDV");
        transaction.setTransactionStatus("00");
        transaction.setCardType("CREDIT");

        Order order = new Order();
        order.setOrderID("ORD002");
        order.setTotalAmount(2000.0);
        order.setStatus(OrderStatus.PENDING);
        transaction.setOrder(order);

        PaymentOrderRetrievalDTO orderDTO = new PaymentOrderRetrievalDTO();
        orderDTO.setOrderID("ORD002");
        orderDTO.setTotalAmount(2000.0);
        orderDTO.setStatus(OrderStatus.PENDING);

        when(orderMapper.toPaymentOrderRetrievalDTO(order)).thenReturn(orderDTO);

        // Act
        TransactionRetrievalDTO result = transactionMapper.toTransactionRetrievalDTO(transaction);

        // Assert
        assertNotNull(result);
        assertEquals("TXN002", result.getTransactionId());
        assertEquals("987654321", result.getTransactionNo());
        assertEquals(2000.0, result.getAmount());
        assertEquals(transaction.getDatetime(), result.getDatetime());
        assertEquals(orderDTO, result.getOrder());

        verify(orderMapper).toPaymentOrderRetrievalDTO(order);
    }

    @Test
    void testToTransactionRetrievalDTO_WithNullTransaction() {
        // Act
        TransactionRetrievalDTO result = transactionMapper.toTransactionRetrievalDTO(null);

        // Assert
        assertNull(result);
    }

    @Test
    void testToTransactionResponseDTO_WithNullOrder() {
        // Arrange
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionId("TXN003");
        transaction.setTransactionNo("111222333");
        transaction.setAmount(1500.0);
        transaction.setDatetime(new Date());
        transaction.setOrder(null);

        when(orderMapper.toPaymentOrderResponseFromReturnDTO(null)).thenReturn(null);

        // Act
        TransactionResponseDTO result = transactionMapper.toTransactionResponseDTO(transaction);

        // Assert
        assertNotNull(result);
        assertEquals("TXN003", result.getTransactionId());
        assertEquals("111222333", result.getTransactionNo());
        assertEquals(1500.0, result.getAmount());
        assertEquals(transaction.getDatetime(), result.getDatetime());
        assertNull(result.getOrder());

        verify(orderMapper).toPaymentOrderResponseFromReturnDTO(null);
    }

    @Test
    void testToTransactionRetrievalDTO_WithNullOrder() {
        // Arrange
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionId("TXN004");
        transaction.setTransactionNo("444555666");
        transaction.setAmount(2500.0);
        transaction.setDatetime(new Date());
        transaction.setOrder(null);

        when(orderMapper.toPaymentOrderRetrievalDTO(null)).thenReturn(null);

        // Act
        TransactionRetrievalDTO result = transactionMapper.toTransactionRetrievalDTO(transaction);

        // Assert
        assertNotNull(result);
        assertEquals("TXN004", result.getTransactionId());
        assertEquals("444555666", result.getTransactionNo());
        assertEquals(2500.0, result.getAmount());
        assertEquals(transaction.getDatetime(), result.getDatetime());
        assertNull(result.getOrder());

        verify(orderMapper).toPaymentOrderRetrievalDTO(null);
    }
}