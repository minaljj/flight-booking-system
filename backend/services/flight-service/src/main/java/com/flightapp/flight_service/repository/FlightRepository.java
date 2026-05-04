package com.flightapp.flight_service.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.flightapp.flight_service.model.Flight;

public interface FlightRepository extends JpaRepository<Flight, Long> {
	Page<Flight> findByFromIgnoreCaseAndToIgnoreCaseAndStartDateTimeBetweenAndIsBlockedFalse(String from, String to, LocalDateTime start,
			LocalDateTime end, Pageable pageable);


	boolean existsByFlightNumber(String flightNumber);
}