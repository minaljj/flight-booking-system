package com.flightapp.api_gateway.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${jwt.secret}")
	private String jwtSecret;

	private SecretKey signingKey() {
		return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token).getPayload().getSubject();
	}

	@SuppressWarnings("unchecked")
	public List<String> getRolesFromJwtToken(String token) {
		return (List<String>) Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token).getPayload()
				.get("roles");
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().verifyWith(signingKey()).build().parse(authToken);
			return true;
		} catch (MalformedJwtException malformedJwtException) {
			logger.error("Invalid JWT token: {}", malformedJwtException.getMessage());
		} catch (ExpiredJwtException expiredJwtException) {
			logger.error("JWT token is expired: {}", expiredJwtException.getMessage());
		} catch (UnsupportedJwtException unsupportedJwtException) {
			logger.error("JWT token is unsupported: {}", unsupportedJwtException.getMessage());
		} catch (IllegalArgumentException illegalArgumentException) {
			logger.error("JWT claims string is empty: {}", illegalArgumentException.getMessage());
		} catch (SignatureException signatureException) {
			logger.error("Invalid JWT signature: {}", signatureException.getMessage());
		}
		return false;
	}
}
