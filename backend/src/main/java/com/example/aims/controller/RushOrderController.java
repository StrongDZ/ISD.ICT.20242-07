package com.example.aims.controller;

import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.dto.PlaceRushOrderResponse;
import com.example.aims.model.Product;
import com.example.aims.service.rush.PlaceRushOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rush-order")
public class RushOrderController {
    private final PlaceRushOrderService placeRushOrderService;

    public RushOrderController(PlaceRushOrderService placeRushOrderService) {
        this.placeRushOrderService = placeRushOrderService;
    }

    @PostMapping("/check")
    public ResponseEntity<PlaceRushOrderResponse> checkRushOrder(@RequestBody RushOrderCheckRequest request) {
        PlaceRushOrderResponse response = placeRushOrderService.placeRushOrder(request.getDeliveryInfo(), request.getProducts());
        return ResponseEntity.ok(response);
    }

    // Inner static class for request body
    public static class RushOrderCheckRequest {
        private DeliveryInfoDTO deliveryInfo;
        private List<Product> products;

        public DeliveryInfoDTO getDeliveryInfo() {
            return deliveryInfo;
        }
        public void setDeliveryInfo(DeliveryInfoDTO deliveryInfo) {
            this.deliveryInfo = deliveryInfo;
        }
        public List<Product> getProducts() {
            return products;
        }
        public void setProducts(List<Product> products) {
            this.products = products;
        }
    }
} 