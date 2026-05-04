package com.flightapp.booking_service.dto;

import lombok.Data;

@Data
public class BookingResponse {
	private String pnr;
	private String status;
}
