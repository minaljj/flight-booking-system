package com.flightapp.api_gateway.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RateLimitingFilterTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private RateLimitingFilter rateLimitingFilter;

    private AutoCloseable closeable;

    private static final String CLIENT_IP = "192.168.1.100";

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        ReflectionTestUtils.setField(rateLimitingFilter, "authMaxRequests", 10);
        ReflectionTestUtils.setField(rateLimitingFilter, "searchMaxRequests", 5);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testDoFilterInternalNonRateLimitedEndpointProceedsImmediately() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1.0/flight/booking/123"); 
        MockHttpServletResponse response = new MockHttpServletResponse();
        rateLimitingFilter.doFilterInternal(request, response, filterChain);
        verify(redisTemplate, never()).opsForValue();
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterForInternalAuthEndpointUnderLimitProceedsNext() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1.0/flight/auth/login");
        request.setRemoteAddr(CLIENT_IP);
        MockHttpServletResponse response = new MockHttpServletResponse();
        String expectedKey = "rate_limit:auth:" + CLIENT_IP;
        when(valueOperations.increment(expectedKey)).thenReturn(1L);
        rateLimitingFilter.doFilterInternal(request, response, filterChain);
        verify(redisTemplate, times(1)).expire(eq(expectedKey), eq(60L), eq(TimeUnit.SECONDS));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterForInternalAuthEndpointOverLimitReturns429() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1.0/flight/auth/register");
        request.addHeader("X-Forwarded-For", CLIENT_IP + ", 10.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        String expectedKey = "rate_limit:auth:" + CLIENT_IP;
        when(valueOperations.increment(expectedKey)).thenReturn(11L);
        rateLimitingFilter.doFilterInternal(request, response, filterChain);
        assertEquals(429, response.getStatus());
        assertEquals("60", response.getHeader("Retry-After"));
        assertEquals("Too many requests. Limit exceeded for authentication.", response.getContentAsString());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testDoFilterForInternalSearchEndpointOverLimitReturns429() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1.0/flight/search/"); 
        request.setRemoteAddr(CLIENT_IP);
        MockHttpServletResponse response = new MockHttpServletResponse();
        String expectedKey = "rate_limit:search:" + CLIENT_IP;
        when(valueOperations.increment(expectedKey)).thenReturn(6L);
        rateLimitingFilter.doFilterInternal(request, response, filterChain);
        assertEquals(429, response.getStatus());
        assertEquals("1", response.getHeader("Retry-After"));
        assertEquals("Too many requests. Limit exceeded for search.", response.getContentAsString());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testDoFilterForInternalRedisErrorFailsOpenAndProceeds() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1.0/flight/auth/login");
        request.setRemoteAddr(CLIENT_IP);
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(valueOperations.increment(anyString())).thenThrow(new RuntimeException("Redis connection error"));
        rateLimitingFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }
}