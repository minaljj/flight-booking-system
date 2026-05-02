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
			throw new IllegalArgumentException("Flight number already exists");
		}
		if (flight.getFrom().equalsIgnoreCase(flight.getTo())) {
			throw new IllegalArgumentException("From and To cities cannot be same");
		}
		if (flight.getEndDateTime().isBefore(flight.getStartDateTime())) {
			throw new IllegalArgumentException("End date time cannot be before start date time");
		}
		flight.setIsBlocked(false);
		return flightRepository.save(flight);
	}

	public List<Flight> searchFlights(FlightSearchRequest request) {

	    if(request.getFrom().equalsIgnoreCase(request.getTo())) {
	        throw new IllegalArgumentException("From and To cities cannot be same");
	    }

	    try {
			LocalDate date = LocalDate.parse(request.getDate());

			return flightRepository.findByFromIgnoreCaseAndToIgnoreCaseAndStartDateTimeBetweenAndIsBlockedFalse(
					request.getFrom(), request.getTo(), date.atStartOfDay(), date.plusDays(1).atStartOfDay()).stream()
					.filter(flight -> (flight.getTotalBusinessSeats() + flight.getTotalNonBusinessSeats()) > 0).toList();
		} catch (Exception exception) {
			return List.of();
		}
	}

	public Flight getFlightById(Long id) {
		return flightRepository.findById(id).orElse(null);
	}
	public void updateSeats(Long flightId, int businessDelta, int nonBusinessDelta) {
		Flight flight = flightRepository.findById(flightId).orElse(null);
		if (flight != null) {
			flight.setTotalBusinessSeats(flight.getTotalBusinessSeats() - businessDelta);
			flight.setTotalNonBusinessSeats(flight.getTotalNonBusinessSeats() - nonBusinessDelta);
			flightRepository.save(flight);
		}
}
}