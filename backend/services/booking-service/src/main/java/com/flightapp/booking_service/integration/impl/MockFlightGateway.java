package com.flightapp.booking_service.integration.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.flightapp.booking_service.dto.FlightDetails;
import com.flightapp.booking_service.dto.SeatUpdateRequest;
import com.flightapp.booking_service.integration.FlightGateway;

@Service
@Profile("local")
public class MockFlightGateway implements FlightGateway {
	private final KafkaTemplate<String, Object> kafkaTemplate;

	public MockFlightGateway(KafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	@Override
	public FlightDetails getFlightDetails(Long flightId) {
		FlightDetails flight = new FlightDetails();
		flight.setNumberOfRows(30);
		flight.setNumberOfColumns(6);
		return flight;
	}

	@Override
	public void updateSeats(Long flightId, int businessDelta, int nonBusinessDelta) {
		SeatUpdateRequest request = new SeatUpdateRequest(businessDelta, nonBusinessDelta);
		kafkaTemplate.send("flight-seat-update", flightId.toString(), request);

	}
}
