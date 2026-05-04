package com.flightapp.booking_service.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class BookingHistoryResponse {
	private String pnr;
	private Long flightId;
	private String status;
	private LocalDateTime bookingDate;
	private int noOfSeats;
	private List<SeatInfoResponse> seats;
}
