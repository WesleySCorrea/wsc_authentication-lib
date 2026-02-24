package com.wsc.auth.lib.security.filter;

import jakarta.servlet.FilterChain;
import com.wsc.auth.lib.model.UserInfo;
import jakarta.servlet.ServletException;
import org.jspecify.annotations.NonNull;
import com.wsc.auth.lib.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

//        if (this.shouldFilterPermitted(request)) {
//            filterChain.doFilter(request, response);
//            return;
//        }

        String token = extractToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtService.isInvalidTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtService.isAccessToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        UserInfo userInfo = jwtService.extractUserInfo(token);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userInfo,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + userInfo.getRole()))
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

//    private boolean shouldFilterPermitted(HttpServletRequest request) {
//        String path = request.getServletPath();
//
//        return path.startsWith("/api/v1/auth");
//    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }
}
