package com.flightapp.user_service.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
public class AuditLog {
@Id
@GeneratedValue(strategy= GenerationType.IDENTITY)
private Long id;
private Instant timestamp;
private String action;
private String username;
private String details;

public AuditLog(String action, String username, String details) {
	this.action = action;
	this.username=username;
	this.details = details;
	this.timestamp=Instant.now();
}
}
