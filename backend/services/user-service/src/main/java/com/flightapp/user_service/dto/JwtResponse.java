package com.flightapp.user_service.dto;

import lombok.Data;

import java.util.List;

@Data

public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private String username;
	private String email;
	private String refreshToken;
	private Long id;
	private List<String> roles;

	public JwtResponse(String accessToken, String refreshToken, Long id, String username, String email,
			List<String> roles) {
		this.token = accessToken;
		this.refreshToken = refreshToken;
		this.id = id;
		this.roles = roles;
		this.username = username;
		this.email = email;
	}
}
