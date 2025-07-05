package com.example.aims.controller;

import com.example.aims.dto.products.ProductDTO;
import com.example.aims.service.products.ProductService;
import com.example.aims.security.UserDetailsImpl;
import com.example.aims.dto.BulkDeleteRequestDTO;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manager")
@CrossOrigin(origins = "*")
public class ManagerController {

    private final ProductService productService;

    public ManagerController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/products")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Integer managerID = userDetails.getId();

        ProductDTO response = productService.createProduct(productDTO, managerID);
        // Response DTO sẽ có productID được validate theo OnResponse group
        return ResponseEntity.ok(response);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable String id,
            @Valid @RequestBody ProductDTO productDTO) {
        ProductDTO response = productService.updateProduct(id, productDTO);
        // Response DTO sẽ có productID được validate theo OnResponse group
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/products/bulk")
    public ResponseEntity<Void> deleteProducts(@Valid @RequestBody BulkDeleteRequestDTO request) {
        productService.deleteProducts(request.getProductIds());
        return ResponseEntity.noContent().build();
    }
}