package com.example.aims.controller;

import com.example.aims.dto.products.ProductDTO;
import com.example.aims.dto.PagedResponse;
import com.example.aims.service.products.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
@Tag(name = "Product", description = "Product management APIs")
public class ViewProductController {

    private final ProductService productService;

    public ViewProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get all products", description = "Retrieves a list of all available products with optional pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of products", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<?> getAllProducts(
            @Parameter(description = "Page number (0-based)") @RequestParam(required = false) Integer page,
            @Parameter(description = "Page size") @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            PagedResponse<ProductDTO> pagedProducts = productService.getAllProducts(page, size);
            return ResponseEntity.ok(pagedProducts);
        } else {
            List<ProductDTO> products = productService.getAllProducts();
            return ResponseEntity.ok(products);
        }
    }

    @Operation(summary = "Get product by ID", description = "Retrieves a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the product", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "ID of the product to be retrieved", required = true) @PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(summary = "Get products by category", description = "Retrieves products by their category with optional pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the products", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<?> getProductsByCategory(
            @Parameter(description = "Category of products to be retrieved (e.g., book, cd, dvd)", required = true) @PathVariable String category,
            @Parameter(description = "Page number (0-based)") @RequestParam(required = false) Integer page,
            @Parameter(description = "Page size") @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            PagedResponse<ProductDTO> pagedProducts = productService.getProductsByCategory(category, page, size);
            return ResponseEntity.ok(pagedProducts);
        } else {
            List<ProductDTO> products = productService.getProductsByCategory(category);
            return ResponseEntity.ok(products);
        }
    }

    @Operation(summary = "Search products", description = "Searches for products by keyword in title with optional pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the products", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @Parameter(description = "Keyword to search in product titles", required = true) @RequestParam String keyword,
            @Parameter(description = "Page number (0-based)") @RequestParam(required = false) Integer page,
            @Parameter(description = "Page size") @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            PagedResponse<ProductDTO> pagedProducts = productService.searchProducts(keyword, page, size);
            return ResponseEntity.ok(pagedProducts);
        } else {
            List<ProductDTO> products = productService.searchProducts(keyword);
            return ResponseEntity.ok(products);
        }
    }
}