package com.flightapp.user_service.service;

import com.flightapp.user_service.model.RefreshToken;
import com.flightapp.user_service.model.User;
import com.flightapp.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", 86400000L);
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void testFindByTokenSuccess() {
        String token = "test-token";
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(valueOperations.get("refresh_token:" + token)).thenReturn(userId.toString());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Optional<RefreshToken> result = refreshTokenService.findByToken(token);
        assertTrue(result.isPresent());
        assertEquals(token, result.get().getToken());
        assertEquals(userId, result.get().getUser().getId());
    }

    @Test
    public void testFindByTokenNotFound() {
        String token = "test-token";
        when(valueOperations.get("refresh_token:" + token)).thenReturn(null);
        Optional<RefreshToken> result = refreshTokenService.findByToken(token);
        assertFalse(result.isPresent());
    }

    @Test
    public void testCreateRefreshToken() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(valueOperations.get("user_refresh_token:" + userId)).thenReturn(null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        RefreshToken result = refreshTokenService.createRefreshToken(userId);
        assertNotNull(result);
        assertEquals(userId, result.getUser().getId());
        assertNotNull(result.getToken());
        verify(valueOperations).set(eq("refresh_token:" + result.getToken()), eq(userId.toString()), eq(86400000L),
                eq(TimeUnit.MILLISECONDS));
        verify(valueOperations).set(eq("user_refresh_token:" + userId), eq(result.getToken()), eq(86400000L),
                eq(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testCreateRefreshTokenWhenOldTokenExists() {
        Long userId = 1L;
        String oldToken = "old-token";
        User user = new User();
        user.setId(userId);
        when(valueOperations.get("user_refresh_token:" + userId)).thenReturn(oldToken);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        RefreshToken result = refreshTokenService.createRefreshToken(userId);
        assertNotNull(result);
        verify(redisTemplate).delete("refresh_token:" + oldToken);
        verify(valueOperations).set(eq("refresh_token:" + result.getToken()), eq(userId.toString()), eq(86400000L),
                eq(TimeUnit.MILLISECONDS));
        verify(valueOperations).set(eq("user_refresh_token:" + userId), eq(result.getToken()), eq(86400000L),
                eq(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testDeleteByUserId() {
        Long userId = 1L;
        String token = "test-token";
        when(valueOperations.get("user_refresh_token:" + userId)).thenReturn(token);
        refreshTokenService.deleteByUserId(userId);
        verify(redisTemplate).delete("refresh_token:" + token);
        verify(redisTemplate).delete("user_refresh_token:" + userId);
    }

    @Test
    public void testDeleteByUserIdForTokenNotFound() {
        Long userId = 1L;
        when(valueOperations.get("user_refresh_token:" + userId)).thenReturn(null);
        refreshTokenService.deleteByUserId(userId);
        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    public void testVerifyExpiration() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().plusMillis(1000));
        RefreshToken result = refreshTokenService.verifyExpiration(token);
        assertEquals(token, result);
    }
}
