package com.example.aims.mapper;

import com.example.aims.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class ApiExceptionMapper {
    public ApiException mapToApiException(String message, HttpStatus status) {
        return new ApiException(
                message,
                status,
                ZonedDateTime.now(ZoneId.systemDefault())
        );
    }

    public ApiException mapToApiException(Exception e, HttpStatus status) {
        return new ApiException(
                e.getMessage(),
                status,
                ZonedDateTime.now(ZoneId.systemDefault())
        );
    }

    public Map<String, String> mapValidationErrors(Map<String, String> errors) {
        return new HashMap<>(errors);
    }
} 