package com.wsc.auth.lib.model;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresAt,
        UserInfo userInfo
) {
}
