package com.flightapp.booking_service.integration.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.flightapp.booking_service.dto.FlightDetails;
import com.flightapp.booking_service.integration.FlightGateway;
import com.flightapp.booking_service.service.FlightServiceClient;

@Service
@Profile("integration")
public class RealFlightGateway implements FlightGateway {

	@Autowired
	private FlightServiceClient flightServiceClient;

	@Override
	public FlightDetails getFlightDetails(Long flightId) {
		return flightServiceClient.fetchFlightDetails(flightId);
	}

	@Override
	public void updateSeats(Long flightId, int businessDelta, int nonBusinessDelta) {
		flightServiceClient.updateFlightSeats(flightId, businessDelta, nonBusinessDelta);
	}

}