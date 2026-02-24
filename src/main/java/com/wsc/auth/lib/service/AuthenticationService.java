package com.wsc.auth.lib.service;

import com.wsc.auth.lib.contract.JwtUser;
import com.wsc.auth.lib.model.AuthResponse;

public interface AuthenticationService {

    String generateHashPassword(String password);

    AuthResponse authenticate(String rawPassword, String password, JwtUser user);

    AuthResponse authenticateWithRefreshToken(String refreshToken);
}
