package com.flightapp.api_gateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private final String testSecret = "mySecretKey12345678901234567890123456789012"; 
    private SecretKey key;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", testSecret);
        key = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Should return true when validating a properly signed active token")
    void validateJwtTokenWithValidTokenReturnsTrue() {
        String token = Jwts.builder()
                .subject("testUser")
                .issuedAt(new Date())
                .signWith(key)
                .compact();

        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    @DisplayName("Should return correct username from subject claim")
    void getUserNameFromJwtTokenReturnsCorrectSubject() {
        String username = "flightAdmin";
        String token = Jwts.builder()
                .subject(username)
                .signWith(key)
                .compact();

        assertEquals(username, jwtUtils.getUserNameFromJwtToken(token));
    }

    @Test
    @DisplayName("Should extract roles list from custom claims")
    void getRolesFromJwtTokenReturnsRolesList() {
        List<String> roles = List.of("ROLE_USER", "ROLE_FLIGHT_MANAGER");
        String token = Jwts.builder()
                .claim("roles", roles)
                .signWith(key)
                .compact();

        List<String> result = jwtUtils.getRolesFromJwtToken(token);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("ROLE_FLIGHT_MANAGER"));
    }

    @Test
    @DisplayName("Should return false when token has expired")
    void validateJwtTokenWithExpiredTokenReturnsFalse() {
        String token = Jwts.builder()
                .subject("expiredUser")
                .expiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(key)
                .compact();

        assertFalse(jwtUtils.validateJwtToken(token));
    }

    @Test
    @DisplayName("Should return false when signature is invalid or tampered")
    void validateJwtTokenWithTamperedSignatureReturnsFalse() {
        SecretKey fakeKey = Keys.hmacShaKeyFor("wrongSecretKeyForTesting123456789012".getBytes());
        String token = Jwts.builder()
                .subject("user")
                .signWith(fakeKey)
                .compact();

        assertFalse(jwtUtils.validateJwtToken(token));
    }

    @Test
    @DisplayName("Should return false for malformed JWT strings")
    void validateJwtTokenWithMalformedStringReturnsFalse() {
        assertFalse(jwtUtils.validateJwtToken("header.payload.signature.extra"));
        assertFalse(jwtUtils.validateJwtToken("not-a-jwt"));
    }

    @Test
    @DisplayName("Should return false for empty or null strings")
    void validateJwtTokenWithEmptyOrNullStringsReturnsFalse() {
        assertFalse(jwtUtils.validateJwtToken(""));
        assertFalse(jwtUtils.validateJwtToken(null));
    }

    @Test
    @DisplayName("Should throw exception when parsing username from an unsigned token")
    void getUserNameFromJwtTokenWithUnsignedTokenThrowsException() {
        String unsignedToken = Jwts.builder()
                .subject("user")
                .compact();

        assertThrows(Exception.class, () -> jwtUtils.getUserNameFromJwtToken(unsignedToken));
    }

    @Test
    @DisplayName("Should return null if roles claim is missing")
    void getRolesFromJwtTokenWithMissingRolesClaimReturnsNull() {
        String token = Jwts.builder()
                .subject("user")
                .signWith(key)
                .compact();

        assertNull(jwtUtils.getRolesFromJwtToken(token));
    }
}