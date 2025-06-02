package com.example.aims.service;

import com.example.aims.dto.AuthRequest;
import com.example.aims.dto.AuthResponse;
import com.example.aims.dto.RegisterRequest;
import com.example.aims.model.Users;
import com.example.aims.repository.UsersRepository;
import com.example.aims.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    // COHESION: Logical Cohesion
    // The class groups together operations related to authentication (login and register),
    // but each method performs a different task with its own sub-logic:
    // - login(): handles authentication and token generation.
    // - register(): handles validation, account creation, encoding, saving, and re-authentication.
    //
    // These operations are logically related (user auth), but internally do not share processing or data,
    // which makes this Logical Cohesion.

    // SRP VIOLATION: This class currently handles multiple distinct responsibilities:
    // 1. Credential validation and user authentication (using AuthenticationManager)
    // 2. User account creation and password hashing (userRepository + PasswordEncoder)
    // 3. Token generation (JWT)
    //
    // SUGGESTED SOLUTION:
    // - Extract `TokenService` to encapsulate JWT generation.
    // - Extract `UserRegistrationService` to handle user creation and save logic.
    // - Keep AuthService focused only on coordinating those responsibilities.

    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UsersRepository userRepository, PasswordEncoder passwordEncoder, 
                      AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(AuthRequest request) {
        // Responsibility 1: Authenticate user credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Responsibility 2: Generate JWT token (can be separated)
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        // Responsibility 3: Load user entity (repository logic mixed in)
        Users user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        // SRP VIOLATION: Authentication logic, token creation, and user retrieval are tightly coupled.
        // Suggest extracting token creation and user loading.

        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole());
    }

    public AuthResponse register(RegisterRequest request) {
        // Responsibility 1: Validate if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Usersname already exists");
        }

        // Responsibility 2: Create and persist user
        Users user = new Users();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Hashing responsibility
        user.setRole(request.getRole());

        userRepository.save(user);

        // Responsibility 3: Auto-authenticate newly registered user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Responsibility 4: Generate JWT token
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        // SRP VIOLATION: Registration, password encoding, and token generation could be delegated.
        // Suggest extracting registration logic into its own service.

        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole());
    }
}
