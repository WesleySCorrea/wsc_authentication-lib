package com.wsc.auth.lib.config;

import com.wsc.auth.lib.service.JwtService;
import org.springframework.context.annotation.Bean;
import com.wsc.auth.lib.service.impl.JwtServiceImpl;
import com.wsc.auth.lib.service.AuthenticationService;
import com.wsc.auth.lib.service.impl.AuthenticationServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import com.wsc.auth.lib.security.filter.JwtAuthenticationFilter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtAutoConfiguration {

    @Bean
    public JwtService jwtService(JwtProperties properties) {
        return new JwtServiceImpl(properties);
    }

    @Bean
    public AuthenticationService authService(JwtServiceImpl jwtService, PasswordEncoder passwordEncoder, JwtProperties properties) {
        return new AuthenticationServiceImpl(jwtService, passwordEncoder, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService) {
        return new JwtAuthenticationFilter(jwtService);
    }

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
