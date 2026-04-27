package com.flightapp.flight_service.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "flights")
@Data
@NoArgsConstructor
public class Flight {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String flightNumber;
	private String airline;
	@Column(name = "`from`")
	@Pattern(regexp = "^[A-Za-z' ]+$", message = "from city should have only letters")
	private String from;
	@Column(name = "`to`")
	@Pattern(regexp = "^[A-Za-z' ]+$", message = " to city should have only letters")
	private String to;
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	@NotNull(message = "Available seats is required")
	@Min(value = 1, message = "Available seats must be at least 1")
	private Integer availableSeats;
	@NotNull
	@Min(value = 1, message = "Ticket cost must be greater than 0")
	private Double ticketCost;
	private Boolean isBlocked;
}
