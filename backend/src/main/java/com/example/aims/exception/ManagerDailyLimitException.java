package com.example.aims.exception;

public class ManagerDailyLimitException extends RuntimeException {
    
    public ManagerDailyLimitException(String message) {
        super(message);
    }
    
    public ManagerDailyLimitException(String message, Throwable cause) {
        super(message, cause);
    }
} 