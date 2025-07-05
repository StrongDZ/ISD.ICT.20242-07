package com.example.aims.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class BulkDeleteRequestDTO {
    @NotEmpty(message = "Product IDs list cannot be empty")
    @Size(max = 10, message = "Cannot delete more than 10 products at once")
    private List<String> productIds;
} 