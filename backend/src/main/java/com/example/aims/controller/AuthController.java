package com.example.aims.controller;

import com.example.aims.dto.JwtResponseDTO;
import com.example.aims.dto.LoginRequestDTO;
import com.example.aims.security.JwtUtils;
import com.example.aims.security.UserDetailsImpl;
import com.example.aims.service.user.UserService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
    
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        log.info("Attempting to authenticate user: {}", loginRequest.getUsername());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            log.info("Authentication successful for user: {}", loginRequest.getUsername());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            log.info("User details loaded: {}", userDetails.getUsername());
            
            String jwt = jwtUtils.generateJwtToken(
                userDetails.getUsername(),
                userDetails.getAuthorities().iterator().next().getAuthority(),
                userDetails.getUserStatus()
            );
            log.info("JWT token generated successfully");

            JwtResponseDTO response = new JwtResponseDTO(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getGmail(),
                    userDetails.getRoles());
            log.info("Response DTO created: {}", response);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", loginRequest.getUsername(), e);
            throw e;
        }
    }

} 