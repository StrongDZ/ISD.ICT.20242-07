package com.example.aims.service.user;

import java.util.List;

import com.example.aims.common.TokenType;




public interface JwtService {
    String generateAccessToken(Integer userId, String username, List<String>authorities);
    String generateRefreshToken(Integer userId, String username, List<String>authorities);
    String extractUsername(String token, TokenType tokenType);
}
