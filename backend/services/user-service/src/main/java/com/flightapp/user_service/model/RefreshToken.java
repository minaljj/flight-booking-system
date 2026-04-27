package com.flightapp.user_service.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class RefreshToken {
    private Long id;
    private User user;
    private String token;
    private Instant expiryDate;
}
