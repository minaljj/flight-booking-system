package com.flightapp.user_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix="app.auth")
@Data
public class PasswordProperties {
	private String passwordRegex;
	private String passwordMessage;

}
