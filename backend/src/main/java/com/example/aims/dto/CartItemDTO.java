package com.example.aims.dto;

import com.example.aims.dto.products.ProductDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private ProductDTO product;
    private Integer quantity;
    private Integer customerId;
}