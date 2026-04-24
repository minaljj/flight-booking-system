    package com.flightapp.user_service.security;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.redis.core.RedisTemplate;
    import org.springframework.http.HttpStatus;
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

        private static final String RATE_LIMIT_PREFIX = "rate_limit:ip:";
        private static final int MAX_REQUESTS_PER_MINUTE = 10;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            if (request.getRequestURI().startsWith("/api/v1.0/flight/auth")) {
                String clientIp = request.getRemoteAddr();
                String key = RATE_LIMIT_PREFIX + clientIp;
                Long count = redisTemplate.opsForValue().increment(key);
                if (count != null && count == 1) {
                    redisTemplate.expire(key, 1, TimeUnit.MINUTES);
                }
                if (count != null && count > MAX_REQUESTS_PER_MINUTE) {
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    response.setHeader("Retry-After", "60");
                    response.getWriter().write("Too many requests. Please try again after a minute.");
                    return;
                }
            }
            filterChain.doFilter(request, response);
        }
    }
