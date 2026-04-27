package com.flightapp.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private static final String BLACKLIST_PREFIX = "blacklist:";
	private static final String BLOCKED_USER_PREFIX = "blocked_user:";

	public void blacklistToken(String token, long expirationMs) {
		if (expirationMs > 0) {
			redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "true", expirationMs, TimeUnit.MILLISECONDS);
		} else {
			redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "true", 15, TimeUnit.MINUTES);
		}
	}

	public boolean isBlacklisted(String token) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
	}

	public void blockUser(String username) {
		redisTemplate.opsForValue().set(BLOCKED_USER_PREFIX + username, "true");
	}

	public void unblockUser(String username) {
		redisTemplate.delete(BLOCKED_USER_PREFIX + username);
	}

	public boolean isUserBlocked(String username) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(BLOCKED_USER_PREFIX + username));
	}
}
