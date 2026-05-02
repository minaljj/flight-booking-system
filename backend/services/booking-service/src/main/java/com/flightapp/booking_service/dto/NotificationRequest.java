package com.flightapp.booking_service.dto;

import lombok.Data;

@Data
public class NotificationRequest {
	private String eventType;
	private String emailId;
	private String phoneNumber;
	private String pnr;
	private Long flightId;
	private int noOfSeats;
}
