package com.flightapp.flight_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.flightapp.flight_service.dto.FlightSearchRequest;
import com.flightapp.flight_service.model.Flight;
import com.flightapp.flight_service.repository.FlightRepository;

@Service
public class FlightService {

	@Autowired
	private FlightRepository flightRepository;

	public Flight addInventory(Flight flight) {
		if (flightRepository.existsByFlightNumber(flight.getFlightNumber())) {
			throw new IllegalArgumentException();
		}
		if (flight.getFrom().equalsIgnoreCase(flight.getTo())) {
			throw new IllegalArgumentException();
		}
		if (flight.getEndDateTime().isBefore(flight.getStartDateTime())) {
			throw new IllegalArgumentException();
		}
		flight.setIsBlocked(false);
		return flightRepository.save(flight);
	}

	public List<Flight> searchFlights(FlightSearchRequest request) {

		LocalDate date = LocalDate.parse(request.getDate());

		LocalDateTime start = date.atStartOfDay();
		LocalDateTime end = date.atTime(23, 59, 59);

		return flightRepository.findByFromIgnoreCaseAndToIgnoreCaseAndStartDateTimeBetween(request.getFrom(),
				request.getTo(), start, end);
	}

	public Flight getFlightById(Long id) {
		return flightRepository.findById(id).orElse(null);
	}
}