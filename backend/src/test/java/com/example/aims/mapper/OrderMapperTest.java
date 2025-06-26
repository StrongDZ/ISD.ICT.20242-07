package com.example.aims.mapper;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.dto.UsersDTO;
import com.example.aims.dto.order.*;
import com.example.aims.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderMapperTest {

    private OrderMapper orderMapper;
    private DeliveryInfoMapper deliveryInfoMapper;
    private UsersMapper usersMapper;

    @BeforeEach
    void setUp() {
        deliveryInfoMapper = mock(DeliveryInfoMapper.class);
        usersMapper = mock(UsersMapper.class);

        orderMapper = new OrderMapper();
        orderMapper.deliveryInfoMapper = deliveryInfoMapper;
        orderMapper.customerMapper = usersMapper;
    }

    @Test
    void testToOrderDTO() {
        Order order = new Order();
        order.setOrderID("ORD002");
        order.setTotalAmount(1000.0);
        order.setStatus(OrderStatus.PENDING);

        Users user = new Users();
        user.setId(101);
        order.setCustomer(user);

        DeliveryInfo deliveryInfo = new DeliveryInfo();
        order.setDeliveryInfo(deliveryInfo);

        DeliveryInfoDTO deliveryInfoDTO = new DeliveryInfoDTO();
        when(deliveryInfoMapper.toDto(deliveryInfo)).thenReturn(deliveryInfoDTO);

        OrderDTO dto = orderMapper.toOrderDTO(order);

        assertEquals(1, dto.getId());
        assertEquals(1000.0, dto.getTotalPrice());
        assertEquals("PAID", dto.getStatus());
        assertEquals(101, dto.getCustomerID());
        assertEquals(deliveryInfoDTO, dto.getDeliveryInfo());
    }

    @Test
    void testToPaymentOrderRequestDTO() {
        Order order = new Order();
        order.setOrderID("ORD002");
        order.setTotalAmount(2000.0);

        Users customer = new Users();
        customer.setGmail("test@gmail.com");
        order.setCustomer(customer);

        PaymentOrderRequestDTO dto = orderMapper.toPaymentOrderRequestDTO(order);

        assertEquals(2, dto.getOrderId());
        assertEquals(2000.0, dto.getAmount());
        assertTrue(dto.getContent().contains("test@gmail.com"));
    }

    @Test
    void testToPaymentOrderResponseFromReturnDTO() {
        Order order = new Order();
        order.setOrderID("ORD003");
        order.setTotalAmount(3000.0);
        order.setStatus(OrderStatus.PENDING);

        DeliveryInfo deliveryInfo = new DeliveryInfo();
        Users customer = new Users();
        order.setDeliveryInfo(deliveryInfo);
        order.setCustomer(customer);

        DeliveryInfoDTO deliveryInfoDTO = new DeliveryInfoDTO();
        UsersDTO customerDTO = new UsersDTO();

        when(deliveryInfoMapper.toDto(deliveryInfo)).thenReturn(deliveryInfoDTO);
        when(usersMapper.toDto(customer)).thenReturn(customerDTO);

        PaymentOrderResponseFromReturnDTO dto = orderMapper.toPaymentOrderResponseFromReturnDTO(order);

        assertEquals(3, dto.getOrderID());
        assertEquals("John Doe", dto.getCustomerName());
        assertEquals("0123456789", dto.getPhoneNumber());
        assertEquals("Hanoi", dto.getShippingAddress());
        assertEquals("HN", dto.getProvince());
        assertEquals(deliveryInfoDTO, dto.getDeliveryInfo());
        assertEquals(customerDTO, dto.getCustomer());
    }

    @Test
    void testToPaymentOrderRetrievalDTO() {
        Order order = new Order();
        order.setOrderID("ORD004");
        order.setTotalAmount(4000.0);
        order.setStatus(OrderStatus.PENDING);

        PaymentOrderRetrievalDTO dto = orderMapper.toPaymentOrderRetrievalDTO(order);

        assertEquals(4, dto.getOrderID());
        assertEquals(4000.0, dto.getTotalAmount());
        assertEquals("DELIVERED", dto.getStatus());
        assertEquals("Jane Doe", dto.getCustomerName());
        assertEquals("0987654321", dto.getPhoneNumber());
        assertEquals("Saigon", dto.getShippingAddress());
        assertEquals("SG", dto.getProvince());
    }
}
