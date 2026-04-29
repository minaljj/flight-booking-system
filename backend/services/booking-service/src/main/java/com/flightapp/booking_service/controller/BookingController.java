package com.flightapp.booking_service.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flightapp.booking_service.dto.BookingHistoryResponse;
import com.flightapp.booking_service.dto.BookingResponse;
import com.flightapp.booking_service.model.Booking;
import com.flightapp.booking_service.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1.0/flight/booking")
public class BookingController {

	@Autowired
	private BookingService bookingService;

	@PostMapping("/{flightId}")
	public ResponseEntity<?> bookFlight(@PathVariable Long flightId, @Valid @RequestBody Booking booking,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(errors);
		}

		BookingResponse response = bookingService.bookFlight(flightId, booking);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/history/{emailId}")
	public ResponseEntity<List<BookingHistoryResponse>> getBookingHistory(@PathVariable String emailId) {
		return ResponseEntity.ok(bookingService.getBookingHistory(emailId));
	}

	@DeleteMapping("/cancel/{pnr}")
	public ResponseEntity<?> cancelBooking(@PathVariable String pnr) {
		try {
			bookingService.cancelBooking(pnr);
			return ResponseEntity.ok("Booking cancelled!");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/ticket/{pnr}")
	public ResponseEntity<?> getTicketDetails(@PathVariable String pnr) {
		return bookingService.getTicketDetails(pnr).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

}
