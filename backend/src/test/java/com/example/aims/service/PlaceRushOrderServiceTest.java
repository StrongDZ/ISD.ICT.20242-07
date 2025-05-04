package com.example.aims.service;

import com.example.aims.dto.PlaceRushOrderResponse;
import com.example.aims.model.DeliveryInfo;
import com.example.aims.model.Product;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

class PlaceRushOrderServiceTest {
    private PlaceRushOrderService service;

    @BeforeEach
    void setUp() {
        service = new PlaceRushOrderService();
    }

    @Test
    public void testRushOrderSupported_AllProductsEligible() {
        DeliveryInfo info = createDeliveryInfo("Ba Dinh");
        List<Product> products = List.of(
                createProduct("1", true),
                createProduct("2", true)
        );

        PlaceRushOrderResponse response = service.evaluateRushOrder(info, products);

        assertTrue(response.isSupported());
        assertEquals(2, response.getRushProducts().size());
        assertEquals(0, response.getRegularProducts().size());
        assertEquals(20000f, response.getRushFee());
        assertEquals(0f, response.getRegularFee());
        assertNull(response.getPromptMessage());
    }

    @Test
    public void testRushOrderSupported_SomeProductsEligible() {
        DeliveryInfo info = createDeliveryInfo("Hoan Kiem");
        List<Product> products = List.of(
                createProduct("1", true),
                createProduct("2", false)
        );

        PlaceRushOrderResponse response = service.evaluateRushOrder(info, products);

        assertTrue(response.isSupported());
        assertEquals(1, response.getRushProducts().size());
        assertEquals(1, response.getRegularProducts().size());
        assertEquals(20000f, response.getRushFee());
        assertEquals(15000f, response.getRegularFee());
        assertNull(response.getPromptMessage());
    }

    @Test
    public void testRushOrderNotSupported_AddressOutsideInnerCity() {
        DeliveryInfo info = createDeliveryInfo("Ha Dong");
        List<Product> products = List.of(
                createProduct("1", true),
                createProduct("2", true)
        );

        PlaceRushOrderResponse response = service.evaluateRushOrder(info, products);

        assertFalse(response.isSupported());
        assertEquals("Rush order not available. Please update your delivery address or product selection.", response.getPromptMessage());
        assertEquals(20000f, response.getRushFee());
        assertEquals(0f, response.getRegularFee());
    }

    @Test
    public void testRushOrderNotSupported_NoEligibleProducts() {
        DeliveryInfo info = createDeliveryInfo("Ba Dinh");
        List<Product> products = List.of(
                createProduct("1", false),
                createProduct("2", false)
        );

        PlaceRushOrderResponse response = service.evaluateRushOrder(info, products);

        assertFalse(response.isSupported());
        assertEquals(0f, response.getRushFee());
        assertEquals(15000f, response.getRegularFee());
        assertEquals("Rush order not available. Please update your delivery address or product selection.", response.getPromptMessage());
    }

    // --- Helper methods ---
    private Product createProduct(String id, boolean rushEligible) {
        Product p = new Product();
        p.setProductID(id);
        p.setRushEligible(rushEligible);
        p.setTitle("Product " + id);
        return p;
    }

    private DeliveryInfo createDeliveryInfo(String province) {
        DeliveryInfo info = new DeliveryInfo();
        info.setProvince(province);
        return info;
    }
}