package com.example.aims.service.rush;

import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.dto.PlaceRushOrderResponse;
import com.example.aims.dto.products.BookDTO;
import com.example.aims.dto.products.ProductDTO;
import com.example.aims.dto.rush.RushOrderCheckRequest;
import com.example.aims.service.rush.eligibility.AddressRushEligibility;
import com.example.aims.service.rush.eligibility.ProductRushEligibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PlaceRushOrderServiceTest {

    @Mock
    private AddressRushEligibility addressEligibility;

    @Mock
    private ProductRushEligibility productEligibility;

    private PlaceRushOrderService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PlaceRushOrderService(addressEligibility, productEligibility);
    }

    @Test
    void testPlaceRushOrder_WithNullRushEligibleField() {
        // Arrange
        DeliveryInfoDTO deliveryInfo = new DeliveryInfoDTO();
        deliveryInfo.setCity("Ha Noi");
        deliveryInfo.setDistrict("Ba Dinh");

        BookDTO product1 = new BookDTO();
        product1.setProductID("B001");
        product1.setRushEligible(null); // Null rushEligible field

        BookDTO product2 = new BookDTO();
        product2.setProductID("B002");
        product2.setRushEligible(true);

        List<ProductDTO> products = Arrays.asList(product1, product2);

        when(addressEligibility.isRushAllowed(any(DeliveryInfoDTO.class))).thenReturn(true);
        when(productEligibility.isRushAllowed(product1)).thenReturn(false); // null should be treated as false
        when(productEligibility.isRushAllowed(product2)).thenReturn(true);

        // Act
        PlaceRushOrderResponse response = service.placeRushOrder(deliveryInfo, products);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSupported()); // Should be supported because product2 is eligible
        assertEquals(1, response.getRushProducts().size());
        assertEquals(1, response.getRegularProducts().size());
        assertEquals("B002", response.getRushProducts().get(0).getProductID());
        assertEquals("B001", response.getRegularProducts().get(0).getProductID());
    }

    @Test
    void testPlaceRushOrder_AddressNotEligible() {
        // Arrange
        DeliveryInfoDTO deliveryInfo = new DeliveryInfoDTO();
        deliveryInfo.setCity("Ho Chi Minh");
        deliveryInfo.setDistrict("District 1");

        BookDTO product = new BookDTO();
        product.setProductID("B001");
        product.setRushEligible(true);

        List<ProductDTO> products = Arrays.asList(product);

        when(addressEligibility.isRushAllowed(any(DeliveryInfoDTO.class))).thenReturn(false);
        when(productEligibility.isRushAllowed(any(ProductDTO.class))).thenReturn(true);

        // Act
        PlaceRushOrderResponse response = service.placeRushOrder(deliveryInfo, products);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSupported());
        assertEquals(0, response.getRushProducts().size());
        assertEquals(1, response.getRegularProducts().size());
        assertNotNull(response.getPromptMessage());
    }

    @Test
    void testPlaceRushOrder_NoRushEligibleProducts() {
        // Arrange
        DeliveryInfoDTO deliveryInfo = new DeliveryInfoDTO();
        deliveryInfo.setCity("Ha Noi");
        deliveryInfo.setDistrict("Ba Dinh");

        BookDTO product1 = new BookDTO();
        product1.setProductID("B001");
        product1.setRushEligible(false);

        BookDTO product2 = new BookDTO();
        product2.setProductID("B002");
        product2.setRushEligible(false);

        List<ProductDTO> products = Arrays.asList(product1, product2);

        when(addressEligibility.isRushAllowed(any(DeliveryInfoDTO.class))).thenReturn(true);
        when(productEligibility.isRushAllowed(any(ProductDTO.class))).thenReturn(false);

        // Act
        PlaceRushOrderResponse response = service.placeRushOrder(deliveryInfo, products);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSupported());
        assertEquals(0, response.getRushProducts().size());
        assertEquals(2, response.getRegularProducts().size());
        assertNotNull(response.getPromptMessage());
    }

    @Test
    void testPlaceRushOrder_SuccessfulRushOrder() {
        // Arrange
        DeliveryInfoDTO deliveryInfo = new DeliveryInfoDTO();
        deliveryInfo.setCity("Ha Noi");
        deliveryInfo.setDistrict("Ba Dinh");

        BookDTO product1 = new BookDTO();
        product1.setProductID("B001");
        product1.setRushEligible(true);

        BookDTO product2 = new BookDTO();
        product2.setProductID("B002");
        product2.setRushEligible(false);

        List<ProductDTO> products = Arrays.asList(product1, product2);

        when(addressEligibility.isRushAllowed(any(DeliveryInfoDTO.class))).thenReturn(true);
        when(productEligibility.isRushAllowed(product1)).thenReturn(true);
        when(productEligibility.isRushAllowed(product2)).thenReturn(false);

        // Act
        PlaceRushOrderResponse response = service.placeRushOrder(deliveryInfo, products);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSupported());
        assertEquals(1, response.getRushProducts().size());
        assertEquals(1, response.getRegularProducts().size());
        assertEquals("B001", response.getRushProducts().get(0).getProductID());
        assertEquals("B002", response.getRegularProducts().get(0).getProductID());
    }

    @Test
    void testRushOrderCheckRequest_WithValidData() {
        // Arrange
        DeliveryInfoDTO deliveryInfo = new DeliveryInfoDTO();
        deliveryInfo.setCity("Ha Noi");
        deliveryInfo.setDistrict("Ba Dinh");

        BookDTO product = new BookDTO();
        product.setProductID("B001");
        product.setRushEligible(true);

        List<ProductDTO> products = Arrays.asList(product);

        RushOrderCheckRequest request = new RushOrderCheckRequest(deliveryInfo, products);

        // Act & Assert
        assertNotNull(request);
        assertEquals(deliveryInfo, request.getDeliveryInfo());
        assertEquals(products, request.getProducts());
        assertEquals(1, request.getProducts().size());
        assertEquals("B001", request.getProducts().get(0).getProductID());
    }

    @Test
    void testRushOrderCheckRequest_WithNullData() {
        // Arrange
        RushOrderCheckRequest request = new RushOrderCheckRequest();

        // Act & Assert
        assertNotNull(request);
        assertNull(request.getDeliveryInfo());
        assertNull(request.getProducts());
    }
}