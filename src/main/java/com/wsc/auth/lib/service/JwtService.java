package com.wsc.auth.lib.service;

import com.wsc.auth.lib.model.UserInfo;
import com.wsc.auth.lib.contract.JwtUser;

import java.util.Date;

public interface JwtService {

    UserInfo extractUserInfo(String token);

    boolean isInvalidTokenValid(String token);

    boolean isAccessToken(String token);

    boolean isRefreshToken(String token);

    String generateToken(JwtUser user, Date date);

    String generateRefreshToken(JwtUser user, Date date);

    String generateTokenWithRefreshToken(String refreshToken);

    String generateNewRefreshToken(String refreshToken);
}
