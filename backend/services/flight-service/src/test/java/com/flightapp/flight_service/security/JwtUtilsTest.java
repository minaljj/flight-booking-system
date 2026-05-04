package com.flightapp.flight_service.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtUtilsTest {
	private JwtUtils jwtUtils;
	private String secret;
	private SecretKey key;
	private String token;
	@BeforeEach
	void setUp() {
		jwtUtils=new JwtUtils();
		secret = "mySecretKeyForJwtTokenTesting12345678901234567890";
		ReflectionTestUtils.setField(jwtUtils,"jwtSecret",secret);
		key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		token=Jwts.builder()
				.subject("testuser")
				.claim("roles",List.of("ROLE_ADMIN","ROLE_USER"))
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis()+60000))
				.signWith(key)
				.compact();
		
	}
	@Test
	void testGetUserNameFromJwtToken()
	{
		String username=jwtUtils.getUserNameFromJwtToken(token);
		assertEquals("testuser",username);
	}
	@Test
	void testGetRolesFromToken()
	{
		List<String> roles=jwtUtils.getRolesFromJwtToken(token);
		assertNotNull(roles);
		assertEquals(2, roles.size());
		assertEquals("ROLE_ADMIN",roles.get(0));
		assertEquals("ROLE_USER",roles.get(1));
	}
	@Test
	void testValidateJwtToken()
	{
		boolean result=jwtUtils.validateJwtToken(token);
		assertTrue(result);
	}
}
