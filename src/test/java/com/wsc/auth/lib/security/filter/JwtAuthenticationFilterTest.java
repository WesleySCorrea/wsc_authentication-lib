package com.wsc.auth.lib.security.filter;

import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import jakarta.servlet.FilterChain;
import com.wsc.auth.lib.model.UserInfo;
import org.junit.jupiter.api.BeforeEach;
import com.wsc.auth.lib.service.JwtService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateWhenTokenIsValid() throws Exception {

        String token = "valid-token";

        UserInfo userInfo = new UserInfo(
                1L,
                "Teste",
                "teste@email.com",
                "ADMIN"
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/v1/users");
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.isInvalidTokenValid(token)).thenReturn(false);
        when(jwtService.isAccessToken(token)).thenReturn(true);
        when(jwtService.extractUserInfo(token)).thenReturn(userInfo);

        filter.doFilter(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals(userInfo, authentication.getPrincipal());
        assertTrue(authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN")));

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenAuthorizationHeaderDoesNotStartWithBearer() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/v1/users");
        request.addHeader("Authorization", "Basic abc123");

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verifyNoInteractions(jwtService);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenAuthorizationHeaderDoesNotStartWithBearerIsNull() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/v1/users");
        request.addHeader("Error", "Barer");

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verifyNoInteractions(jwtService);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenAuthorizationTokenIsInvalid() throws Exception {

        String token = "invalid-token";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/v1/users");
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.isInvalidTokenValid(token)).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenAuthorizationIsNotAccessToken() throws Exception {
        String token = "invalid-token";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/v1/users");
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.isInvalidTokenValid(token)).thenReturn(false);
        when(jwtService.isAccessToken(token)).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(request, response);
    }
}
