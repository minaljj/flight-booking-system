package com.flightapp.booking_service.integration.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.flightapp.booking_service.dto.FlightDetails;
import com.flightapp.booking_service.integration.FlightGateway;

@Service
@Profile("local")
public class MockFlightGateway implements FlightGateway {

	@Override
	public FlightDetails getFlightDetails(Long flightId) {
		FlightDetails flight = new FlightDetails();
		flight.setNumberOfRows(30);
		flight.setNumberOfColumns(6);
		return flight;
	}

	@Override
	public void updateSeats(Long flightId, int delta) {

	}
}
