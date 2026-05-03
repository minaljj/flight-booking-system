package com.flightapp.booking_service.dto;

import lombok.Data;

@Data
public class FlightDetails {
	private Integer numberOfRows;
	private Integer numberOfColumns;
	private Integer totalBusinessSeats;
	private Integer totalNonBusinessSeats;
}
