package com.flightapp.booking_service.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
public class Booking {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String pnr;
	private Long flightId;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String emailId;
	private Integer noOfSeats;

	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number. Must be 10 digits and start with 6-9")
	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	private MealType meal;

	@Enumerated(EnumType.STRING)
	private BookingStatus status;
	private LocalDateTime bookingDate;

	@OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Passenger> passengers = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		this.pnr = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
		this.bookingDate = LocalDateTime.now();
		this.status = BookingStatus.BOOKED;
	}

}
