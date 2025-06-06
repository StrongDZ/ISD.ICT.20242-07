package com.example.aims.controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aims.controller.request.SigInRequest;
import com.example.aims.controller.response.TokenResponse;
import com.example.aims.service.user.AuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/auth")
@Slf4j(topic = "AuthenticationController")
@Tag(name = "Authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Get Access Token", description = "Get access token and referesh token for the user")
    @PostMapping("/access-token")
    public TokenResponse getAcessToken(@RequestBody SigInRequest request) {
        //TODO: process POST request
        log.info("Access token request");
        // return TokenResponse.builder()
        //         .accessToken("DUMMY-ACCESS-TOKEN")
        //         .refreshToken("DUMMY-REFERSH-TOKEN")
        //         .build();
       return authenticationService.getAccessToken(request);
    }

     @Operation(summary = "Refresh Token", description = "Get new access token and referesh token for the user")
    @PostMapping("/refresh-token")
    public TokenResponse getRefreshToken(@RequestBody String refreshToken) {
        //TODO: process POST request
        log.info("Refresh token request");
        return TokenResponse.builder()
                .accessToken("DUMMY-NEW-ACCESS-TOKEN")
                .refreshToken("DUMMY-REFERSH-TOKEN")
                .build();
    }
}
