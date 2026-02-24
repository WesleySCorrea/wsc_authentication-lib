package com.wsc.auth.lib.service.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import com.wsc.auth.lib.model.UserInfo;
import com.wsc.auth.lib.enums.TokenType;
import com.wsc.auth.lib.contract.JwtUser;
import com.wsc.auth.lib.service.JwtService;
import com.wsc.auth.lib.config.JwtProperties;
import org.springframework.stereotype.Service;

import java.util.Date;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JwtServiceImpl implements JwtService {

    private final SecretKey key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtServiceImpl(JwtProperties properties) {
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = properties.getAccessTokenExpiration();
        this.refreshTokenExpiration = properties.getRefreshTokenExpiration();
    }

    @Override
    public UserInfo extractUserInfo(String token) {

        Claims claims = this.extractAllClaims(token);

        return new UserInfo(
                Long.parseLong(claims.getSubject()),
                claims.get("name", String.class),
                claims.get("email", String.class),
                claims.get("role", String.class)
        );
    }

    @Override
    public boolean isInvalidTokenValid(String token) {
        try {
            this.extractAllClaims(token);
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }

    @Override
    public boolean isAccessToken(String token) {
        Claims claims = this.extractAllClaims(token);
        return TokenType.ACCESS.name()
                .equals(claims.get("type", String.class));
    }

    @Override
    public boolean isRefreshToken(String token) {
        Claims claims = this.extractAllClaims(token);
        return TokenType.REFRESH.name()
                .equals(claims.get("type", String.class));
    }

    @Override
    public String generateToken(JwtUser user, Date date) {
        Date expiration = new Date(date.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("name", user.getName())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .claim("type", TokenType.ACCESS.name())
                .issuedAt(date)
                .expiration(expiration)
                .issuer("auth-service")
                .signWith(key)
                .compact();
    }

    @Override
    public String generateRefreshToken(JwtUser user, Date date) {
        Date expiration = new Date(date.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("name", user.getName())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .claim("type", TokenType.REFRESH.name())
                .issuedAt(date)
                .expiration(expiration)
                .issuer("auth-service")
                .signWith(key)
                .compact();
    }

    @Override
    public String generateTokenWithRefreshToken(String refreshToken) {
        Claims claims = this.extractAllClaims(refreshToken);

        if (!TokenType.REFRESH.name().equals(claims.get("type", String.class))) {
            throw new JwtException("Token inválido: não é refresh token");
        }

        UserInfo userInfo = this.extractUserInfo(refreshToken);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(claims.getSubject())
                .claim("name", userInfo.getName())
                .claim("email", userInfo.getEmail())
                .claim("role", userInfo.getRole())
                .claim("type", TokenType.ACCESS.name())
                .issuedAt(now)
                .expiration(expiration)
                .issuer("auth-service")
                .signWith(key)
                .compact();
    }

    @Override
    public String generateNewRefreshToken(String refreshToken) {
        Claims claims = this.extractAllClaims(refreshToken);

        if (!TokenType.REFRESH.name().equals(claims.get("type", String.class))) {
            throw new JwtException("Token inválido: não é refresh token");
        }

        UserInfo userInfo = this.extractUserInfo(refreshToken);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .subject(claims.getSubject())
                .claim("name", userInfo.getName())
                .claim("email", userInfo.getEmail())
                .claim("role", userInfo.getRole())
                .claim("type", TokenType.REFRESH.name())
                .issuedAt(now)
                .expiration(expiration)
                .issuer("auth-service")
                .signWith(key)
                .compact();
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
