package com.example.aims.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
public class ManagerDailyLimitExceptionMapper {
    
    @ExceptionHandler(ManagerDailyLimitException.class)
    public ResponseEntity<ApiException> handleManagerDailyLimitException(ManagerDailyLimitException ex) {
        ApiException apiException = new ApiException(
            ex.getMessage(),
            HttpStatus.TOO_MANY_REQUESTS,
            ZonedDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(apiException);
    }
} 