package com.flightapp.api_gateway.filter;

import com.flightapp.api_gateway.security.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TokenBlacklistFilterTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private TokenBlacklistFilter tokenBlacklistFilter;

    private AutoCloseable closeable;

    private static final String BLACKLIST_PREFIX = "blacklist:";
    private static final String BLOCKED_USER_PREFIX = "blocked_user:";

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testDoFilterInternalWhenNoJwtProceedsToNextFilter() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        tokenBlacklistFilter.doFilterInternal(request, response, filterChain);
        verify(redisTemplate, never()).hasKey(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterWhenInternalValidJwtNotBlacklistedProceedsToNextFilter() throws ServletException, IOException {
        String token = "valid-token";
        String username = "testuser";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(redisTemplate.hasKey(BLACKLIST_PREFIX + token)).thenReturn(false);
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(username);
        when(redisTemplate.hasKey(BLOCKED_USER_PREFIX + username)).thenReturn(false);
        tokenBlacklistFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalTokenIsBlacklistedReturns401() throws ServletException, IOException {
        String token = "blacklisted-token";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(redisTemplate.hasKey(BLACKLIST_PREFIX + token)).thenReturn(true);
        tokenBlacklistFilter.doFilterInternal(request, response, filterChain);
        assertEquals(401, response.getStatus());
        assertEquals("Custom-Error: Token is blacklisted. Please log in again.", response.getContentAsString());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testDoFilterWhenInternalUserIsBlockedReturns403() throws ServletException, IOException {
        String token = "valid-token-but-blocked-user";
        String username = "blockeduser";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(redisTemplate.hasKey(BLACKLIST_PREFIX + token)).thenReturn(false);
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(username);
        when(redisTemplate.hasKey(BLOCKED_USER_PREFIX + username)).thenReturn(true);
        tokenBlacklistFilter.doFilterInternal(request, response, filterChain);
        assertEquals(403, response.getStatus());
        assertEquals("Custom-Error: Your account has been blocked. Contact support.", response.getContentAsString());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testDoFilterWhenInternalInvalidJwtProceedsToNextFilter() throws ServletException, IOException {
        String token = "invalid-token";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(redisTemplate.hasKey(BLACKLIST_PREFIX + token)).thenReturn(false);
        when(jwtUtils.validateJwtToken(token)).thenReturn(false);
        tokenBlacklistFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalRedisErrorFailsOpenAndProceeds() throws ServletException, IOException {
        String token = "some-token";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(redisTemplate.hasKey(BLACKLIST_PREFIX + token)).thenThrow(new RuntimeException("Redis connection error"));
        tokenBlacklistFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }
}