package com.flightapp.user_service.service;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {
	@Autowired
	private RedisTemplate<String,Object>redisTemplate;
	private static final String BLACKLIST_PREFIX="blacklist:";
	public void blacklistToken(String token,long expirationMs)
	{
		if(expirationMs > 0)
		{
			redisTemplate.opsForValue().set(BLACKLIST_PREFIX+token,"true",expirationMs,TimeUnit.MILLISECONDS);
		}
		else
		{
			redisTemplate.opsForValue().set(BLACKLIST_PREFIX+token,"true",15,TimeUnit.MINUTES);
		}
	}
	public boolean isBlacklisted(String token)
	{
		return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX+token));
	}
	

}
