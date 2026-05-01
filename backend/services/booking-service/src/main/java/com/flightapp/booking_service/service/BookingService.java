package com.flightapp.booking_service.service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.flightapp.booking_service.dto.BookingHistoryResponse;
import com.flightapp.booking_service.dto.BookingResponse;
import com.flightapp.booking_service.dto.FlightDetails;
import com.flightapp.booking_service.dto.NotificationRequest;
import com.flightapp.booking_service.dto.SeatInfoResponse;
import com.flightapp.booking_service.exception.SeatAlreadyBookedException;
import com.flightapp.booking_service.integration.FlightGateway;
import com.flightapp.booking_service.integration.NotificationGateway;
import com.flightapp.booking_service.model.Booking;
import com.flightapp.booking_service.model.BookingStatus;
import com.flightapp.booking_service.model.SeatClass;
import com.flightapp.booking_service.repository.BookingRepository;

@Service
public class BookingService {
	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private FlightGateway flightGateway;

	@Autowired
	private NotificationGateway notificationGateway;

	public BookingResponse bookFlight(Long flightId, Booking booking) {

		FlightDetails flight = flightGateway.getFlightDetails(flightId);

		booking.setFlightId(flightId);

		if (booking.getPassengers() != null) {
			booking.setNoOfSeats(booking.getPassengers().size());
			for (var passenger : booking.getPassengers()) {
				passenger.setBooking(booking);
				passenger.setFlightId(flightId);
				validateSeat(passenger.getSeatNumber(), flight);
			}
		}
		try {
			Booking saved = bookingRepository.save(booking);
			int businessDelta = 0;
			int nonBusinessDelta = 0;
			for (var passenger : saved.getPassengers()) {
				if (SeatClass.BUSINESS.equals(passenger.getSeatClass())) {
					businessDelta++;
				} else {
					nonBusinessDelta++;
				}
			}
			flightGateway.updateSeats(flightId, businessDelta, nonBusinessDelta);

			NotificationRequest notification = new NotificationRequest();
			notification.setEventType("BOOKING_CONFIRMED");
			notification.setEmailId(saved.getEmailId());
			notification.setPhoneNumber(saved.getPhoneNumber());
			notification.setPnr(saved.getPnr());
			notification.setNoOfSeats(saved.getNoOfSeats());
			notification.setFlightId(saved.getFlightId());
			notificationGateway.sendNotification(notification);

			BookingResponse response = new BookingResponse();
			response.setPnr(saved.getPnr());
			response.setStatus(saved.getStatus().name());
			return response;

		} catch (DataIntegrityViolationException e) {
			throw new SeatAlreadyBookedException("Seat already booked. Please choose a different seat.");
		}
	}

	private void validateSeat(String seatNumber, FlightDetails flight) {
		if (seatNumber == null || seatNumber.isEmpty()) {
			throw new RuntimeException("Seat number is required");
		}

		Pattern pattern = Pattern.compile("^(\\d+)([A-Z])$");
		Matcher matcher = pattern.matcher(seatNumber);

		if (!matcher.matches()) {
			throw new RuntimeException("Invalid seat format: " + seatNumber + ". Expected format like '12A'");
		}

		int row = Integer.parseInt(matcher.group(1));
		char colChar = matcher.group(2).charAt(0);
		int col = colChar - 'A' + 1;

		if (row < 1 || row > flight.getNumberOfRows()) {
			throw new RuntimeException(
					"Row " + row + " is out of bounds for this flight (Max rows: " + flight.getNumberOfRows() + ")");
		}

		if (col < 1 || col > flight.getNumberOfColumns()) {
			throw new RuntimeException("Column " + colChar + " is out of bounds for this flight (Max columns: "
					+ flight.getNumberOfColumns() + ")");
		}
	}

	public List<BookingHistoryResponse> getBookingHistory(String emailId) {

		return bookingRepository.findByEmailId(emailId).stream().map(b -> {
			BookingHistoryResponse dto = new BookingHistoryResponse();
			dto.setPnr(b.getPnr());
			dto.setFlightId(b.getFlightId());
			dto.setStatus(b.getStatus().name());
			dto.setBookingDate(b.getBookingDate());
			dto.setNoOfSeats(b.getNoOfSeats());

			List<SeatInfoResponse> seats = b.getPassengers().stream().map(p -> {
				SeatInfoResponse s = new SeatInfoResponse();
				s.setSeatNumber(p.getSeatNumber());
				return s;
			}).toList();

			dto.setSeats(seats);
			return dto;
		}).toList();
	}

	public void cancelBooking(String pnr) {
		Booking booking = bookingRepository.findByPnr(pnr)
				.orElseThrow(() -> new RuntimeException("Booking not found!"));
		int businessDelta = 0;
		int nonBusinessDelta = 0;
		for (var p : booking.getPassengers()) {
			if (SeatClass.BUSINESS.equals(p.getSeatClass())) {
				businessDelta--;
			} else {
				nonBusinessDelta--;
			}
		}
		booking.getPassengers().clear();
		booking.setStatus(BookingStatus.CANCELLED);
		Booking updated = bookingRepository.save(booking);
		flightGateway.updateSeats(updated.getFlightId(), businessDelta, nonBusinessDelta);

		NotificationRequest notification = new NotificationRequest();
		notification.setEventType("BOOKING_CANCELLED");
		notification.setEmailId(updated.getEmailId());
		notification.setPhoneNumber(updated.getPhoneNumber());
		notification.setPnr(updated.getPnr());
		notification.setNoOfSeats(updated.getNoOfSeats());
		notification.setFlightId(updated.getFlightId());
		notificationGateway.sendNotification(notification);
	}

	public Optional<Booking> getTicketDetails(String pnr) {
		return bookingRepository.findByPnr(pnr);
	}
}
