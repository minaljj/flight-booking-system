package com.flightapp.user_service.service;
import com.flightapp.user_service.model.RefreshToken;
import com.flightapp.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
@Service
public class RefreshTokenService {
    @Value("${jwt.refreshExpiration}")
    private Long refreshTokenDurationMs;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserRepository userRepository;
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String USER_TOKEN_PREFIX = "user_refresh_token:";
    public Optional<RefreshToken> findByToken(String token) {
        String userIdString = (String) redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + token);
        if (userIdString == null) {
            return Optional.empty();
        }
        Long userId = Long.parseLong(userIdString);
        return userRepository.findById(userId).map(user -> {
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setToken(token);
            refreshToken.setUser(user);
            refreshToken.setExpiryDate(Instant.now().plusMillis(60000));
            return refreshToken;
        });
    }
    public RefreshToken createRefreshToken(Long userId) {
        String oldToken = (String) redisTemplate.opsForValue().get(USER_TOKEN_PREFIX + userId);

        if (oldToken != null) {
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + oldToken);
        }
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                USER_TOKEN_PREFIX + userId,
                token,
                refreshTokenDurationMs,
                TimeUnit.MILLISECONDS
        );
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + token,
                userId.toString(),
                refreshTokenDurationMs,
                TimeUnit.MILLISECONDS
        );
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        return refreshToken;
    }
    public RefreshToken verifyExpiration(RefreshToken token) {
        return token;
    }
    public void deleteByUserId(Long userId) {
        String token = (String) redisTemplate.opsForValue().get(USER_TOKEN_PREFIX + userId);
        if (token != null) {
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + token);
            redisTemplate.delete(USER_TOKEN_PREFIX + userId);
        }
    }
}