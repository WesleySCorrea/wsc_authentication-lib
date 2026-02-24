package com.wsc.auth.lib.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wsc.auth.jwt")
public class JwtProperties {

    private String secret;

    private long accessTokenExpiration;

    private long refreshTokenExpiration;

    public String getSecret() {
        return secret;
    }

    public void setRefreshTokenExpiration(long refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public void setAccessTokenExpiration(long accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
