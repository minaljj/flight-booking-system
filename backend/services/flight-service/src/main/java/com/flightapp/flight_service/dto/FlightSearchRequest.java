package com.flightapp.flight_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class FlightSearchRequest {

	@NotBlank(message = "From city is required")
	@Pattern(regexp = "^[A-Za-z' ]+$", message = "From city should contain only letters")
	private String from;

	@NotBlank(message = "To city is required")
	@Pattern(regexp = "^[A-Za-z' ]+$", message = "To city should contain only letters")
	private String to;
	private String date;
}