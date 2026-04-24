package com.flightapp.flight_service.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flightapp.flight_service.model.Flight;

public interface FlightRepository extends JpaRepository<Flight, Long> {
	List<Flight> findByFromIgnoreCaseAndToIgnoreCaseAndStartDateTimeBetween(String from, String to, LocalDateTime start,
			LocalDateTime end);

	boolean existsByFlightNumber(String flightNumber);
}