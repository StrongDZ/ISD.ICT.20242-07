package com.example.aims.mapper;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.dto.order.*;
import com.example.aims.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderMapperTest {

    private OrderMapper orderMapper;
    private DeliveryInfoMapper deliveryInfoMapper;

    @BeforeEach
    void setUp() {
        deliveryInfoMapper = mock(DeliveryInfoMapper.class);

        orderMapper = new OrderMapper();
        orderMapper.deliveryInfoMapper = deliveryInfoMapper;
    }

    @Test
    void testToOrderDTO() {
        Order order = new Order();
        order.setOrderID("ORD002");
        order.setTotalAmount(1000.0);
        order.setStatus(OrderStatus.PENDING);

        DeliveryInfo deliveryInfo = new DeliveryInfo();
        order.setDeliveryInfo(deliveryInfo);

        DeliveryInfoDTO deliveryInfoDTO = new DeliveryInfoDTO();
        when(deliveryInfoMapper.toDto(deliveryInfo)).thenReturn(deliveryInfoDTO);

        OrderDTO dto = orderMapper.toOrderDTO(order);

        assertEquals("ORD002", dto.getId());
        assertEquals(1000.0, dto.getTotalPrice());
        assertEquals(OrderStatus.PENDING, dto.getStatus());
        assertEquals(deliveryInfoDTO, dto.getDeliveryInfo());
    }

    @Test
    void testToPaymentOrderRequestDTO() {
        Order order = new Order();
        order.setOrderID("ORD002");
        order.setTotalAmount(2000.0);

        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.setMail("test@example.com");
        order.setDeliveryInfo(deliveryInfo);

        PaymentOrderRequestDTO dto = orderMapper.toPaymentOrderRequestDTO(order);

        assertEquals("ORD002", dto.getOrderId());
        assertEquals(2000.0, dto.getAmount());
        assertTrue(dto.getContent().contains("ORD002"));
        assertTrue(dto.getContent().contains("test@example.com"));
    }

    @Test
    void testToPaymentOrderResponseFromReturnDTO() {
        Order order = new Order();
        order.setOrderID("ORD004");
        order.setTotalAmount(4000.0);
        order.setStatus(OrderStatus.PENDING);

        DeliveryInfo deliveryInfo = new DeliveryInfo();
        order.setDeliveryInfo(deliveryInfo);

        DeliveryInfoDTO deliveryInfoDTO = new DeliveryInfoDTO();
        when(deliveryInfoMapper.toDto(deliveryInfo)).thenReturn(deliveryInfoDTO);

        PaymentOrderResponseFromReturnDTO dto = orderMapper.toPaymentOrderResponseFromReturnDTO(order);

        assertEquals("ORD004", dto.getOrderID());
        assertEquals(4000.0, dto.getTotalAmount());
        assertEquals(OrderStatus.PENDING, dto.getStatus());
        assertEquals(deliveryInfoDTO, dto.getDeliveryInfo());
    }

    @Test
    void testToPaymentOrderResponseFromReturnDTOWithNullOrder() {
        PaymentOrderResponseFromReturnDTO dto = orderMapper.toPaymentOrderResponseFromReturnDTO(null);
        assertNull(dto);
    }

    @Test
    void testToOrderDTOWithNullDeliveryInfo() {
        Order order = new Order();
        order.setOrderID("ORD005");
        order.setTotalAmount(5000.0);
        order.setStatus(OrderStatus.PENDING);
        order.setDeliveryInfo(null);

        OrderDTO dto = orderMapper.toOrderDTO(order);

        assertEquals("ORD005", dto.getId());
        assertEquals(5000.0, dto.getTotalPrice());
        assertEquals(OrderStatus.PENDING, dto.getStatus());
        assertNull(dto.getDeliveryInfo());
    }
}
