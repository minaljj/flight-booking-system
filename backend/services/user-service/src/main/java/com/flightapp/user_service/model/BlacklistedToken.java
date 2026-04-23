package com.flightapp.user_service.model;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "blacklisted_tokens")
@Data
@NoArgsConstructor
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 1000)
    private String token;
    
    private Instant blacklistedAt;
    private Instant expiryDate;

    public BlacklistedToken(String token) {
        this.token = token;
        this.blacklistedAt = Instant.now();
    }
    public BlacklistedToken(String token, Instant expiryDate) {
        this.token = token;
        this.blacklistedAt = Instant.now();
        this.expiryDate = expiryDate;
    }
}
