package com.example.aims.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.aims.factory.PaymentErrorMapperFactory;
import com.example.aims.mapper.PaymentError.MomoErrorMapper;
import com.example.aims.mapper.PaymentError.IPaymentErrorMapper;
import com.example.aims.mapper.PaymentError.VNPayErrorMapper;

import static org.junit.jupiter.api.Assertions.*;

class PaymentErrorMapperFactoryTest {

    private PaymentErrorMapperFactory factory;

    @BeforeEach
    void setUp() {
        factory = new PaymentErrorMapperFactory();
    }

    @Test
    void testGetMapper_VNPay() {
        IPaymentErrorMapper mapper = factory.getMapper("vnpay");
        assertNotNull(mapper);
        assertTrue(mapper instanceof VNPayErrorMapper);
        assertEquals("vnpay", mapper.getPaymentType());
    }

    @Test
    void testGetMapper_Momo() {
        IPaymentErrorMapper mapper = factory.getMapper("momo");
        assertNotNull(mapper);
        assertTrue(mapper instanceof MomoErrorMapper);
        assertEquals("momo", mapper.getPaymentType());
    }

    @Test
    void testGetMapper_CaseInsensitive() {
        IPaymentErrorMapper vnpayMapper = factory.getMapper("VNPAY");
        IPaymentErrorMapper momoMapper = factory.getMapper("MOMO");

        assertNotNull(vnpayMapper);
        assertNotNull(momoMapper);
        assertEquals("vnpay", vnpayMapper.getPaymentType());
        assertEquals("momo", momoMapper.getPaymentType());
    }

    @Test
    void testGetMapper_UnsupportedPaymentType() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> factory.getMapper("unsupported"));
        assertEquals("Unsupported payment type: unsupported", exception.getMessage());
    }

    @Test
    void testGetMapper_NullPaymentType() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> factory.getMapper(null));
        assertEquals("Payment type cannot be null", exception.getMessage());
    }

    @Test
    void testGetMapper_EmptyPaymentType() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> factory.getMapper(""));
        assertEquals("Unsupported payment type: ", exception.getMessage());
    }

    @Test
    void testGetMapper_WhitespacePaymentType() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> factory.getMapper("   "));
        assertEquals("Unsupported payment type:    ", exception.getMessage());
    }
}