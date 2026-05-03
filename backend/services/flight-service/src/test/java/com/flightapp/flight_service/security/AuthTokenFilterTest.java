package com.flightapp.flight_service.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

class AuthTokenFilterTest {

	private AuthTokenFilter authTokenFilter;
	private JwtUtils jwtUtils;

	@BeforeEach
	void setUp() {
		authTokenFilter = new AuthTokenFilter();
		jwtUtils = mock(JwtUtils.class);
		ReflectionTestUtils.setField(authTokenFilter, "jwtUtils", jwtUtils);
		SecurityContextHolder.clearContext();

	}

	@Test
	void testValidToken() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer validtoken");
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain filterChain = new MockFilterChain();
		when(jwtUtils.validateJwtToken("validtoken")).thenReturn(true);
		when(jwtUtils.getUserNameFromJwtToken("validtoken")).thenReturn("test user");
		when(jwtUtils.getRolesFromJwtToken("validtoken")).thenReturn(List.of("ROLE_ADMIN"));
		authTokenFilter.doFilterInternal(request, response, filterChain);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		assertNotNull(authentication);
		assertEquals("test user", authentication.getPrincipal());
		assertEquals(1, authentication.getAuthorities().size());

	}

	@Test
	void testNoAuthorizationHeader() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain filterChain = new MockFilterChain();
		authTokenFilter.doFilterInternal(request, response, filterChain);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		assertEquals(null, authentication);
		verify(jwtUtils, never()).validateJwtToken(anyString());

	}

	@Test
	void testAuthorizationHeaderWithoutBearer() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "validtoken");
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain filterChain = new MockFilterChain();
		authTokenFilter.doFilterInternal(request, response, filterChain);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		assertEquals(null, authentication);
		verify(jwtUtils, never()).validateJwtToken(anyString());
	}

	@Test
	void testExceptionWhileReadingToken() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer validtoken");
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain filterChain = new MockFilterChain();
		when(jwtUtils.validateJwtToken("validtoken")).thenReturn(true);
		when(jwtUtils.getUserNameFromJwtToken("validtoken")).thenThrow(new RuntimeException("error"));
		authTokenFilter.doFilterInternal(request, response, filterChain);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		assertEquals(null, authentication);
	}

}