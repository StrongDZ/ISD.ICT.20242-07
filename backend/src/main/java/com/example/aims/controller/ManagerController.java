package com.example.aims.controller;

import com.example.aims.dto.products.ProductDTO;
import com.example.aims.service.products.ProductService;
import com.example.aims.security.UserDetailsImpl;
import com.example.aims.dto.BulkDeleteRequestDTO;
import com.example.aims.dto.DailyLimitsResponseDTO;
import com.example.aims.service.ManagerActivityService;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/manager")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('PRODUCTMANAGER')")
public class ManagerController {

    private final ProductService productService;
    private final ManagerActivityService managerActivityService;

    public ManagerController(ProductService productService, ManagerActivityService managerActivityService) {
        this.productService = productService;
        this.managerActivityService = managerActivityService;
    }

    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Integer managerID = userDetails.getId();
            ProductDTO response = productService.createProduct(productDTO, managerID);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable String id, @Valid @RequestBody ProductDTO productDTO) {
        try {
            ProductDTO response = productService.updateProduct(id, productDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/products/bulk")
    public ResponseEntity<?> deleteProducts(@Valid @RequestBody BulkDeleteRequestDTO request) {
        try {
            productService.deleteProducts(request.getProductIds());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }
    
    @GetMapping("/daily-limits")
    public ResponseEntity<DailyLimitsResponseDTO> getDailyLimits() {
        Integer updateCount = managerActivityService.getDailyUpdateCount();
        Integer deleteCount = managerActivityService.getDailyDeleteCount();
        
        DailyLimitsResponseDTO response = new DailyLimitsResponseDTO(
            updateCount,
            deleteCount,
            Integer.valueOf(30), // update limit
            Integer.valueOf(30), // delete limit
            "Daily limits retrieved successfully"
        );
        
        return ResponseEntity.ok(response);
    }
    
    // Price management endpoints
    @GetMapping("/products/{productId}/price-changes-today")
    public ResponseEntity<Integer> getDailyPriceChangeCount(@PathVariable String productId) {
        try {
            Integer count = productService.getDailyPriceChangeCount(productId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/products/{productId}/old-price")
    public ResponseEntity<Double> getOldPrice(@PathVariable String productId) {
        try {
            Double oldPrice = productService.getOldPrice(productId);
            return ResponseEntity.ok(oldPrice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}