package com.flightapp.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenBlacklistServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    public void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void testBlacklistTokenWithExpiration() {
        String token = "test-token";
        long expirationMs = 1000L;
        tokenBlacklistService.blacklistToken(token, expirationMs);
        verify(valueOperations).set(eq("blacklist:" + token), eq("true"), eq(expirationMs), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testBlacklistTokenWithZeroExpiration() {
        String token = "test-token";
        tokenBlacklistService.blacklistToken(token, 0);
        verify(valueOperations).set(eq("blacklist:" + token), eq("true"), eq(15L), eq(TimeUnit.MINUTES));
    }

    @Test
    public void testIsBlacklisted() {
        String token = "test-token";
        when(redisTemplate.hasKey("blacklist:" + token)).thenReturn(true);
        boolean result = tokenBlacklistService.isBlacklisted(token);
        assertTrue(result);
        verify(redisTemplate).hasKey("blacklist:" + token);
    }

    @Test
    public void testBlockUser() {
        String username = "testuser";
        tokenBlacklistService.blockUser(username);
        verify(valueOperations).set(eq("blocked_user:" + username), eq("true"));
    }

    @Test
    public void testUnblockUser() {
        String username = "testuser";
        tokenBlacklistService.unblockUser(username);
        verify(redisTemplate).delete(eq("blocked_user:" + username));
    }

    @Test
    public void testIsUserBlocked() {
        String username = "testuser";
        when(redisTemplate.hasKey("blocked_user:" + username)).thenReturn(true);
        boolean result = tokenBlacklistService.isUserBlocked(username);
        assertTrue(result);
        verify(redisTemplate).hasKey("blocked_user:" + username);
    }
}
