package com.example.aims.service;

import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.dto.PlaceRushOrderResponse;
import com.example.aims.model.Product;
import com.example.aims.subsystem.rush.eligibility.AddressRushEligibility;
import com.example.aims.subsystem.rush.eligibility.ProductRushEligibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlaceRushOrderServiceTest {

    private AddressRushEligibility addressEligibility;
    private ProductRushEligibility productEligibility;
    private PlaceRushOrderService service;

    @BeforeEach
    void setUp() {
        addressEligibility = mock(AddressRushEligibility.class);
        productEligibility = mock(ProductRushEligibility.class);
        service = new PlaceRushOrderService(addressEligibility, productEligibility);
    }

    @Test
    void testAddressNotEligible_ShouldReturnNotSupported() {
        // Given
        DeliveryInfoDTO deliveryInfo = new DeliveryInfoDTO("HCM", "Quan 1", "123 Le Loi", "0123456789");
        List<Product> products = Arrays.asList(new Product(), new Product());

        when(addressEligibility.isRushAllowed(deliveryInfo)).thenReturn(false);

        // When
        PlaceRushOrderResponse response = service.placeRushOrder(deliveryInfo, products);

        // Then
        assertFalse(response.isSupported());
        assertEquals(0, response.getRushProducts().size());
        assertEquals(2, response.getRegularProducts().size());
        assertNotNull(response.getPromptMessage());
    }

    @Test
    void testAddressEligibleButNoRushProduct_ShouldReturnNotSupported() {
        // Given
        DeliveryInfoDTO deliveryInfo = new DeliveryInfoDTO("Ha Noi", "Ba Dinh", "12 Kim Ma", "0909090909");
        List<Product> products = Arrays.asList(new Product(), new Product());

        when(addressEligibility.isRushAllowed(deliveryInfo)).thenReturn(true);
        when(productEligibility.isRushAllowed(any())).thenReturn(false);

        // When
        PlaceRushOrderResponse response = service.placeRushOrder(deliveryInfo, products);

        // Then
        assertFalse(response.isSupported());
        assertEquals(0, response.getRushProducts().size());
        assertEquals(2, response.getRegularProducts().size());
        assertNotNull(response.getPromptMessage());
    }

    @Test
    void testSomeProductsEligible_ShouldReturnSupportedWithSplit() {
        // Given
        DeliveryInfoDTO deliveryInfo = new DeliveryInfoDTO("Ha Noi", "Dong Da", "123 Ton Duc Thang", "0988888888");
        Product rushProduct = new Product();
        Product regularProduct = new Product();

        List<Product> products = Arrays.asList(rushProduct, regularProduct);

        when(addressEligibility.isRushAllowed(deliveryInfo)).thenReturn(true);
        when(productEligibility.isRushAllowed(rushProduct)).thenReturn(true);
        when(productEligibility.isRushAllowed(regularProduct)).thenReturn(false);

        // When
        PlaceRushOrderResponse response = service.placeRushOrder(deliveryInfo, products);

        // Then
        assertTrue(response.isSupported());
        assertEquals(1, response.getRushProducts().size());
        assertEquals(1, response.getRegularProducts().size());
        assertNull(response.getPromptMessage());
    }

    @Test
    void testAllProductsEligible_ShouldReturnSupportedOnlyRush() {
        // Given
        DeliveryInfoDTO deliveryInfo = new DeliveryInfoDTO("Ha Noi", "Hoan Kiem", "45 Hang Dao", "0912121212");
        Product product1 = new Product();
        Product product2 = new Product();

        List<Product> products = Arrays.asList(product1, product2);

        when(addressEligibility.isRushAllowed(deliveryInfo)).thenReturn(true);
        when(productEligibility.isRushAllowed(product1)).thenReturn(true);
        when(productEligibility.isRushAllowed(product2)).thenReturn(true);

        // When
        PlaceRushOrderResponse response = service.placeRushOrder(deliveryInfo, products);

        // Then
        assertTrue(response.isSupported());
        assertEquals(2, response.getRushProducts().size());
        assertEquals(0, response.getRegularProducts().size());
        assertNull(response.getPromptMessage());
    }

    @Test
    void testEmptyProductList_ShouldReturnNotSupported() {
        // Given
        DeliveryInfoDTO deliveryInfo = new DeliveryInfoDTO("Ha Noi", "Hoan Kiem", "45 Hang Dao", "0912121212");
        List<Product> products = Collections.emptyList();

        when(addressEligibility.isRushAllowed(deliveryInfo)).thenReturn(true);

        // When
        PlaceRushOrderResponse response = service.placeRushOrder(deliveryInfo, products);

        // Then
        assertFalse(response.isSupported());
        assertEquals(0, response.getRushProducts().size());
        assertEquals(0, response.getRegularProducts().size());
        assertNotNull(response.getPromptMessage());
    }
}