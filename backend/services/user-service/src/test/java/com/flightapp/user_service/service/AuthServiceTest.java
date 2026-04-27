package com.flightapp.user_service.service;

import com.flightapp.user_service.dto.JwtResponse;
import com.flightapp.user_service.dto.LoginRequest;
import com.flightapp.user_service.dto.MessageResponse;
import com.flightapp.user_service.dto.SignupRequest;
import com.flightapp.user_service.model.*;
import com.flightapp.user_service.repository.AuditLogRepository;
import com.flightapp.user_service.repository.RoleRepository;
import com.flightapp.user_service.repository.UserRepository;
import com.flightapp.user_service.security.JwtUtils;
import com.flightapp.user_service.security.UserDetailsImplementation;
import jakarta.servlet.http.HttpServletRequest;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;



@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private UserRepository userRepository;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private AuditLogRepository auditLogRepository;

	@Mock
	private TokenBlacklistService tokenBlacklistService;

	@Mock
	private RefreshTokenService refreshTokenService;

	@Mock
	private PasswordEncoder encoder;

	@Mock
	private JwtUtils jwtUtils;

	@InjectMocks
	private AuthService authService;

	@Test
	public void testAuthenticateUser() {
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setUsername("testuser");
		loginRequest.setPassword("password");
		Authentication authentication = mock(Authentication.class);
		UserDetailsImplementation userDetails = new UserDetailsImplementation(1L, "testuser", "test@test.com",
				"password", false, Collections.emptyList());
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setToken("refresh-token");
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenReturn(authentication);
		when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");
		when(authentication.getPrincipal()).thenReturn(userDetails);
		when(refreshTokenService.createRefreshToken(anyLong())).thenReturn(refreshToken);
		JwtResponse response = authService.authenticateUser(loginRequest);
		assertNotNull(response);
		assertEquals("jwt-token", response.getToken());
		assertEquals("refresh-token", response.getRefreshToken());
		verify(auditLogRepository).save(any(AuditLog.class));
	}

	@Test
	public void testRegisterUserSuccessfull() {
		SignupRequest signupRequest = new SignupRequest();
		signupRequest.setUsername("newuser");
		signupRequest.setEmail("new@new.com");
		signupRequest.setPassword("Password@123");
		signupRequest.setRoles(Collections.singleton("user"));
		Role userRole = new Role();
		userRole.setName(RoleName.ROLE_USER);
		when(userRepository.existsByUsername(anyString())).thenReturn(false);
		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(encoder.encode(anyString())).thenReturn("encoded-password");
		when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
		authService.registerUser(signupRequest);
		verify(userRepository).save(any(User.class));
		verify(auditLogRepository).save(any(AuditLog.class));
	}

	@Test
	public void testRegisterUserFailureForDuplicateUsername() {
		SignupRequest signupRequest = new SignupRequest();
		signupRequest.setUsername("testuser");
		signupRequest.setEmail("new@new.com");
		signupRequest.setPassword("Password@123");
		when(userRepository.existsByUsername("testuser")).thenReturn(true);
		assertThrows(RuntimeException.class, () -> authService.registerUser(signupRequest));
	}

	@Test
	public void testRegisterUserFailureForDuplicateEmail() {
		SignupRequest signupRequest = new SignupRequest();
		signupRequest.setUsername("newuser");
		signupRequest.setEmail("existing@test.com");
		signupRequest.setPassword("Password@123");
		when(userRepository.existsByUsername(anyString())).thenReturn(false);
		when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);
		assertThrows(RuntimeException.class, () -> authService.registerUser(signupRequest));
	}

	@Test
	public void testRegisterUserWithDefaultRole() {
		SignupRequest signupRequest = new SignupRequest();
		signupRequest.setUsername("newuser");
		signupRequest.setEmail("new@test.com");
		signupRequest.setPassword("Password@123");
		signupRequest.setRoles(null); // Should default to ROLE_USER
		Role userRole = new Role();
		userRole.setName(RoleName.ROLE_USER);
		when(userRepository.existsByUsername(anyString())).thenReturn(false);
		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(encoder.encode(anyString())).thenReturn("encoded");
		when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
		authService.registerUser(signupRequest);
		verify(userRepository).save(any(User.class));
	}


	@Test
	public void testBlockUserSuccessfull() {
		String username = "targetuser";
		String adminName = "adminuser";
		User user = new User();
		user.setId(1L);
		user.setUsername(username);
		when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
		MessageResponse response = authService.blockUser(username, true, adminName);
		assertEquals("User blocked successfully.", response.getMessage());
		assertTrue(user.isBlocked());
		verify(tokenBlacklistService).blockUser(username);
		verify(refreshTokenService).deleteByUserId(user.getId());
	}

	@Test
	public void testUnblockUserSuccessful() {
		String username = "targetuser";
		String adminName = "adminuser";
		User user = new User();
		user.setId(1L);
		user.setUsername(username);
		when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
		MessageResponse response = authService.blockUser(username, false, adminName);
		assertEquals("User unblocked successfully.", response.getMessage());
		assertFalse(user.isBlocked());
		verify(tokenBlacklistService).unblockUser(username);
		verify(auditLogRepository).save(any(AuditLog.class));
	}

	@Test
	public void testBlockUserFailureForInvalidUser() {
		String username = "nonexistent";
		when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
		assertThrows(RuntimeException.class, () -> authService.blockUser(username, true, "admin"));
	}

	@Test
	public void testBlockUserSelfBlockDenied() {
		String username = "adminuser";
		String adminName = "adminuser";
		User user = new User();
		user.setUsername(username);
		when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
		assertThrows(RuntimeException.class, () -> authService.blockUser(username, true, adminName));
	}

	@Test
	public void testRefreshJwtTokenFailureInvalidToken() {
		com.flightapp.user_service.dto.TokenRefreshRequest request = new com.flightapp.user_service.dto.TokenRefreshRequest();
		request.setRefreshToken("invalid-token");
		when(refreshTokenService.findByToken("invalid-token")).thenReturn(Optional.empty());
		assertThrows(RuntimeException.class, () -> authService.refreshJwtToken(request));
	}

	@Test
	public void testRefreshJwtTokenSuccess() {
		com.flightapp.user_service.dto.TokenRefreshRequest request = new com.flightapp.user_service.dto.TokenRefreshRequest();
		request.setRefreshToken("valid-token");
		User user = new User();
		user.setId(1L);
		user.setUsername("testuser");
		user.setEmail("test@test.com");
		user.setRoles(new HashSet<>());
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setUser(user);
		refreshToken.setToken("valid-token");
		when(refreshTokenService.findByToken("valid-token")).thenReturn(Optional.of(refreshToken));
		when(refreshTokenService.verifyExpiration(any(RefreshToken.class))).thenReturn(refreshToken);
		when(jwtUtils.generateTokenFromUsername(anyString(), anyList())).thenReturn("new-jwt-token");
		JwtResponse response = authService.refreshJwtToken(request);
		assertNotNull(response);
		assertEquals("new-jwt-token", response.getToken());
		assertEquals("valid-token", response.getRefreshToken());
	}

	@Test
	public void testLogoutUser() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader("Authorization")).thenReturn("Bearer jwt-token");
		when(jwtUtils.getExpirationDateFromJwtToken("jwt-token")).thenReturn(new Date(System.currentTimeMillis() + 100000));

		Authentication authentication = mock(Authentication.class);
		UserDetailsImplementation userDetails = new UserDetailsImplementation(1L, "testuser", "test@test.com",
				"password", false, Collections.emptyList());
		when(authentication.getPrincipal()).thenReturn(userDetails);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		authService.logoutUser(request);
		verify(tokenBlacklistService).blacklistToken(eq("jwt-token"), anyLong());
		verify(refreshTokenService).deleteByUserId(1L);
		verify(auditLogRepository).save(any(AuditLog.class));
	}

	@Test
	public void testGetAllUsers() {
		when(userRepository.findAll()).thenReturn(Arrays.asList(new User(), new User()));
		List<User> users = authService.getAllUsers();
		assertEquals(2, users.size());
		verify(userRepository).findAll();
	}
}
