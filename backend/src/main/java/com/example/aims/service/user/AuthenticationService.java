package com.example.aims.service.user;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.aims.controller.request.SigInRequest;
import com.example.aims.controller.response.TokenResponse;


@Service
public interface AuthenticationService {

    TokenResponse getAccessToken(SigInRequest request);
    TokenResponse getRefreshToken(String request);
}
