package com.example.aims.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlaceRushOrderServiceTest {

    @BeforeEach
    void setUp() {
        service = new RushOrderService();
    }
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

    @Test
    void testUnsupportedAddress() {
        DeliveryInfo deliveryInfo = createDeliveryInfo("Soc Son"); // not inner city
        List<Product> products = List.of(createProduct("1", true));

        PlaceRushOrderResponse response = service.evaluateRushOrder(deliveryInfo, products);

        assertFalse(response.isSupported());
        assertEquals("Rush order not available. Please update your delivery address or product selection.", response.getPromptMessage());
        assertEquals(1, response.getRushProducts().size());
        assertEquals(0f, response.getRegularFee());
    }

    @Test
    void testNoRushEligibleProducts() {
        DeliveryInfo deliveryInfo = createDeliveryInfo("Ba Dinh"); // supported
        List<Product> products = List.of(createProduct("1", false));

        PlaceRushOrderResponse response = service.evaluateRushOrder(deliveryInfo, products);

        assertFalse(response.isSupported());
        assertEquals("Rush order not available. Please update your delivery address or product selection.", response.getPromptMessage());
        assertEquals(0, response.getRushProducts().size());
        assertEquals(1, response.getRegularProducts().size());
        assertEquals(15000f, response.getRegularFee());
    }

    @Test
    void testMixedProductsAndSupportedAddress() {
        DeliveryInfo deliveryInfo = createDeliveryInfo("Hoan Kiem"); // supported
        List<Product> products = Arrays.asList(
                createProduct("1", true),
                createProduct("2", false)
        );

        PlaceRushOrderResponse response = service.evaluateRushOrder(deliveryInfo, products);

        assertTrue(response.isSupported());
        assertEquals(1, response.getRushProducts().size());
        assertEquals(1, response.getRegularProducts().size());
        assertEquals(20000f, response.getRushFee());
        assertEquals(15000f, response.getRegularFee());
    }

    @Test
    void testAllRushEligibleAndSupportedAddress() {
        DeliveryInfo deliveryInfo = createDeliveryInfo("Dong Da"); // supported
        List<Product> products = List.of(
                createProduct("1", true),
                createProduct("2", true)
        );

        PlaceRushOrderResponse response = service.evaluateRushOrder(deliveryInfo, products);

        assertTrue(response.isSupported());
        assertEquals(2, response.getRushProducts().size());
        assertEquals(0, response.getRegularProducts().size());
        assertEquals(20000f, response.getRushFee());
        assertEquals(0f, response.getRegularFee());
    }

    @Test
    void testEmptyProductList() {
        DeliveryInfo deliveryInfo = createDeliveryInfo("Ba Dinh");
        List<Product> products = Collections.emptyList();

        PlaceRushOrderResponse response = service.evaluateRushOrder(deliveryInfo, products);

        assertFalse(response.isSupported());
        assertEquals(0f, response.getRushFee());
        assertEquals(0f, response.getRegularFee());
    }

}