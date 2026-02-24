package com.wsc.auth.lib.service;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import com.wsc.auth.lib.model.UserInfo;
import com.wsc.auth.lib.contract.JwtUser;
import com.wsc.auth.lib.model.AuthResponse;
import com.wsc.auth.lib.config.JwtProperties;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.wsc.auth.lib.service.impl.JwtServiceImpl;
import com.wsc.auth.lib.service.impl.AuthenticationServiceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @Mock
    private JwtServiceImpl jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private AuthenticationServiceImpl authService;

    @Test
    void generateHashPasswordTest() {

        String rawPassword = "123456";
        String encodedPassword = "encoded-password";

        when(passwordEncoder.encode(rawPassword))
                .thenReturn(encodedPassword);

        String result = authService.generateHashPassword(rawPassword);

        assertEquals(encodedPassword, result);

        verify(passwordEncoder).encode(rawPassword);
    }

    @Test
    void authenticateTest() {

        String rawPassword = "123";
        String encodedPassword = "encoded";

        JwtUser user = mock(JwtUser.class);

        when(passwordEncoder.matches(rawPassword, encodedPassword))
                .thenReturn(true);

        when(user.getId()).thenReturn(1L);
        when(user.getName()).thenReturn("Teste");
        when(user.getEmail()).thenReturn("teste@email.com");
        when(user.getRole()).thenReturn("ADMIN");

        when(jwtService.generateToken(eq(user), any(Date.class)))
                .thenReturn("access-token");

        when(jwtService.generateRefreshToken(eq(user), any(Date.class)))
                .thenReturn("refresh-token");

        when(jwtProperties.getAccessTokenExpiration())
                .thenReturn(3600000L);

        AuthResponse response = authService.authenticate(
                rawPassword,
                encodedPassword,
                user
        );

        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600000L, response.expiresAt());
        assertEquals(1L, response.userInfo().getId());

        verify(passwordEncoder).matches(rawPassword, encodedPassword);
        verify(jwtService).generateToken(eq(user), any(Date.class));
        verify(jwtService).generateRefreshToken(eq(user), any(Date.class));
    }

    @Test
    void authenticateFailedTest() {

        String rawPassword = "123";
        String encodedPassword = "encoded";

        JwtUser user = mock(JwtUser.class);

        when(passwordEncoder.matches(rawPassword, encodedPassword))
                .thenReturn(false);

        assertThrows(RuntimeException.class, () ->
                authService.authenticate(rawPassword, encodedPassword, user)
        );

        verify(jwtService, never()).generateToken(any(), any());
        verify(jwtService, never()).generateRefreshToken(any(), any());
    }

    @Test
    void authenticateWithRefreshTokenTest() {

        String refreshToken = "valid-refresh-token";

        UserInfo userInfo = new UserInfo(
                1L,
                "Teste",
                "teste@email.com",
                "ADMIN"
        );

        when(jwtService.isInvalidTokenValid(refreshToken))
                .thenReturn(false);

        when(jwtService.isRefreshToken(refreshToken))
                .thenReturn(true);

        when(jwtService.extractUserInfo(refreshToken))
                .thenReturn(userInfo);

        when(jwtService.generateToken(eq(userInfo), any(Date.class)))
                .thenReturn("new-access-token");

        when(jwtService.generateRefreshToken(eq(userInfo), any(Date.class)))
                .thenReturn("new-refresh-token");

        when(jwtProperties.getAccessTokenExpiration())
                .thenReturn(3600000L);

        AuthResponse response =
                authService.authenticateWithRefreshToken(refreshToken);

        assertEquals("new-access-token", response.accessToken());
        assertEquals("new-refresh-token", response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(1L, response.userInfo().getId());

        verify(jwtService).generateToken(eq(userInfo), any(Date.class));
        verify(jwtService).generateRefreshToken(eq(userInfo), any(Date.class));
    }

    @Test
    void authenticateWithRefreshTokenWithInvalidTokenTest() {

        String refreshToken = "invalid";

        when(jwtService.isInvalidTokenValid(refreshToken))
                .thenReturn(true);

        assertThrows(RuntimeException.class, () ->
                authService.authenticateWithRefreshToken(refreshToken)
        );

        verify(jwtService, never()).generateToken(any(), any());
    }

    @Test
    void authenticateWithRefreshTokenWithInvalidRefreshTokenTest() {

        String refreshToken = "invalid";

        when(jwtService.isRefreshToken(refreshToken))
                .thenReturn(false);

        assertThrows(RuntimeException.class, () ->
                authService.authenticateWithRefreshToken(refreshToken)
        );

        verify(jwtService, never()).generateToken(any(), any());
    }
}
