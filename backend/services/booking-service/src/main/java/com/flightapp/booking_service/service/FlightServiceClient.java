package com.flightapp.booking_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.flightapp.booking_service.dto.FlightDetails;
import com.flightapp.booking_service.dto.SeatUpdateRequest;

@Service
public class FlightServiceClient {
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${flight.service.url}")
	private String flightServiceUrl;

	public FlightDetails fetchFlightDetails(Long flightId) {
		return restTemplate.getForObject(flightServiceUrl + flightId, FlightDetails.class);
	}

	public void updateFlightSeats(Long flightId, int businessDelta, int nonBusinessDelta) {
		SeatUpdateRequest request = new SeatUpdateRequest(businessDelta, nonBusinessDelta);
		kafkaTemplate.send("flight-seat-update", flightId.toString(), request);
	}
}
