package com.flightapp.user_service.dto;

import java.util.Set;

import com.flightapp.user_service.validation.ValidPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @ValidPassword
    private String password;

    // e.g. ["ROLE_AIRLINE"]. ROLE_ADMIN will never be allowed via self-registration
    private Set<String> roles;
}
