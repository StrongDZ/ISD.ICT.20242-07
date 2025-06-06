package com.example.aims.service.user;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.aims.controller.request.SigInRequest;
import com.example.aims.controller.response.TokenResponse;
import com.example.aims.repository.UsersRepository;
import com.example.aims.service.user.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AuthenticationServiceImpl")
public class AuthenticationServiceImpl implements AuthenticationService{
    private final UsersRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @Override
    public TokenResponse getAccessToken(SigInRequest request) {
        // List<String> authorities = new ArrayList<>();
        log.info("Request to authenticate user: {}, password: {}", request.getUsername(), request.getPassword());
        log.info("Full request object: {}", request);

        // try{
       Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        // authorities.add(authenticate.getAuthorities().toString());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // } catch (Exception e) {
        //     throw new AccessDeniedException("Authentication failed: " + e.getMessage());
        // }
        // String accessToken = jwtService.generateAccessToken(1, request.getUsername(), authorities);
        // String refreshToken = jwtService.generateRefreshToken(1, request.getUsername(), authorities);

        // return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
        log.info("Get access token");
        String accessToken = jwtService.generateAccessToken(1, request.getUsername(), null);
        String refreshToken = jwtService.generateRefreshToken(1, request.getUsername(), null);

        return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }
    @Override
    public TokenResponse getRefreshToken(String request) {
        return null;
    }

}


