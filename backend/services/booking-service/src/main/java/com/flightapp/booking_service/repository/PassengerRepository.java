package com.flightapp.booking_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flightapp.booking_service.model.Passenger;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
	boolean existsByFlightIdAndSeatNumber(Long flightId, String seatNumber);
}