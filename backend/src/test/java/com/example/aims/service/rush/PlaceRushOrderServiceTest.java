package com.example.aims.service.rush;

import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.dto.PlaceRushOrderResponse;
import com.example.aims.dto.products.ProductDTO;
import com.example.aims.service.rush.eligibility.RushEligibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlaceRushOrderServiceTest {

    private RushEligibility<DeliveryInfoDTO> addressEligibility;
    private RushEligibility<ProductDTO> productEligibility;
    private PlaceRushOrderService service;

    @BeforeEach
    void setUp() {
        addressEligibility = mock(RushEligibility.class);
        productEligibility = mock(RushEligibility.class);
        service = new PlaceRushOrderService(addressEligibility, productEligibility);
    }

    @Test
    void testRushOrder_Supported_AllEligible() {
        DeliveryInfoDTO deliveryInfo = new DeliveryInfoDTO();
        ProductDTO product1 = mock(ProductDTO.class);
        ProductDTO product2 = mock(ProductDTO.class);
        List<ProductDTO> products = Arrays.asList(product1, product2);

        when(addressEligibility.isRushAllowed(deliveryInfo)).thenReturn(true);
        when(productEligibility.isRushAllowed(product1)).thenReturn(true);
        when(productEligibility.isRushAllowed(product2)).thenReturn(true);

        PlaceRushOrderResponse response = service.placeRushOrder(deliveryInfo, products);

        assertTrue(response.isSupported());
        assertEquals(2, response.getRushProducts().size());
        assertEquals(0, response.getRegularProducts().size());
        assertNull(response.getPromptMessage());
    }

    @Test
    void testRushOrder_Supported_SomeEligible() {
        DeliveryInfoDTO deliveryInfo = new DeliveryInfoDTO();
        ProductDTO product1 = mock(ProductDTO.class);
        ProductDTO product2 = mock(ProductDTO.class);
        List<ProductDTO> products = Arrays.asList(product1, product2);

        when(addressEligibility.isRushAllowed(deliveryInfo)).thenReturn(true);
        when(productEligibility.isRushAllowed(product1)).thenReturn(true);
        when(productEligibility.isRushAllowed(product2)).thenReturn(false);

        PlaceRushOrderResponse response = service.placeRushOrder(deliveryInfo, products);

        assertTrue(response.isSupported());
        assertEquals(1, response.getRushProducts().size());
        assertEquals(1, response.getRegularProducts().size());
        assertNull(response.getPromptMessage());
    }

    @Test
    void testRushOrder_NotSupported_AddressNotEligible() {
        DeliveryInfoDTO deliveryInfo = new DeliveryInfoDTO();
        ProductDTO product1 = mock(ProductDTO.class);
        List<ProductDTO> products = Collections.singletonList(product1);

        when(addressEligibility.isRushAllowed(deliveryInfo)).thenReturn(false);
        when(productEligibility.isRushAllowed(product1)).thenReturn(true);

        PlaceRushOrderResponse response = service.placeRushOrder(deliveryInfo, products);

        assertFalse(response.isSupported());
        assertEquals(1, response.getRushProducts().size());
        assertEquals(0, response.getRegularProducts().size());
        assertNotNull(response.getPromptMessage());
    }

    @Test
    void testRushOrder_NotSupported_NoRushableProduct() {
        DeliveryInfoDTO deliveryInfo = new DeliveryInfoDTO();
        ProductDTO product1 = mock(ProductDTO.class);
        ProductDTO product2 = mock(ProductDTO.class);
        List<ProductDTO> products = Arrays.asList(product1, product2);

        when(addressEligibility.isRushAllowed(deliveryInfo)).thenReturn(true);
        when(productEligibility.isRushAllowed(product1)).thenReturn(false);
        when(productEligibility.isRushAllowed(product2)).thenReturn(false);

        PlaceRushOrderResponse response = service.placeRushOrder(deliveryInfo, products);

        assertFalse(response.isSupported());
        assertEquals(0, response.getRushProducts().size());
        assertEquals(2, response.getRegularProducts().size());
        assertNotNull(response.getPromptMessage());
    }

    @Test
    void testRushOrder_NotSupported_AddressAndNoRushableProduct() {
        DeliveryInfoDTO deliveryInfo = new DeliveryInfoDTO();
        ProductDTO product1 = mock(ProductDTO.class);
        List<ProductDTO> products = Collections.singletonList(product1);

        when(addressEligibility.isRushAllowed(deliveryInfo)).thenReturn(false);
        when(productEligibility.isRushAllowed(product1)).thenReturn(false);

        PlaceRushOrderResponse response = service.placeRushOrder(deliveryInfo, products);

        assertFalse(response.isSupported());
        assertEquals(0, response.getRushProducts().size());
        assertEquals(1, response.getRegularProducts().size());
        assertNotNull(response.getPromptMessage());
    }

    @Test
    void testRushOrder_EmptyProductList() {
        DeliveryInfoDTO deliveryInfo = new DeliveryInfoDTO();
        List<ProductDTO> products = Collections.emptyList();

        when(addressEligibility.isRushAllowed(deliveryInfo)).thenReturn(true);

        PlaceRushOrderResponse response = service.placeRushOrder(deliveryInfo, products);

        assertFalse(response.isSupported());
        assertEquals(0, response.getRushProducts().size());
        assertEquals(0, response.getRegularProducts().size());
        assertNotNull(response.getPromptMessage());
    }
}