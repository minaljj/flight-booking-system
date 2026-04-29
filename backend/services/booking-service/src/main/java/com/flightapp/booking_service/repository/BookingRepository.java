package com.flightapp.booking_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flightapp.booking_service.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
	Optional<Booking> findByPnr(String pnr);

	List<Booking> findByEmailId(String emailId);
}
