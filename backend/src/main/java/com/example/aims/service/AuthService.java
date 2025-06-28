package com.example.aims.service;

import com.example.aims.dto.JwtResponseDTO;
import com.example.aims.dto.LoginRequestDTO;
import com.example.aims.security.JwtUtils;
import com.example.aims.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public JwtResponseDTO authenticateUser(LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String jwt = jwtUtils.generateJwtToken(
                userDetails.getUsername(),
                userDetails.getAuthorities().iterator().next().getAuthority(),
                userDetails.getUserStatus()
            );
            return new JwtResponseDTO(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getGmail(),
                userDetails.getRoles()
            );
        } catch (Exception e) {
            throw e;
        }
    }
} 