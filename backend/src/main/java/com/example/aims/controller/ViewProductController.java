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

    @Operation(summary = "Get products", description = "Retrieves a list of products with optional filtering and pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of products", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    
    @GetMapping
    public ResponseEntity<?> getProducts(
            @Parameter(description = "Page number (0-based)") @RequestParam(required = false) Integer page,
            @Parameter(description = "Page size") @RequestParam(required = false) Integer size,
            @Parameter(description = "Keyword to search in product titles") @RequestParam(required = false) String keyword,
            @Parameter(description = "Category of products (e.g., book, cd, dvd)") @RequestParam(required = false) String category,
            @Parameter(description = "Minimum price") @RequestParam(required = false) Double minPrice,
            @Parameter(description = "Maximum price") @RequestParam(required = false) Double maxPrice,
            @Parameter(description = "Sort by field") @RequestParam(required = false) String sortBy) {
        // Default pagination values
        int p = page != null ? page : 0;
        int s = size != null ? size : 20;

        boolean hasFilter = (keyword != null && !keyword.isBlank()) ||
                (category != null && !category.isBlank() && !"all".equalsIgnoreCase(category)) ||
                minPrice != null || maxPrice != null || (sortBy != null && !sortBy.isBlank());

        if (hasFilter || page != null && size != null) {
            PagedResponse<ProductDTO> paged = productService.getFilteredProducts(
                    keyword, category, minPrice, maxPrice, sortBy, p, s);
            return ResponseEntity.ok(paged);
        }

        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
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
}