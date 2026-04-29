package com.flightapp.booking_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "passengers", uniqueConstraints = { @UniqueConstraint(columnNames = { "flightId", "seatNumber" }) })
@Data
@NoArgsConstructor
public class Passenger {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank(message = "Booking name is required")
	@Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces")
	private String name;
	private String gender;
	private Integer age;
	private String seatNumber;
	private Long flightId;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "booking_id", nullable = false)
	@JsonIgnore
	private Booking booking;

}
