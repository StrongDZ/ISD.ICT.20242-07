package com.example.aims.service;

import com.example.aims.dto.JwtResponseDTO;
import com.example.aims.dto.LoginRequestDTO;
import com.example.aims.security.JwtUtils;
import com.example.aims.security.UserDetailsImpl;
import com.example.aims.common.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAuthenticateUser_Success() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO("testuser", "password");
        List<GrantedAuthority> authorities = List.of(() -> "ROLE_USER");
        UserDetailsImpl userDetails = new UserDetailsImpl(1, "testuser", "test@gmail.com", "password", authorities, UserStatus.NONE);
        String fakeJwt = "fake-jwt-token";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(eq("testuser"), eq("ROLE_USER"), eq(UserStatus.NONE))).thenReturn(fakeJwt);

        // Act
        JwtResponseDTO response = authService.authenticateUser(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(fakeJwt, response.getToken());
        assertEquals(1, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@gmail.com", response.getEmail());
        assertEquals(List.of("ROLE_USER"), response.getRoles());
    }

    @Test
    void testAuthenticateUser_Fail_ThrowsException() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO("wronguser", "wrongpass");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Bad credentials"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.authenticateUser(loginRequest));
    }
} 