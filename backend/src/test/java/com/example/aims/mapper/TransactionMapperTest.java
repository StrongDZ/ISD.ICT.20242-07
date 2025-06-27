package com.example.aims.mapper;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.order.PaymentOrderResponseFromReturnDTO;
import com.example.aims.dto.transaction.TransactionResponseDTO;
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

        Order order = new Order();
        order.setOrderID("ORD001");
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(1000.0);
        transaction.setOrder(order);

        PaymentOrderResponseFromReturnDTO orderDTO = new PaymentOrderResponseFromReturnDTO();
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
}