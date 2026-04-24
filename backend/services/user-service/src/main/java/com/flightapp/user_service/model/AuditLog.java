package com.flightapp.user_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
public class AuditLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Instant timestamp;
	private String action;
	private String username;
	private String details;

	public AuditLog(String action, String username, String details) {
		this.timestamp = Instant.now();
		this.action = action;
		this.username = username;
		this.details = details;
	}
}
