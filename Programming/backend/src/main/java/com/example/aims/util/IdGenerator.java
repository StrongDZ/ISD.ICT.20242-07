package com.example.aims.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Utility class for generating unique IDs for different entities
 */
public class IdGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * Generates a unique product ID with a prefix based on the product category
     * 
     * @param category The product category (book, cd, dvd)
     * @return A unique product ID
     */
    public static String generateProductId(String category) {
        String prefix;
        switch (category.toLowerCase()) {
            case "book":
                prefix = "BK";
                break;
            case "cd":
                prefix = "CD";
                break;
            case "dvd":
                prefix = "DVD";
                break;
            default:
                prefix = "PRD";
        }
        
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String randomPart = UUID.randomUUID().toString().substring(0, 8);
        
        return prefix + "-" + timestamp + "-" + randomPart;
    }

    /**
     * Generates a unique order ID
     * 
     * @return A unique order ID
     */
    public static String generateOrderId() {
        String prefix = "ORD";
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String randomPart = UUID.randomUUID().toString().substring(0, 8);
        
        return prefix + "-" + timestamp + "-" + randomPart;
    }

    /**
     * Generates a unique user ID
     * 
     * @return A unique user ID
     */
    public static String generateUsersId() {
        String prefix = "USR";
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String randomPart = UUID.randomUUID().toString().substring(0, 8);
        
        return prefix + "-" + timestamp + "-" + randomPart;
    }
}