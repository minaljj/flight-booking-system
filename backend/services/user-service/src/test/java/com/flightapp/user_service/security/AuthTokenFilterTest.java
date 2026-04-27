package com.flightapp.user_service.security;

import com.flightapp.user_service.service.UserDetailsServiceImplementation;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthTokenFilterTest {

	@InjectMocks
	private AuthTokenFilter authTokenFilter;

	@Mock
	private JwtUtils jwtUtils;

	@Mock
	private UserDetailsServiceImplementation userDetailsService;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain filterChain;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		SecurityContextHolder.clearContext();
	}

	@Test
	public void testDoFilterInternalValidToken() throws Exception {
		String jwt = "valid-jwt";
		String username = "testuser";
		UserDetails userDetails = mock(UserDetails.class);
		when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
		when(jwtUtils.validateJwtToken(jwt)).thenReturn(true);
		when(jwtUtils.getUserNameFromJwtToken(jwt)).thenReturn(username);
		when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
		when(userDetails.isAccountNonLocked()).thenReturn(true);
		authTokenFilter.doFilterInternal(request, response, filterChain);
		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
		verify(filterChain).doFilter(request, response);
	}

	@Test
	public void testDoFilterInternalInvalidToken() throws Exception {
		String jwt = "invalid-jwt";
		when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
		when(jwtUtils.validateJwtToken(jwt)).thenReturn(false);
		authTokenFilter.doFilterInternal(request, response, filterChain);
		assertNull(SecurityContextHolder.getContext().getAuthentication());
		verify(filterChain).doFilter(request, response);
	}

	@Test
	public void testDoFilterInternalBlockedUser() throws Exception {
		String jwt = "valid-jwt";
		String username = "blockeduser";
		UserDetails userDetails = mock(UserDetails.class);
		when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
		when(jwtUtils.validateJwtToken(jwt)).thenReturn(true);
		when(jwtUtils.getUserNameFromJwtToken(jwt)).thenReturn(username);
		when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
		when(userDetails.isAccountNonLocked()).thenReturn(false);
		authTokenFilter.doFilterInternal(request, response, filterChain);
		assertNull(SecurityContextHolder.getContext().getAuthentication());
		verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
		verify(filterChain, never()).doFilter(request, response);
	}
}
