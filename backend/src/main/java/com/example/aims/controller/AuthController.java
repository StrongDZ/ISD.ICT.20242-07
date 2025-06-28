package com.example.aims.controller;

import com.example.aims.dto.JwtResponseDTO;
import com.example.aims.dto.LoginRequestDTO;
import com.example.aims.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {
        log.info("Received login request for user: {}", loginRequest.getUsername());
        
        JwtResponseDTO response = authService.authenticateUser(loginRequest);
        
        log.info("Login successful for user: {}", loginRequest.getUsername());
        return ResponseEntity.ok(response);
    }
} 