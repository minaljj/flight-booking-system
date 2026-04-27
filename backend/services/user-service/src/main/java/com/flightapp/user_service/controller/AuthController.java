package com.flightapp.user_service.controller;

import com.flightapp.user_service.dto.BlockUserRequest;
import com.flightapp.user_service.dto.LoginRequest;
import com.flightapp.user_service.dto.SignupRequest;
import com.flightapp.user_service.dto.TokenRefreshRequest;
import com.flightapp.user_service.model.User;
import com.flightapp.user_service.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1.0/flight/auth")
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		return ResponseEntity.ok(authService.authenticateUser(loginRequest));
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		try {
			authService.registerUser(signUpRequest);
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
		try {
			return ResponseEntity.ok(authService.refreshJwtToken(request));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}

	
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }
    
	@PostMapping("/logout")
	public ResponseEntity<?> logoutUser(HttpServletRequest request) {
		authService.logoutUser(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PostMapping("/admin/block-user")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> blockUser(@Valid @RequestBody BlockUserRequest request, Authentication auth) {
		try {
			return ResponseEntity.ok(authService.blockUser(request.getUsername(), request.getBlock(), auth.getName()));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
}
