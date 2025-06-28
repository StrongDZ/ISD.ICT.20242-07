package com.example.aims.controller;

import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.dto.PlaceRushOrderResponse;
import com.example.aims.dto.products.ProductDTO;
import com.example.aims.dto.rush.RushOrderCheckRequest;
import com.example.aims.service.rush.PlaceRushOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ✅ Controller for handling rush order operations.
 * ✅ High Cohesion: All methods relate to rush order functionality.
 * ✅ Follows SRP: This class only handles rush order HTTP requests.
 *
 * SOLID Evaluation:
 * - S (Single Responsibility Principle): Focused solely on rush order HTTP operations.
 * - O (Open/Closed Principle): Can be extended with new endpoints without modifying existing ones.
 * - L (Liskov Substitution Principle): Can be substituted with other controllers implementing same interface.
 * - I (Interface Segregation Principle): Not applicable; no interfaces implemented here.
 * - D (Dependency Inversion Principle): Depends on service abstraction, not concrete implementations.
 */
@RestController
@RequestMapping("/api/rush-order")
public class RushOrderController {
    
    private final PlaceRushOrderService placeRushOrderService;

    public RushOrderController(PlaceRushOrderService placeRushOrderService) {
        this.placeRushOrderService = placeRushOrderService;
    }

    /**
     * Checks if a rush order is eligible based on delivery info and products.
     *
     * @param request the rush order check request containing delivery info and products
     * @return ResponseEntity containing the rush order eligibility response
     */
    @PostMapping("/check")
    public ResponseEntity<PlaceRushOrderResponse> checkRushOrder(@RequestBody RushOrderCheckRequest request) {
        PlaceRushOrderResponse response = placeRushOrderService.placeRushOrder(request.getDeliveryInfo(), request.getProducts());
        return ResponseEntity.ok(response);
    }
} 