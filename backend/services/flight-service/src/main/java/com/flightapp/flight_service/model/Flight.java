package com.flightapp.flight_service.model;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
	@NotNull(message = "Start date time is required")
	private LocalDateTime startDateTime;
	@NotNull(message = "End date time is required")
	private LocalDateTime endDateTime;
	@NotBlank(message = "Scheduled days is required")
	private String scheduledDays;
	@NotBlank(message = "Instrument used is required")
	private String instrumentUsed;
	@NotNull(message = "Total business seats is required")
	@Min(value = 0, message = "Total business seats cannot be negative")
	private Integer totalBusinessSeats;
	@NotNull(message = "Total non-business seats is required")
	@Min(value = 1, message = "Total non-business seats must be at least 1")
	private Integer totalNonBusinessSeats;
	@Enumerated(EnumType.STRING)
	private MealType meal;
	@NotNull
	@Min(value=5,message="Minimum 5 rows required")
	private Integer numberOfRows;
    private Integer numberOfColumns = (totalBusinessSeats+totalNonBusinessSeats)/numberOfRows;
	@NotNull
	@Min(value = 1, message = "Ticket cost must be greater than 0")
	private Double ticketCost;
	private Boolean isBlocked;
}
