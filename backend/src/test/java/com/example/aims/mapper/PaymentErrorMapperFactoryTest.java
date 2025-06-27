package com.example.aims.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.aims.factory.PaymentErrorMapperFactory;
import com.example.aims.mapper.PaymentError.MomoErrorMapper;
import com.example.aims.mapper.PaymentError.IPaymentErrorMapper;
import com.example.aims.mapper.PaymentError.VNPayErrorMapper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

class PaymentErrorMapperFactoryTest {

    private PaymentErrorMapperFactory factory;
    private VNPayErrorMapper vnpayMapper;
    private MomoErrorMapper momoMapper;

    @BeforeEach
    void setUp() {
        vnpayMapper = new VNPayErrorMapper();
        momoMapper = new MomoErrorMapper();
        List<IPaymentErrorMapper> mappers = Arrays.asList(vnpayMapper, momoMapper);
        factory = new PaymentErrorMapperFactory(mappers);
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
        assertEquals("No error mapper found for payment type: unsupported", exception.getMessage());
    }

    @Test
    void testGetSupportedPaymentTypes() {
        List<String> supportedTypes = factory.getSupportedPaymentTypes();
        assertNotNull(supportedTypes);
        assertEquals(2, supportedTypes.size());
        assertTrue(supportedTypes.contains("vnpay"));
        assertTrue(supportedTypes.contains("momo"));
    }

    @Test
    void testIsSupported() {
        assertTrue(factory.isSupported("vnpay"));
        assertTrue(factory.isSupported("momo"));
        assertTrue(factory.isSupported("VNPAY"));
        assertTrue(factory.isSupported("MOMO"));
        assertFalse(factory.isSupported("unsupported"));
        assertFalse(factory.isSupported("paypal"));
    }
}