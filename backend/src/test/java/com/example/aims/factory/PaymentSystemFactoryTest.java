package com.example.aims.factory;

import com.example.aims.subsystem.IPaymentSystem;
import com.example.aims.subsystem.Momo.MomoSubsystem;
import com.example.aims.subsystem.VNPay.VNPaySubsystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentSystemFactoryTest {

    private PaymentSystemFactory factory;
    private VNPaySubsystem vnpaySystem;
    private MomoSubsystem momoSystem;

    @BeforeEach
    void setUp() {
        vnpaySystem = new VNPaySubsystem();
        momoSystem = new MomoSubsystem();
        List<IPaymentSystem> paymentSystems = Arrays.asList(vnpaySystem, momoSystem);
        factory = new PaymentSystemFactory(paymentSystems);
    }

    @Test
    void testGetPaymentSystem_VNPay() {
        IPaymentSystem paymentSystem = factory.getPaymentSystem("vnpay");
        assertNotNull(paymentSystem);
        assertTrue(paymentSystem instanceof VNPaySubsystem);
        assertEquals("vnpay", paymentSystem.getPaymentType());
    }

    @Test
    void testGetPaymentSystem_Momo() {
        IPaymentSystem paymentSystem = factory.getPaymentSystem("momo");
        assertNotNull(paymentSystem);
        assertTrue(paymentSystem instanceof MomoSubsystem);
        assertEquals("momo", paymentSystem.getPaymentType());
    }

    @Test
    void testGetPaymentSystem_CaseInsensitive() {
        IPaymentSystem vnpaySystem = factory.getPaymentSystem("VNPAY");
        IPaymentSystem momoSystem = factory.getPaymentSystem("MOMO");

        assertNotNull(vnpaySystem);
        assertNotNull(momoSystem);
        assertEquals("vnpay", vnpaySystem.getPaymentType());
        assertEquals("momo", momoSystem.getPaymentType());
    }

    @Test
    void testGetPaymentSystem_UnsupportedPaymentType() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> factory.getPaymentSystem("unsupported"));
        assertTrue(exception.getMessage().contains("Unsupported payment type"));
        assertTrue(exception.getMessage().contains("vnpay"));
        assertTrue(exception.getMessage().contains("momo"));
    }

    @Test
    void testGetPaymentSystem_NullPaymentType() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> factory.getPaymentSystem(null));
        assertEquals("Payment type cannot be null", exception.getMessage());
    }

    @Test
    void testGetSupportedPaymentTypes() {
        List<String> supportedTypes = factory.getSupportedPaymentTypes();
        assertNotNull(supportedTypes);
        assertEquals(2, supportedTypes.size());
        assertTrue(supportedTypes.contains("momo"));
        assertTrue(supportedTypes.contains("vnpay"));
        // Should be sorted
        assertEquals("momo", supportedTypes.get(0));
        assertEquals("vnpay", supportedTypes.get(1));
    }

    @Test
    void testIsSupported() {
        assertTrue(factory.isSupported("vnpay"));
        assertTrue(factory.isSupported("momo"));
        assertTrue(factory.isSupported("VNPAY"));
        assertTrue(factory.isSupported("MOMO"));
        assertFalse(factory.isSupported("unsupported"));
        assertFalse(factory.isSupported("paypal"));
        assertFalse(factory.isSupported(null));
    }

    @Test
    void testConstructor_WithEmptyList() {
        List<IPaymentSystem> emptySystems = Arrays.asList();
        PaymentSystemFactory emptyFactory = new PaymentSystemFactory(emptySystems);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> emptyFactory.getPaymentSystem("vnpay"));
        assertTrue(exception.getMessage().contains("Unsupported payment type"));
    }
}