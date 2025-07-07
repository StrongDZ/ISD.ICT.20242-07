package com.example.aims.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyLimitsResponseDTO {
    private Integer updateCount;
    private Integer deleteCount;
    private Integer updateLimit;
    private Integer deleteLimit;
    private String message;
} 