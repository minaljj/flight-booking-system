package com.flightapp.booking_service.integration;

import com.flightapp.booking_service.dto.FlightDetails;

public interface FlightGateway {
	FlightDetails getFlightDetails(Long flightId);

	void updateSeats(Long flightId, int delta);
}
