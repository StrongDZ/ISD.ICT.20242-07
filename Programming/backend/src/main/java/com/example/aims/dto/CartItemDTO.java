package com.example.aims.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private String productID;
    private String productTitle;
    private Double productPrice;
    private Integer quantity;
    private String imageURL;
}