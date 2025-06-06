package com.example.aims.service.user;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import com.example.aims.controller.response.TokenResponse;
import com.example.aims.service.user.JwtService;
import com.example.aims.common.TokenType;


@Service
@Slf4j(topic = "JwtServiceImpl")
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.exiryMintues}")
    private long exiryMintues;

    @Value("${jwt.accessKey}")
    private String accessKey;

    @Value("${jwt.refreshKey}")
    private String refreshKey;

    @Override
    public String generateAccessToken(Integer userId, String username, List<String> authorities) {
        // Implementation for generating JWT token
       // log.info("Generating access token for user: {}", username);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role",authorities);
        return generateToken(claims, username);
    }
    @Override
    public String generateRefreshToken(Integer userId, String username, List<String> authorities) {
        // Implementation for generating refresh token
       // log.info("Generating refresh token for user: {}", username);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role",authorities);
        return generateRefreshToken(claims, username);
    }
    @Override
    public String extractUsername(String token, TokenType tokenType) {
      //  log.info("Extracting username from token: {} with type {}", token, type);
        // Implementation for extracting username from token
        return extractClaim(tokenType, token, Claims::getSubject);
    }

    private <T> T extractClaim(TokenType type, String token, Function<Claims, T> claimsExtractor) {
        final Claims claims = extractAllClaims(token, type);
        return claimsExtractor.apply(claims);
    }
    private Claims extractAllClaims(String token, TokenType type) {
        try{
        return Jwts.parser()
                .setSigningKey(accessKey)
                .parseClaimsJws(token)
                .getBody();
        } catch (SignatureException e) {
            throw new RuntimeException("Invalid JWT signature: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JWT: " + e.getMessage());
        }
    }

    private String generateToken( Map<String, Object> claims, String username){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 *60 * 10)) // 10 hours
                .signWith(getKey(TokenType.ACCESS_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }
    private String generateRefreshToken( Map<String, Object> claims, String username){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 *60 * 20*14)) 
                .signWith(getKey(TokenType.REFRESH_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey(TokenType type){
        switch(type){
            case ACCESS_TOKEN:
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
            case REFRESH_TOKEN:
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
            default:
                throw new IllegalArgumentException("Invalid token type");
        }
    }
}
