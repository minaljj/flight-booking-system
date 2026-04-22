package com.flightapp.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class SignupRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @jakarta.validation.constraints.Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Password must be at least 8 characters long, contain at least one digit, one uppercase letter, one lowercase letter, one special character, and no whitespace"
    )
    private String password;

    // Optional: e.g. ["ROLE_AIRLINE"]. ROLE_ADMIN is never allowed via self-registration.
    private Set<String> roles;
}
