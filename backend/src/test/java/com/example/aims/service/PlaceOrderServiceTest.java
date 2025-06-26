package com.example.aims.service;

import com.example.aims.dto.DeliveryProductDTO;
import com.example.aims.dto.InvoiceDTO;
import com.example.aims.model.*;
import com.example.aims.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlaceOrderServiceTest {

    @InjectMocks
    private PlaceOrderService placeOrderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private DeliveryInfoRepository deliveryInfoRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // TC1: invoice = null
    @Test
    void testCreateOrder_InvoiceNull_ThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> placeOrderService.createOrder(null));
        assertEquals("Invoice, delivery info, or cart is null", ex.getMessage());
    }

    // TC2: deliveryInfo null
    @Test
    void testCreateOrder_DeliveryInfoNull_ThrowsException() {
        InvoiceDTO invoice = new InvoiceDTO();
        invoice.setDeliveryInfo(null);
        invoice.setCart(new DeliveryProductDTO[0]);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> placeOrderService.createOrder(invoice));
        assertEquals("Invoice, delivery info, or cart is null", ex.getMessage());
    }

    // TC3: cart null
    @Test
    void testCreateOrder_CartNull_ThrowsException() {
        InvoiceDTO invoice = new InvoiceDTO();
        invoice.setDeliveryInfo(new DeliveryInfo());
        invoice.setCart(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> placeOrderService.createOrder(invoice));
        assertEquals("Invoice, delivery info, or cart is null", ex.getMessage());
    }

    // TC4: Tạo đơn thành công
    @Test
    void testCreateOrder_Success() {
        // Mock input
        DeliveryInfo deliveryInfo = new DeliveryInfo();
        DeliveryProductDTO productDTO = new DeliveryProductDTO();
        productDTO.setId("p1");
        productDTO.setQuantity(2);

        InvoiceDTO invoice = new InvoiceDTO();
        invoice.setCart(new DeliveryProductDTO[] { productDTO });
        invoice.setDeliveryInfo(deliveryInfo);
        invoice.setSubtotal(100);
        invoice.setDeliveryFee(20);
        invoice.setVat(10);

        Book product = new Book();
        product.setProductID("p1");
        product.setCategory("book");
        product.setTitle("Test Product");
        product.setPrice(19.99);
        product.setImageURL("http://example.com/image.jpg");
        product.setRushEligible(true);

        // Mock behavior
        when(deliveryInfoRepository.save(deliveryInfo)).thenReturn(deliveryInfo);
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call method
        Order result = placeOrderService.createOrder(invoice);

        // Assert
        assertNotNull(result);
        assertEquals(130.0, result.getTotalAmount());
    }

    // TC5: Product not found
    @Test
    void testCreateOrder_ProductNotFound_ThrowsException() {
        DeliveryProductDTO productDTO = new DeliveryProductDTO();
        productDTO.setId("invalid-id");
        productDTO.setQuantity(1);

        InvoiceDTO invoice = new InvoiceDTO();
        invoice.setCart(new DeliveryProductDTO[] { productDTO });
        invoice.setDeliveryInfo(new DeliveryInfo());
        invoice.setSubtotal(50);
        invoice.setDeliveryFee(10);
        invoice.setVat(5);

        when(deliveryInfoRepository.save(any())).thenReturn(new DeliveryInfo());
        when(productRepository.findById("invalid-id")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> placeOrderService.createOrder(invoice));
        assertEquals("An error occurred: Product not found with id: invalid-id", ex.getMessage());
    }

    // TC6: Lỗi truy cập DB khi save Order
    @Test
    void testCreateOrder_OrderRepoThrowsException() {
        DeliveryInfo deliveryInfo = new DeliveryInfo();
        DeliveryProductDTO productDTO = new DeliveryProductDTO();
        productDTO.setId("p1");
        productDTO.setQuantity(1);

        InvoiceDTO invoice = new InvoiceDTO();
        invoice.setCart(new DeliveryProductDTO[] { productDTO });
        invoice.setDeliveryInfo(deliveryInfo);
        invoice.setSubtotal(100);
        invoice.setDeliveryFee(20);
        invoice.setVat(10);

        Book product = new Book();
        product.setProductID("p1");

        when(deliveryInfoRepository.save(any())).thenReturn(deliveryInfo);
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        when(orderRepository.save(any())).thenThrow(new RuntimeException("Simulated DB failure"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> placeOrderService.createOrder(invoice));
        assertEquals("An error occurred: Simulated DB failure", ex.getMessage());
    }
}