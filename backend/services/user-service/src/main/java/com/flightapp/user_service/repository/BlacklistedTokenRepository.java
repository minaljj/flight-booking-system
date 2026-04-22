package com.flightapp.user_service.repository;

import com.flightapp.user_service.model.BlacklistedToken;

import jakarta.transaction.Transactional;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    boolean existsByToken(String token);
    
    @Transactional
    void deleteAllByExpiryDateBefore(Instant now);
}
