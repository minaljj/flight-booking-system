package com.flightapp.user_service.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Jwts;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class JwtUtilsTest {

	private JwtUtils jwtUtils;
	private final String secret = "testSecretWithMinimumLengthOfThirtyTwoCharactersForKeySecurity";

	@BeforeEach
	public void setUp() {
		jwtUtils = new JwtUtils();
		ReflectionTestUtils.setField(jwtUtils, "jwtSecret", secret);
		ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 60000);
	}

	@Test
	public void testGenerateAndValidateToken() {
		UserDetailsImplementation userDetails = new UserDetailsImplementation(1L, "testuser", "test@test.com",
				"password", false, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());
		String token = jwtUtils.generateJwtToken(authentication);
		assertNotNull(token);
		assertTrue(jwtUtils.validateJwtToken(token));
		assertEquals("testuser", jwtUtils.getUserNameFromJwtToken(token));
	}

	@Test
	public void testInvalidToken() {
		assertFalse(jwtUtils.validateJwtToken("invalid.token.here"));
	}

	@Test
	public void testExpiredToken() {
		ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", -1000);
		String token = jwtUtils.generateTokenFromUsername("testuser", List.of("ROLE_USER"));
		assertFalse(jwtUtils.validateJwtToken(token));
	}
	

	@Test
	public void testGenerateJwtTokenWithNonUserDetailsPrincipal() {
		Authentication authentication = new UsernamePasswordAuthenticationToken("plainuser", null);
		String token = jwtUtils.generateJwtToken(authentication);
		assertNotNull(token);
		assertEquals("plainuser", jwtUtils.getUserNameFromJwtToken(token));
	}

	@Test
	public void testGetExpirationDateFromJwtToken() {
		String token = jwtUtils.generateTokenFromUsername("testuser", List.of("ROLE_USER"));
		Date expirationDate = jwtUtils.getExpirationDateFromJwtToken(token);
		assertNotNull(expirationDate);
		assertTrue(expirationDate.after(new Date()));
	}

	@Test
	public void testValidateJwtToken_IllegalArgument() {
		assertFalse(jwtUtils.validateJwtToken(""));
		assertFalse(jwtUtils.validateJwtToken(null));
	}

	@Test
	public void testValidateJwtToken_UnsupportedJwt() {
		String token = Jwts.builder().subject("user").compact();
		assertFalse(jwtUtils.validateJwtToken(token));
	}
}
