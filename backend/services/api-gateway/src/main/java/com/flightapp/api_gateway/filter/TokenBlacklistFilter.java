package com.flightapp.api_gateway.filter;

import com.flightapp.api_gateway.security.JwtUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class TokenBlacklistFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistFilter.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private JwtUtils jwtUtils;

    private static final String BLACKLIST_PREFIX = "blacklist:";
    private static final String BLOCKED_USER_PREFIX = "blocked_user:";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                    @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = parseJwt(request);
        if (jwt != null) {
            try {
                // 1. Check if the specific Token is blacklisted (e.g. logout)
                if (Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + jwt))) {
                    logger.warn("Blocked access from blacklisted token: {}", jwt.substring(0, Math.min(jwt.length(), 10)) + "...");
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().write("Custom-Error: Token is blacklisted. Please log in again.");
                    return;
                }

                // 2. Check if the User is globally blocked (e.g. Admin block)
                if (jwtUtils.validateJwtToken(jwt)) {
                    String username = jwtUtils.getUserNameFromJwtToken(jwt);
                    if (Boolean.TRUE.equals(redisTemplate.hasKey(BLOCKED_USER_PREFIX + username))) {
                        logger.warn("Blocked access for globally blocked user: {}", username);
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                        response.getWriter().write("Custom-Error: Your account has been blocked. Contact support.");
                        return;
                    }
                }                
            } catch (Exception e) {
                // If Redis is down, we log and allow processing (fail-open)
                logger.error("Security check failed due to Redis error: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
