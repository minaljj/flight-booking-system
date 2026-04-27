package com.flightapp.user_service.controller;

import com.flightapp.user_service.config.PasswordProperties;
import com.flightapp.user_service.dto.JwtResponse;
import com.flightapp.user_service.dto.LoginRequest;
import com.flightapp.user_service.dto.MessageResponse;
import com.flightapp.user_service.dto.SignupRequest;
import com.flightapp.user_service.dto.TokenRefreshRequest;
import com.flightapp.user_service.exception.GlobalExceptionHandler;
import com.flightapp.user_service.security.JwtUtils;
import com.flightapp.user_service.security.WebSecurityConfig;
import com.flightapp.user_service.service.AuthService;
import com.flightapp.user_service.service.UserDetailsServiceImplementation;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({ PasswordProperties.class, WebSecurityConfig.class, GlobalExceptionHandler.class })
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthService authService;

	@MockBean
	private JwtUtils jwtUtils;

	@MockBean
	private UserDetailsServiceImplementation userDetailsService;

	@Test
	public void testLoginSuccessfull() throws Exception {
		JwtResponse response = new JwtResponse("dummy-jwt", "refresh", 1L, "testuser", "test@test.com",
				Collections.singletonList("ROLE_USER"));

		Mockito.when(authService.authenticateUser(Mockito.any(LoginRequest.class))).thenReturn(response);

		String json = "{\"username\":\"testuser\", \"password\":\"pass\"}";

		mockMvc.perform(post("/api/v1.0/flight/auth/login").contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(json)).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "testuser")
	public void testLogoutUser() throws Exception {
		mockMvc.perform(post("/api/v1.0/flight/auth/logout").header("Authorization", "Bearer dummy-token")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void testRegisterSuccessfull() throws Exception {
		Mockito.doNothing().when(authService).registerUser(Mockito.any(SignupRequest.class));

		String json = "{\"username\":\"testuser\", \"email\":\"test@test.com\", \"password\":\"Password@123\"}";

		mockMvc.perform(post("/api/v1.0/flight/auth/register").contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(json)).andExpect(status().isCreated());
	}

	@Test
	public void testRegisterFailure() throws Exception {
		Mockito.doThrow(new RuntimeException()).when(authService).registerUser(Mockito.any(SignupRequest.class));

		String json = "{\"username\":\"testuser\", \"email\":\"test@test.com\", \"password\":\"Password@123\"}";

		mockMvc.perform(post("/api/v1.0/flight/auth/register").contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(json)).andExpect(status().isBadRequest());
	}

	@Test
	public void testRegisterForEmptyUsername() throws Exception {
		Mockito.doNothing().when(authService).registerUser(Mockito.any(SignupRequest.class));

		String json = "{\"username\":\"\", \"email\":\"test@email.com\", \"password\":\"Password@123\"}";

		mockMvc.perform(post("/api/v1.0/flight/auth/register").contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(json)).andExpect(status().isBadRequest());

	}

	@Test
	public void testRegisterForNoUsername() throws Exception {
		Mockito.doNothing().when(authService).registerUser(Mockito.any(SignupRequest.class));

		String json = "{ \"email\":\"test@email.com\", \"password\":\"Password@123\"}";

		mockMvc.perform(post("/api/v1.0/flight/auth/register").contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(json)).andExpect(status().isBadRequest());

	}

	@Test
	public void testRegisterForBlankUsername() throws Exception {
		Mockito.doNothing().when(authService).registerUser(Mockito.any(SignupRequest.class));

		String json = "{\"username\":\"        \", \"email\":\"test@email.com\", \"password\":\"Password@123\"}";

		mockMvc.perform(post("/api/v1.0/flight/auth/register").contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(json)).andExpect(status().isBadRequest());

	}

	@Test
	public void testRegisterForBadEmailFormat() throws Exception {
		Mockito.doNothing().when(authService).registerUser(Mockito.any(SignupRequest.class));

		String json = "{\"username\":\"testuser\", \"email\":\"testemailcom\", \"password\":\"Password@123\"}";

		mockMvc.perform(post("/api/v1.0/flight/auth/register").contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(json)).andExpect(status().isBadRequest());

	}

	@Test
	public void testRegisterForEmptyEmail() throws Exception {
		Mockito.doNothing().when(authService).registerUser(Mockito.any(SignupRequest.class));

		String json = "{\"username\":\"testuser\", \"email\":\"\", \"password\":\"Password@123\"}";

		mockMvc.perform(post("/api/v1.0/flight/auth/register").contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(json)).andExpect(status().isBadRequest());

	}

	@Test
	public void testRegisterForNoEmail() throws Exception {
		Mockito.doNothing().when(authService).registerUser(Mockito.any(SignupRequest.class));

		String json = "{\"username\":\"testuser\",  \"password\":\"Password@123\"}";

		mockMvc.perform(post("/api/v1.0/flight/auth/register").contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(json)).andExpect(status().isBadRequest());

	}

	@Test
	public void testRegisterForBlankEmail() throws Exception {
		Mockito.doNothing().when(authService).registerUser(Mockito.any(SignupRequest.class));

		String json = "{\"username\":\"testuser\", \"email\":\"            \", \"password\":\"Password@123\"}";

		mockMvc.perform(post("/api/v1.0/flight/auth/register").contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(json)).andExpect(status().isBadRequest());

	}

	@Test
	public void testRegisterForInvalidPasswords() throws Exception {
		Mockito.doNothing().when(authService).registerUser(Mockito.any(SignupRequest.class));

		String json = "{\"username\":\"testuser\", \"email\":\"test@emailcom\", \"password\":\"password@123\"}";

		mockMvc.perform(post("/api/v1.0/flight/auth/register").contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(json)).andExpect(status().isBadRequest());
		json = "{\"username\":\"testuser\", \"email\":\"test@emailcom\", \"password\":\"password@\"}";
		mockMvc.perform(post("/api/v1.0/flight/auth/register").contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(json)).andExpect(status().isBadRequest());
		json = "{\"username\":\"testuser\", \"email\":\"test@emailcom\", \"password\":\"password123\"}";
		mockMvc.perform(post("/api/v1.0/flight/auth/register").contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(json)).andExpect(status().isBadRequest());

	}

	@Test
	public void testRegisterForEmptyPassword() throws Exception {
		Mockito.doNothing().when(authService).registerUser(Mockito.any(SignupRequest.class));

		String json = "{\"username\":\"testuser\", \"email\":\"test@emailcom\", \"password\":\"password@123\"}";

		mockMvc.perform(post("/api/v1.0/flight/auth/register").contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(json)).andExpect(status().isBadRequest());

	}

	@Test
	public void testRegisterForNoPassword() throws Exception {
		Mockito.doNothing().when(authService).registerUser(Mockito.any(SignupRequest.class));

		String json = "{\"username\":\"testuser\", \"email\":\"test@emailcom\", \"password\":\"\"}";

		mockMvc.perform(post("/api/v1.0/flight/auth/register").contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(json)).andExpect(status().isBadRequest());

	}

	@Test
	public void testRegisterForBlankPassword() throws Exception {
		Mockito.doNothing().when(authService).registerUser(Mockito.any(SignupRequest.class));

		String json = "{\"username\":\"testuser\", \"email\":\"test@emailcom\", \"password\":\"        \"}";

		mockMvc.perform(post("/api/v1.0/flight/auth/register").contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(json)).andExpect(status().isBadRequest());

	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	public void testBlockUserForAdminRole() throws Exception {
		Mockito.when(authService.blockUser(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyString()))
				.thenReturn(new MessageResponse("Success"));
		String json = "{\"username\":\"testuser\", \"block\":true}";
		mockMvc.perform(post("/api/v1.0/flight/auth/admin/block-user").contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON));
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	public void testBlockUserForUserRole() throws Exception {
		Mockito.when(authService.blockUser(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyString()))
				.thenReturn(new MessageResponse("Success"));
		String json = "{\"username\":\"testuser\", \"block\":true}";
		mockMvc.perform(post("/api/v1.0/flight/auth/admin/block-user").contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	public void testRefreshToken() throws Exception {
		JwtResponse response = new JwtResponse("new-jwt", "refresh-token", 1L, "testuser", "test@test.com",
				Collections.singletonList("ROLE_USER"));
		Mockito.when(authService.refreshJwtToken(Mockito.any(TokenRefreshRequest.class))).thenReturn(response);
		String json = "{\"refreshToken\":\"refresh-token\"}";
		mockMvc.perform(post("/api/v1.0/flight/auth/refresh").contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(json)).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	public void testRefreshTokenFailure() throws Exception {
		Mockito.when(authService.refreshJwtToken(Mockito.any(TokenRefreshRequest.class)))
				.thenThrow(new RuntimeException());
		String json = "{\"refreshToken\":\"invalid-token\"}";
		mockMvc.perform(post("/api/v1.0/flight/auth/refresh").contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(json)).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = { "ADMIN" })
	public void testGetAllUsersForAdminRole() throws Exception {
		Mockito.when(authService.getAllUsers()).thenReturn(Collections.emptyList());
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0/flight/auth/admin/users")).andExpect(status().isOk())
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(roles = { "USER" })
	public void testGetAllUsersForUserRole() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0/flight/auth/admin/users"))
				.andExpect(status().isForbidden());
	}
}