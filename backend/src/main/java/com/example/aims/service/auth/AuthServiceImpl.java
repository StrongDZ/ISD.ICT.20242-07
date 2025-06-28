package com.example.aims.service.auth;

import com.example.aims.dto.JwtResponseDTO;
import com.example.aims.dto.LoginRequestDTO;
import com.example.aims.security.JwtUtils;
import com.example.aims.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    public JwtResponseDTO authenticateUser(LoginRequestDTO loginRequest) {
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

            return response;
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", loginRequest.getUsername(), e);
            throw e;
        }
    }
} 