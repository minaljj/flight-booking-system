package com.flightapp.flight_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.flightapp.flight_service.dto.FlightSearchRequest;
import com.flightapp.flight_service.model.Flight;
import com.flightapp.flight_service.model.MealType;
import com.flightapp.flight_service.repository.FlightRepository;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

	@Mock
	private FlightRepository flightRepository;

	@InjectMocks
	private FlightService flightService;

	private Flight flight;

	@BeforeEach
	void setUp() {
		flight = new Flight();
		flight.setFlightNumber("VI345");
		flight.setAirline("Vistara");
		flight.setFrom("Haryana");
		flight.setTo("Pune");
		flight.setStartDateTime(LocalDateTime.of(2026, 7, 27, 4, 15));
		flight.setEndDateTime(LocalDateTime.of(2026, 7, 27, 7, 45));
		flight.setScheduledDays("Daily");
		flight.setInstrumentUsed("A320");
		flight.setTotalBusinessSeats(20);
		flight.setTotalNonBusinessSeats(160);
		flight.setMeal(MealType.VEG);
		flight.setTicketCost(4238.90);
	}

	@Test
	void testAddFlightSuccess() {
		when(flightRepository.existsByFlightNumber("VI345")).thenReturn(false);
		when(flightRepository.save(any(Flight.class))).thenReturn(flight);
		Flight result = flightService.addInventory(flight);
		assertNotNull(result);
		assertEquals("VI345", result.getFlightNumber());
		assertFalse(result.getIsBlocked());
		verify(flightRepository).save(flight);
	}

	@Test
	void testDuplicateFlightNumber() {
		when(flightRepository.existsByFlightNumber("VI345")).thenReturn(true);
		assertThrows(IllegalArgumentException.class, () -> {
			flightService.addInventory(flight);
		});
		verify(flightRepository, never()).save(any(Flight.class));
	}
	@Test
	void testSameFromAndTo() {
        flight.setTo("Haryana");
        when(flightRepository.existsByFlightNumber("VI345")).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> {
            flightService.addInventory(flight);
        });
        verify(flightRepository, never()).save(any(Flight.class));
    }
	@Test
	void testStartTimeandEndTime() {
	    flight.setStartDateTime(LocalDateTime.of(2026, 7, 27, 7, 45));
	    flight.setEndDateTime(LocalDateTime.of(2026, 7, 27, 4, 30));
	    when(flightRepository.existsByFlightNumber("VI345"))
	            .thenReturn(false);
	    assertThrows(IllegalArgumentException.class, () -> {
	        flightService.addInventory(flight);
	    });
	    verify(flightRepository, never())
	            .save(any(Flight.class));
	}
	@Test
	void testSearchFlights()
	{
		FlightSearchRequest request=new FlightSearchRequest();
			request.setFrom("Haryana");
			request.setTo("Pune");
			request.setDate("2026-05-08");
			List<Flight> flights=List.of(flight);
			when(flightRepository.findByFromIgnoreCaseAndToIgnoreCaseAndStartDateTimeBetween(
					eq("Haryana"),
					eq("Pune"),
					any(LocalDateTime.class),
					any(LocalDateTime.class))).thenReturn(flights);
			List<Flight> result=flightService.searchFlights(request);
			assertNotNull(result);
			assertEquals(1,result.size());
			assertEquals("VI345",result.get(0).getFlightNumber());
			
	}
	
}