package com.flightapp.api_gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${app.rate-limit.auth.max-requests:10}")
    private int authMaxRequests;

    @Value("${app.rate-limit.search.max-requests:60}")
    private int searchMaxRequests;

    private static final String RATE_LIMIT_PREFIX = "rate_limit:";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                    @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        String clientIp = getClientIp(request);
        int maxRequests;
        int windowSeconds;
        String keyPrefix;
        if (uri.contains("/auth/")) {
            maxRequests = authMaxRequests;
            windowSeconds = 60; // 1 minute
            keyPrefix = "auth:";
        } else if (uri.contains("/search/")) {
            maxRequests = searchMaxRequests;
            windowSeconds = 1; // 1 second
            keyPrefix = "search:";
        } else {
            filterChain.doFilter(request, response);
            return;
        }
        String key = RATE_LIMIT_PREFIX + keyPrefix + clientIp;
        try {
            Long count = redisTemplate.opsForValue().increment(key);
            if (count != null && count == 1) {
                redisTemplate.expire(key, windowSeconds, TimeUnit.SECONDS);
            }
            if (count != null && count > maxRequests) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setHeader("Retry-After", String.valueOf(windowSeconds));
                response.getWriter().write("Too many requests. Limit exceeded for " + (uri.contains("/auth/") ? "authentication" : "search") + ".");
                return;
            }
        } catch (Exception e) {
            logger.error("Rate limiting failed for " + uri + " due to Redis error: " + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
