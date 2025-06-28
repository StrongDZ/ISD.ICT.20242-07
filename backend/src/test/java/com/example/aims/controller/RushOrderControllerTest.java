package com.example.aims.controller;

import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.dto.PlaceRushOrderResponse;
import com.example.aims.dto.products.BookDTO;
import com.example.aims.dto.products.ProductDTO;
import com.example.aims.dto.rush.RushOrderCheckRequest;
import com.example.aims.service.rush.PlaceRushOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * ✅ Test class for RushOrderController.
 * ✅ Tests the controller's responsibility of handling rush order HTTP requests.
 */
class RushOrderControllerTest {

    @Mock
    private PlaceRushOrderService placeRushOrderService;

    private RushOrderController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new RushOrderController(placeRushOrderService);
    }

    @Test
    void testCheckRushOrder_Success() {
        // Arrange
        DeliveryInfoDTO deliveryInfo = new DeliveryInfoDTO();
        deliveryInfo.setCity("Ha Noi");
        deliveryInfo.setDistrict("Ba Dinh");

        BookDTO product = new BookDTO();
        product.setProductID("B001");
        product.setRushEligible(true);

        List<ProductDTO> products = Arrays.asList(product);

        RushOrderCheckRequest request = new RushOrderCheckRequest();
        request.setDeliveryInfo(deliveryInfo);
        request.setProducts(products);

        PlaceRushOrderResponse expectedResponse = new PlaceRushOrderResponse();
        expectedResponse.setSupported(true);
        expectedResponse.setRushProducts(products);
        expectedResponse.setRegularProducts(Arrays.asList());

        when(placeRushOrderService.placeRushOrder(any(DeliveryInfoDTO.class), any(List.class)))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<PlaceRushOrderResponse> response = controller.checkRushOrder(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSupported());
        assertEquals(1, response.getBody().getRushProducts().size());
        assertEquals(0, response.getBody().getRegularProducts().size());
    }

    @Test
    void testCheckRushOrder_NotSupported() {
        // Arrange
        DeliveryInfoDTO deliveryInfo = new DeliveryInfoDTO();
        deliveryInfo.setCity("Ho Chi Minh");
        deliveryInfo.setDistrict("District 1");

        BookDTO product = new BookDTO();
        product.setProductID("B001");
        product.setRushEligible(false);

        List<ProductDTO> products = Arrays.asList(product);

        RushOrderCheckRequest request = new RushOrderCheckRequest();
        request.setDeliveryInfo(deliveryInfo);
        request.setProducts(products);

        PlaceRushOrderResponse expectedResponse = new PlaceRushOrderResponse();
        expectedResponse.setSupported(false);
        expectedResponse.setRushProducts(Arrays.asList());
        expectedResponse.setRegularProducts(products);
        expectedResponse.setPromptMessage("Rush order is not available. Please update delivery address or product selection.");

        when(placeRushOrderService.placeRushOrder(any(DeliveryInfoDTO.class), any(List.class)))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<PlaceRushOrderResponse> response = controller.checkRushOrder(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSupported());
        assertEquals(0, response.getBody().getRushProducts().size());
        assertEquals(1, response.getBody().getRegularProducts().size());
        assertNotNull(response.getBody().getPromptMessage());
    }

    @Test
    void testCheckRushOrder_WithNullRequest() {
        // Arrange
        RushOrderCheckRequest request = new RushOrderCheckRequest();
        request.setDeliveryInfo(null);
        request.setProducts(null);

        PlaceRushOrderResponse expectedResponse = new PlaceRushOrderResponse();
        expectedResponse.setSupported(false);
        expectedResponse.setRushProducts(Arrays.asList());
        expectedResponse.setRegularProducts(Arrays.asList());
        expectedResponse.setPromptMessage("Rush order is not available. Please update delivery address or product selection.");

        when(placeRushOrderService.placeRushOrder(null, null))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<PlaceRushOrderResponse> response = controller.checkRushOrder(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSupported());
    }
} 