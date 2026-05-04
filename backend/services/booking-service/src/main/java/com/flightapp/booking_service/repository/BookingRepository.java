package com.flightapp.booking_service.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flightapp.booking_service.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
	Optional<Booking> findByPnr(String pnr);

	Page<Booking> findByEmailId(String emailId, Pageable pageable);
}
