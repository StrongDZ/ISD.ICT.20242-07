package com.example.aims.exception;

public class PriceChangeException extends RuntimeException {
    
    public PriceChangeException(String message) {
        super(message);
    }
    
    public PriceChangeException(String message, Throwable cause) {
        super(message, cause);
    }
} 