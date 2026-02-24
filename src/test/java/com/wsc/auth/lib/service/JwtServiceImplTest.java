package com.wsc.auth.lib.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import com.wsc.auth.lib.model.UserInfo;
import org.junit.jupiter.api.BeforeEach;
import com.wsc.auth.lib.enums.TokenType;
import com.wsc.auth.lib.config.JwtProperties;
import com.wsc.auth.lib.service.impl.JwtServiceImpl;

import java.util.Date;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceImplTest {

    private JwtServiceImpl jwtService;
    private SecretKey key;

    @BeforeEach
    void setup() {

        JwtProperties properties = new JwtProperties();
        properties.setSecret("secretkeyaratestesunitarios-secretinvalidaemprod");
        properties.setAccessTokenExpiration(3600000L);
        properties.setRefreshTokenExpiration(7200000L);

        jwtService = new JwtServiceImpl(properties);

        key = Keys.hmacShaKeyFor(
                properties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    @Test
    void extractUserInfoTest() {

        String token = this.generateValidToken();

        UserInfo userInfo = jwtService.extractUserInfo(token);

        assertNotNull(userInfo);
        assertEquals(1L, userInfo.getId());
        assertEquals("Teste", userInfo.getName());
        assertEquals("teste@email.com", userInfo.getEmail());
        assertEquals("ADMIN", userInfo.getRole());
    }

    @Test
    void isInvalidTokenValid() {

        String token = "invalid-token";

        assertTrue(jwtService.isInvalidTokenValid(token));

    }

    @Test
    void isInvalidTokenValidFalse() {

        String token = this.generateValidToken();

        assertFalse(jwtService.isInvalidTokenValid(token));

    }

    @Test
    void isAccessTokenTest() {

        String token = this.generateValidToken();

        assertTrue(jwtService.isAccessToken(token));
    }

    @Test
    void isAccessTokenFalseTest() {

        String token = this.generateValidRefreshToken();

        assertFalse(jwtService.isAccessToken(token));
    }

    @Test
    void isRefreshTokenTest() {

        String token = this.generateValidRefreshToken();

        assertTrue(jwtService.isRefreshToken(token));
    }

    @Test
    void isRefreshTokenFalseTest() {

        String token = this.generateValidToken();

        assertFalse(jwtService.isRefreshToken(token));
    }

    @Test
    void generateTokenTest() {

        UserInfo user = this.generateValidUser();
        Date date = new Date();

        String token = jwtService.generateToken(user, date);

        assertNotNull(token);

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();


        assertEquals("1", claims.getSubject());
        assertEquals("Teste", claims.get("name", String.class));
        assertEquals("teste@email.com", claims.get("email", String.class));
        assertEquals("ADMIN", claims.get("role", String.class));
        assertEquals("ACCESS", claims.get("type", String.class));
        assertEquals("auth-service", claims.getIssuer());
        assertEquals(claims.getIssuedAt().getTime(), claims.getIssuedAt().getTime());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }

    @Test
    void generateRefreshTokenTest() {

        UserInfo user = this.generateValidUser();
        Date date = new Date();

        String token = jwtService.generateRefreshToken(user, date);

        assertNotNull(token);

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();


        assertEquals("1", claims.getSubject());
        assertEquals("Teste", claims.get("name", String.class));
        assertEquals("teste@email.com", claims.get("email", String.class));
        assertEquals("ADMIN", claims.get("role", String.class));
        assertEquals("REFRESH", claims.get("type", String.class));
        assertEquals("auth-service", claims.getIssuer());
        assertEquals(claims.getIssuedAt().getTime(), claims.getIssuedAt().getTime());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }

    @Test
    void generateTokenWithRefreshTokenTest() {

        UserInfo user = generateValidUser();
        Date issuedAt = new Date();

        String refreshToken = jwtService.generateRefreshToken(user, issuedAt);

        String newAccessToken = jwtService.generateTokenWithRefreshToken(refreshToken);

        assertNotNull(newAccessToken);

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(newAccessToken)
                .getPayload();

        assertEquals("1", claims.getSubject());
        assertEquals("Teste", claims.get("name", String.class));
        assertEquals("teste@email.com", claims.get("email", String.class));
        assertEquals("ADMIN", claims.get("role", String.class));
        assertEquals("ACCESS", claims.get("type", String.class));
        assertEquals("auth-service", claims.getIssuer());
    }

    @Test
    void invalidGenerateTokenWithRefreshTokenTest() {

        UserInfo user = generateValidUser();
        Date issuedAt = new Date();

        String token = jwtService.generateToken(user, issuedAt);

        assertThrows(JwtException.class, () ->
                jwtService.generateTokenWithRefreshToken(token)
        );
    }

    @Test
    void generateNewRefreshTokenTest() {

        UserInfo user = generateValidUser();
        Date issuedAt = new Date();

        String refreshToken = jwtService.generateRefreshToken(user, issuedAt);

        String newRefreshToken = jwtService.generateNewRefreshToken(refreshToken);

        assertNotNull(newRefreshToken);

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(newRefreshToken)
                .getPayload();

        assertEquals("1", claims.getSubject());
        assertEquals("Teste", claims.get("name", String.class));
        assertEquals("teste@email.com", claims.get("email", String.class));
        assertEquals("ADMIN", claims.get("role", String.class));
        assertEquals("REFRESH", claims.get("type", String.class));
        assertEquals("auth-service", claims.getIssuer());
    }

    @Test
    void generateNewRefreshToken() {

        UserInfo user = generateValidUser();
        Date issuedAt = new Date();

        String token = jwtService.generateToken(user, issuedAt);

        assertThrows(JwtException.class, () ->
                jwtService.generateNewRefreshToken(token)
        );
    }

    private String generateValidToken() {
        return Jwts.builder()
                .subject("1")
                .claim("name", "Teste")
                .claim("email", "teste@email.com")
                .claim("role", "ADMIN")
                .claim("type", TokenType.ACCESS.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(key)
                .compact();
    }

    private String generateValidRefreshToken() {
        return Jwts.builder()
                .subject("1")
                .claim("name", "Teste")
                .claim("email", "teste@email.com")
                .claim("role", "ADMIN")
                .claim("type", TokenType.REFRESH.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(key)
                .compact();
    }

    private UserInfo generateValidUser() {
        return new UserInfo(1L, "Teste", "teste@email.com", "ADMIN");
    }
}