package com.example.aims.controller;

import com.example.aims.dto.JwtResponseDTO;
import com.example.aims.dto.LoginRequestDTO;
import com.example.aims.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {

        JwtResponseDTO response = authService.authenticateUser(loginRequest);
        
        return ResponseEntity.ok(response);
    }
} 