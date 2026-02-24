package com.wsc.auth.lib.service.impl;

import com.wsc.auth.lib.model.UserInfo;
import com.wsc.auth.lib.contract.JwtUser;
import com.wsc.auth.lib.model.AuthResponse;
import com.wsc.auth.lib.config.JwtProperties;
import org.springframework.stereotype.Service;
import com.wsc.auth.lib.service.AuthenticationService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtServiceImpl jwtService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;

    public AuthenticationServiceImpl(JwtServiceImpl jwtService, PasswordEncoder passwordEncoder, JwtProperties jwtProperties) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public String generateHashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public AuthResponse authenticate(String rawPassword, String password, JwtUser user) {

        if (!passwordEncoder.matches(rawPassword, password)) {
            throw new RuntimeException("Invalid credentials");
        }

        Date date = new Date();

        String accessToken = jwtService.generateToken(user, date);
        String refreshToken = jwtService.generateRefreshToken(user, date);

        UserInfo userInfo = new UserInfo(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtProperties.getAccessTokenExpiration(),
                userInfo
        );
    }

    @Override
    public AuthResponse authenticateWithRefreshToken(String refreshToken) {

        if (jwtService.isInvalidTokenValid(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Is not a refresh token");
        }

        UserInfo userInfo = jwtService.extractUserInfo(refreshToken);

        Date date = new Date();

        String newAccessToken = jwtService.generateToken(userInfo, date);
        String newRefreshToken = jwtService.generateRefreshToken(userInfo, date);

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                jwtProperties.getAccessTokenExpiration(),
                userInfo
        );
    }
}
